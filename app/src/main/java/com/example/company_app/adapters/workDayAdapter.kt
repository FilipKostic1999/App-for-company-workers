package com.example.company_app.adapters

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.company_app.R
import com.example.company_app.classes.objectData

class workDayAdapter(private val manifestoList: ArrayList<objectData>) :
    RecyclerView.Adapter<workDayAdapter.workDayViewHolder>() {

    private var onDeleteClickListener: OnDeleteClickListener? = null
    private var onEditClickListener: OnEditClickListener? = null

    interface OnDeleteClickListener {
        fun onDeleteClick(manifesto: objectData)
    }

    interface OnEditClickListener {
        fun onEditClick(manifesto: objectData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): workDayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return workDayViewHolder(view)
    }

    override fun onBindViewHolder(holder: workDayViewHolder, position: Int) {
        val manifesto = manifestoList[position]
        holder.bind(manifesto)

    }

    override fun getItemCount(): Int {
        return manifestoList.size
    }

    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        onDeleteClickListener = listener
    }

    fun setOnEditClickListener(listener: OnEditClickListener) {
        onEditClickListener = listener
    }

    inner class workDayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bin: ImageView = itemView.findViewById(R.id.binImg)
        private val editImg: ImageView = itemView.findViewById(R.id.editImg)
        private val name: TextView = itemView.findViewById(R.id.tv_Name)
        private val hours: TextView = itemView.findViewById(R.id.tvHours)
        private val comment: TextView = itemView.findViewById(R.id.tvComment)
        private val date: TextView = itemView.findViewById(R.id.tv_date)

        fun bind(manifesto: objectData) {
            name.text = manifesto.userIdentity
            hours.text = manifesto.hours.toString()
            comment.text = manifesto.comment
            date.text = manifesto.date

            bin.setOnClickListener {
                showDeleteConfirmationDialog(manifesto)
            }
            editImg.setOnClickListener {
                showEditConfirmationDialog(manifesto)
            }
        }

        private fun showDeleteConfirmationDialog(manifesto: objectData) {
            val context = itemView.context

            if (isNetworkConnected()) {
                val alertDialogBuilder = AlertDialog.Builder(context)
                alertDialogBuilder.setTitle("Delete Confirmation")
                alertDialogBuilder.setMessage("Are you sure you want to delete this item?")
                alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                    onDeleteClickListener?.onDeleteClick(manifesto)
                }
                alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            } else {
                Toast.makeText(context, "No stable internet connection", Toast.LENGTH_SHORT).show()
            }
        }

        private fun showEditConfirmationDialog(manifesto: objectData) {
            val context = itemView.context

            if (isNetworkConnected()) {
                val alertDialogBuilder = AlertDialog.Builder(context)
                alertDialogBuilder.setTitle("Edit Confirmation")
                alertDialogBuilder.setMessage("Are you sure you want to edit this item?")
                alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                    onEditClickListener?.onEditClick(manifesto)
                }
                alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            } else {
                Toast.makeText(context, "No stable internet connection", Toast.LENGTH_SHORT).show()
            }
        }

        private fun isNetworkConnected(): Boolean {
            val connectivityManager = itemView.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }





    }
}
