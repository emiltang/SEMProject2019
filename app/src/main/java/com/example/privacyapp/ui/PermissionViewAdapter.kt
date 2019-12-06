package com.example.privacyapp.ui

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.privacyapp.R
import kotlinx.android.synthetic.main.permission_list_item.view.*

class PermissionViewAdapter(
    private val context: Context,
    private val application: ApplicationInfo,
    private val permissions: Array<String>,
    private val listener: OnListFragmentInteractionListener
) : RecyclerView.Adapter<PermissionViewAdapter.ViewHolder>() {

    interface OnListFragmentInteractionListener {
        fun onItemClicked(item: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.permission_list_item, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(permissions[position], listener)

    override fun getItemCount() = permissions.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(model: String, listener: OnListFragmentInteractionListener) {
            val status = context.packageManager.checkPermission(model, application.packageName)
            itemView.status.text = when (status) {
                PackageManager.PERMISSION_GRANTED -> "Granted"
                PackageManager.PERMISSION_DENIED -> "Denied"
                else -> "Error"
            }
            itemView.permission.text = model
            itemView.setOnClickListener { listener.onItemClicked(model) }
        }
    }
}
