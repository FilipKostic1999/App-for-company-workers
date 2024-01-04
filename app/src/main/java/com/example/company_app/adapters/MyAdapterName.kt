package com.example.company_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.company_app.R
import com.example.company_app.classes.username

class MyAdapterName(private val documentsList : ArrayList<username>) : RecyclerView.Adapter<MyAdapterName.MyViewHolder>() {






    override fun onCreateViewHolder(parent: ViewGroup, viewType : Int) : MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_itemname, parent, false)

        return MyViewHolder(itemView)
    }





    override fun onBindViewHolder (holder: MyViewHolder, position: Int) {
        val document : username = documentsList[position]
        holder.name.text = document.name
    }









    override fun getItemCount(): Int {

      return  documentsList.size

    }






    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(R.id.tv_NameUser)
    }









}

