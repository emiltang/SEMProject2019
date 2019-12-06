package com.example.privacyapp.ui

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.privacyapp.R
import com.example.privacyapp.model.NetworkActivityRecord
import kotlinx.android.synthetic.main.network_list_item.view.*

class NetworkViewAdapter(val context: Context) :
    RecyclerView.Adapter<NetworkViewAdapter.ViewHolder>() {

    // list backing field
    private var _list: List<NetworkActivityRecord> = emptyList()

    // list property
    var list: List<NetworkActivityRecord>
        get() = _list // return _list
        set(value) {
            _list = value.reversed()
            notifyDataSetChanged()
        }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.network_list_item, parent, false)
    )

    override fun getItemCount() = _list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = _list[position]
        val date = DateUtils.getRelativeTimeSpanString(item.endTime)
        holder.itemView.time.text = "Date: $date"
        holder.itemView.downBytes.text = "Download: ${item.downBytes} Bytes"
        holder.itemView.upBytes.text = "Upload: ${item.upBytes} Bytes"
    }
}
