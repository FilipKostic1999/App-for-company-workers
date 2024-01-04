package com.example.company_app.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.company_app.R
import com.example.company_app.classes.objectData

class workDayAdapter(private val manifestoList: ArrayList<objectData>) :
    RecyclerView.Adapter<workDayAdapter.workDayViewHolder>() {

    private var onDeleteClickListener: OnDeleteClickListener? = null

    interface OnDeleteClickListener {
        fun onDeleteClick(manifesto: objectData)
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

    inner class workDayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bin: ImageView = itemView.findViewById(R.id.binImg)
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
        }

        private fun showDeleteConfirmationDialog(manifesto: objectData) {
            val context = itemView.context
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
        }
    }
}
