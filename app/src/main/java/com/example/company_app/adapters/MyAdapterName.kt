package com.example.company_app.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.company_app.R
import com.example.company_app.classes.objectData
import com.example.company_app.classes.username

class MyAdapterName(private val nameList: ArrayList<username>) :
    RecyclerView.Adapter<MyAdapterName.nameViewHolder>() {

    private var onShowClickListener: OnShowClickListener? = null

    interface OnShowClickListener {
        fun onShowClick(name: username)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): nameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_itemname, parent, false)
        return nameViewHolder(view)
    }

    override fun onBindViewHolder(holder: nameViewHolder, position: Int) {
        val name = nameList[position]
        holder.bind(name)
    }

    override fun getItemCount(): Int {
        return nameList.size
    }

    fun setOnShowClickListener(listener: OnShowClickListener) {
        onShowClickListener = listener
    }

    inner class nameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameWorker: TextView = itemView.findViewById(R.id.tv_NameUser)

        fun bind(name: username) {
            nameWorker.text = name.name

            nameWorker.setOnClickListener {
                onShowClickListener?.onShowClick(name)
            }
        }


    }
}


