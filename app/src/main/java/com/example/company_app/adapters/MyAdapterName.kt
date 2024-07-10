package com.example.company_app.adapters

import android.content.Context
import android.net.ConnectivityManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.company_app.R
import com.example.company_app.classes.objectData
import com.example.company_app.classes.username

class MyAdapterName(private val nameList: ArrayList<username>) :
    RecyclerView.Adapter<MyAdapterName.nameViewHolder>() {

    private var onShowClickListener: OnShowClickListener? = null
    private var onDeleteUserClickListener: OnDeleteUserClickListener? = null

    interface OnShowClickListener {
        fun onShowClick(name: username)
    }

    interface OnDeleteUserClickListener {
        fun onDeleteUserClick(name: username)
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

    fun setOnDeleteUserClickListener(listener: OnDeleteUserClickListener) {
        onDeleteUserClickListener = listener
    }

    inner class nameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameWorker: TextView = itemView.findViewById(R.id.tv_NameUser)
        private val viewImg: ImageView = itemView.findViewById(R.id.viewImg)
        private val deleteUser: Button = itemView.findViewById(R.id.deleteUser)

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
            viewImg.setOnClickListener {
                val context = itemView.context
                if (isNetworkConnected()) {
                    onShowClickListener?.onShowClick(name)
                } else {
                    Toast.makeText(context, "No stable internet connection", Toast.LENGTH_SHORT).show()
                }
            }

            if (name.isAccountDisabled) {
                deleteUser.text = "Unblock user"
            } else {
                deleteUser.text = "Block user"
            }

            deleteUser.setOnClickListener {
                val context = itemView.context
                if (isNetworkConnected()) {
                    onDeleteUserClickListener?.onDeleteUserClick(name)
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


