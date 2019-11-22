package com.example.privacyapp.ui

import android.content.Context
import android.content.pm.ApplicationInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.privacyapp.R
import kotlinx.android.synthetic.main.list_item.view.*


class RecyclerAdapter(
    private val context: Context,
    private val list: List<ApplicationInfo>,
    private val onClickListener: MyOnItemClickListener

) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    interface MyOnItemClickListener {
        fun onItemClicked(model: ApplicationInfo)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(context: Context, model: ApplicationInfo, clickListener: MyOnItemClickListener) {
            itemView.appIcon.setImageDrawable(model.loadIcon(context.packageManager))
            itemView.appTitle.text = context.packageManager.getApplicationLabel(model)
            itemView.setOnClickListener { clickListener.onItemClicked(model) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, list[position], onClickListener)
    }

}
