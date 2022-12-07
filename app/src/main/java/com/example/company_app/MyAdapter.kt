package com.example.company_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.FieldPosition

class MyAdapter(private val documentsList : ArrayList<objectData>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {






    override fun onCreateViewHolder(parent: ViewGroup, viewType : Int) : MyAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return MyViewHolder(itemView)
    }





    override fun onBindViewHolder (holder: MyAdapter.MyViewHolder, position: Int) {
        val document : objectData = documentsList[position]
        holder.comment.text = document.comment
        holder.hours.text = document.hours.toString()
        holder.totalHours.text = document.totalHours.toString()
        holder.name.text = document.userIdentity
        holder.dateInput.text = document.date
    }









    override fun getItemCount(): Int {

      return  documentsList.size

    }






    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val comment : TextView = itemView.findViewById(R.id.tvComment)
        val hours : TextView = itemView.findViewById(R.id.tvHours)
        val totalHours : TextView = itemView.findViewById(R.id.tvTotalHrs)
        val name : TextView = itemView.findViewById(R.id.tv_Name)
        val dateInput : TextView = itemView.findViewById(R.id.tv_date)
    }









}

