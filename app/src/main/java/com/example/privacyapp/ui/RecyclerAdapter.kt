package com.example.privacyapp.ui

import android.content.Context
import android.content.pm.ApplicationInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.privacyapp.R
import kotlinx.android.synthetic.main.list_item.view.*

class RecyclerAdapter(private val context: Context, private val list: List<ApplicationInfo>) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            view = LayoutInflater.from(
                context
            ).inflate(R.layout.list_item, parent, false)
        )

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.appIcon.setImageDrawable(list[position].loadIcon(context.packageManager))
        holder.itemView.appTitle.text = context.packageManager.getApplicationLabel(list[position])
    }
}
