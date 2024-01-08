package com.example.company_app.adapters

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
                val context = itemView.context
                if (isNetworkConnected()) {
                    onShowClickListener?.onShowClick(name)
                } else {
                    Toast.makeText(context, "No stable internet connection", Toast.LENGTH_SHORT).show()
                }
            }
        }





        private fun isNetworkConnected(): Boolean {
            val connectivityManager = itemView.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }




    }
}


