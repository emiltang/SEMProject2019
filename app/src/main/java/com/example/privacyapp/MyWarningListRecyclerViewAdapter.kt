package com.example.privacyapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.privacyapp.model.PrivacyWarning
import kotlinx.android.synthetic.main.fragment_warninglist.view.*


class MyWarningListRecyclerViewAdapter : RecyclerView.Adapter<MyWarningListRecyclerViewAdapter.ViewHolder>() {

    private var _list: List<PrivacyWarning> = emptyList()
    var list: List<PrivacyWarning>
        get() = _list
        set(value) {
            _list = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.fragment_warninglist, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.item_number.text = list[position].app
        holder.itemView.content.text = list[position].description
    }

    override fun getItemCount() = list.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
