package com.example.privacyapp

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.PortUnreachableException


/*
 * https://android.googlesource.com/platform/development/+/master/samples/ToyVpn/src/com/example/android/toyvpn
 * https://www.thegeekstuff.com/2014/06/android-vpn-service/
 * https://stackoverflow.com/questions/20237743/android-firewall-with-vpnservice
 * https://stackoverflow.com/questions/17766405/android-vpnservice-to-capture-packets-wont-capture-packets
 * https://stackoverflow.com/questions/30064823/a-firewall-for-android-with-vpnservice-responses-are-delivered-but-a-sockettim
 */


class NetworkAnalyzerVpnService : VpnService() {

    companion object {
        const val MAX_PACKET_SIZE: Int = Short.MAX_VALUE.toInt()
    }

    private val tag = this::class.qualifiedName

    private val builder = Builder()

    lateinit var thread: Thread

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        thread = Thread {

            val iface: ParcelFileDescriptor? = builder.setSession("NetworkAnalyzerVpnService")
                .addAddress("10.0.2.15", 24)
                .addRoute("0.0.0.0", 0)
                .addDnsServer("10.10.10.10")
                .establish()

            val inputStream = FileInputStream(iface!!.fileDescriptor)
            val outputStream = FileOutputStream(iface.fileDescriptor)

            val dnsSocket: DatagramSocket = DatagramSocket()
            protect(dnsSocket)

            val buffer = ByteArray(Short.MAX_VALUE.toInt())
            Log.d(tag, "Connecting")

            try {


                while (true) {

                    val length = inputStream.read(buffer)
                    if (length > 0) {

                        val data = buffer.copyOfRange(0, length)
                        val ippacket = IPPacket(data, 0, length)

                        if (ippacket.prot == 17 && ippacket.checkCheckSum() != 0) {

                            val udpRequestPacket: UDPPacket = UDPPacket(data, 0, length)


                            val ttl = udpRequestPacket.ttl
                            val sourceIP = udpRequestPacket.sourceIPz`
                            val destIP = udpRequestPacket.destIP
                            val sourcePort = udpRequestPacket.sourcePort
                            val destPort = udpRequestPacket.destPort
                            val version = udpRequestPacket.getVersion()
                            val clientID = IPPacket.int2ip(sourceIP).hostAddress + ":" + sourcePort

                            val hdrLen = udpRequestPacket.headerLength
                            val packetData = udpRequestPacket.getData()
                            val ipOffs = udpRequestPacket.ipPacketOffset
                            val offs = ipOffs + hdrLen
                            val len = udpRequestPacket.ipPacketLength - hdrLen

                            // build request datagram packet from UDP request packet
                            val request = DatagramPacket(packetData, offs, len)

                            // we can reuse the request data array
                            val response = DatagramPacket(packetData, offs, packetData.size - offs)

                            //forward request to DNS and receive response
                            //   DNSCommunicator.getInstance().requestDNS(request, response)

                            // patch the response by applying filter
                            //  val buf = DNSResponsePatcher.patchResponse(clientID, response.getData(), offs)

                            //create  UDP Header and update source and destination IP and port
                            val udp = UDPPacket.createUDPPacket(
                                response.data,
                                ipOffs,
                                hdrLen + response.length,
                                version
                            )

                            //for the response source and destination have to be switched
                            udp.updateHeader(ttl, 17, destIP, sourceIP)
                            udp.updateHeader(destPort, sourcePort)


                            outputStream.write(udp.getData(), udp.ipPacketOffset, udp.ipPacketLength)
                            outputStream.flush()


                            // Write the outgoing packet to the tunnel.
                        }
                    }

                    Thread.sleep(100)
                }
            } finally {
                inputStream.close()
                outputStream.close()
            }
        }
        thread.start()
        return START_STICKY
    }

    fun readInput(buffer: ByteArray, inputStream: FileInputStream, socket: DatagramSocket) {
        try {
            val inputLength = inputStream.read(buffer)
            if (inputLength > 0) {
//                var ipPacket: IpPacket?
//                try {
//                    ipPacket =
//                        IpV4Packet.newPacket(buffer.copyOfRange(0, buffer.size), 0, buffer.size) as IpPacket
//                } catch (ex: Exception) {
//                    Log.d(tag, "Discard Packet", ex)
//                    return
//                }
                val packet = DatagramPacket(
                    buffer.clone(), 0, inputLength
                )
                Log.d(tag, packet.address.toString())
                //val udpPacket = ipPacket.payload as UdpPacket
//                val outPacket = DatagramPacket(
//                    buffer,
//                    0,
//                    inputLength,
//                    ipPacket.header.dstAddr,
//                    udpPacket.header.dstPort.value().toInt()
//                )

                socket.send(packet)
            }
        } catch (ex: PortUnreachableException) {
            Log.d(tag, "Port Error", ex)
        }
    }

    override fun onDestroy() {

        thread.interrupt()
        super.onDestroy()
    }
}