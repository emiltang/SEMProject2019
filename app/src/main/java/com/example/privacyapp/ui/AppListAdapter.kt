package com.example.privacyapp.ui

import android.content.Context
import android.content.pm.ApplicationInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.privacyapp.R
import kotlinx.android.synthetic.main.app_list_item.view.*


class AppListAdapter(
    private val context: Context,
    private val list: List<ApplicationInfo>,
    private val listener: AppListItemClickListener
) : RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

    /**
     * List item callback interface
     */
    interface AppListItemClickListener {
        fun onItemClicked(model: ApplicationInfo)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(context: Context, model: ApplicationInfo, clickListener: AppListItemClickListener) {
            itemView.appIcon.setImageDrawable(model.loadIcon(context.packageManager))
            itemView.appTitle.text = context.packageManager.getApplicationLabel(model)
            itemView.setOnClickListener { clickListener.onItemClicked(model) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        view = LayoutInflater.from(context).inflate(R.layout.app_list_item, parent, false)
    )

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(context, list[position], listener)
}
