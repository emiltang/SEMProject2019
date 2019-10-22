package com.example.privacyapp

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel


/*
 * https://android.googlesource.com/platform/development/+/master/samples/ToyVpn/src/com/example/android/toyvpn
 * https://www.thegeekstuff.com/2014/06/android-vpn-service/
 * https://stackoverflow.com/questions/20237743/android-firewall-with-vpnservice
 * https://stackoverflow.com/questions/17766405/android-vpnservice-to-capture-packets-wont-capture-packets
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
                .addAddress("192.168.0.1", 24)
                .addDnsServer("8.8.8.8")
                .addRoute("0.0.0.0", 0)
                .establish()

            val inputStream = FileInputStream(iface!!.fileDescriptor)
            val outputStream = FileOutputStream(iface.fileDescriptor)

            val tunnel = DatagramChannel.open()
            tunnel.connect(InetSocketAddress(InetAddress.getLocalHost(), 8087))
            protect(tunnel.socket())
            val buffer = ByteBuffer.allocate(MAX_PACKET_SIZE)
            Log.d(tag, "Connecting")

            while (true) {

                val inputLength = inputStream.read(buffer.array())


                if (inputLength > 0) {
                    // Write the outgoing packet to the tunnel.
                    Log.d(tag, String(buffer.array()))

                    buffer.limit(inputLength)
                    tunnel.write(buffer)
                    buffer.clear()
                }

                val outputLength = tunnel.read(buffer)
                if (outputLength > 0) {
                    // Ignore control messages, which start with zero.
                    if (buffer.get(0) != 0.toByte()) {
                        // Write the incoming packet to the output stream.
                        outputStream.write(buffer.array(), 0, outputLength)
                    }
                    buffer.clear()
                }

                Thread.sleep(100)
            }

        }
        thread.start()
        return START_STICKY
    }

    override fun onDestroy() {

        thread.interrupt()
        super.onDestroy()
    }
}