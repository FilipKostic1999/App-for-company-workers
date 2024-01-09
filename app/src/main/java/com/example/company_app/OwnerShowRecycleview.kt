package com.example.company_app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.company_app.adapters.MyAdapterName
import com.example.company_app.adapters.workDayAdapter
import com.example.company_app.classes.objectData
import com.example.company_app.classes.username
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class OwnerShowRecycleview : AppCompatActivity(), MyAdapterName.OnShowClickListener, MyAdapterName.OnDeleteUserClickListener {


    lateinit var database: FirebaseFirestore



    private lateinit var recyclerViewNames: RecyclerView
    private lateinit var listOfNames: ArrayList<username>
    private lateinit var myAdapterNames: MyAdapterName
    lateinit var name: username


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_show_recycleview)







        recyclerViewNames = findViewById(R.id.recyclerViewOwnerNames)
        recyclerViewNames.layoutManager = LinearLayoutManager(this)
        recyclerViewNames.setHasFixedSize(true)






        listOfNames = arrayListOf()
        listOfNames.clear()
        myAdapterNames = MyAdapterName(listOfNames)
        recyclerViewNames.adapter = myAdapterNames
        myAdapterNames.setOnShowClickListener(this)
        myAdapterNames.setOnDeleteUserClickListener(this)



        database = Firebase.firestore




            database.collection("Director view")
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {
                        listOfNames.clear()
                        myAdapterNames.notifyDataSetChanged()
                        for (document in snapshot.documents) {
                            name = document.toObject()!!
                            listOfNames.add(name)
                        }
                        myAdapterNames.notifyDataSetChanged()
                    }
                }






    }



    override fun onShowClick(name: username) {
        val selectedName = name.name
        val userId = name.numberID
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("selectedName", selectedName)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }



    override fun onDeleteUserClick(name: username) {

        var isAccountDisabled = name.isAccountDisabled

        if (isAccountDisabled) {
            name.isAccountDisabled = false
            database.collection("Director view")
                .document("${name.name} ${name.numberID}").set(name)
                .addOnSuccessListener {
                    Toast.makeText(this, "${name.name}`s account enabled", Toast.LENGTH_SHORT).show()
                }
        } else if (!isAccountDisabled) {
            name.isAccountDisabled = true
            database.collection("Director view")
                .document("${name.name} ${name.numberID}").set(name)
                .addOnSuccessListener {
                    Toast.makeText(this, "${name.name}`s account disabled", Toast.LENGTH_SHORT).show()
                }
        }


    }





}