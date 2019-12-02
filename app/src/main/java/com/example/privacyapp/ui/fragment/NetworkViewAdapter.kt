package com.example.privacyapp.ui.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.privacyapp.R
import com.example.privacyapp.model.NetworkActivityRecord
import kotlinx.android.synthetic.main.network_list_item.view.*


class NetworkViewAdapter(var list: List<NetworkActivityRecord>) :
    RecyclerView.Adapter<NetworkViewAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.network_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.time.text = list[position].endTime.toString()
        holder.itemView.downBytes.text = list[position].downBytes.toString()
        holder.itemView.upBytes.text = list[position].upBytes.toString()
    }
}