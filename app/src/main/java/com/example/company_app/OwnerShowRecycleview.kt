package com.example.company_app

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class OwnerShowRecycleview : AppCompatActivity() {


    lateinit var database : FirebaseFirestore

    lateinit var monthB : ImageView
    lateinit var calendarB : ImageView
    lateinit var nameDataSearch : TextView
    lateinit var totalOwnerHours : TextView



    var calculator : Double = 0.0

    var nameDataSearchText = ""



    lateinit var objectDataItem2 : objectData

    private lateinit var recyclerView: RecyclerView
    private lateinit var listOfDocuments2 : ArrayList<objectData>
    private lateinit var myAdapter: MyAdapter

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_show_recycleview)




        recyclerView = findViewById(R.id.recycleviewOwner)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)



        monthB = findViewById(R.id.monthB)
        calendarB = findViewById(R.id.calendarB)
        nameDataSearch = findViewById(R.id.nameDataSearch)
        totalOwnerHours = findViewById(R.id.totalOwnerHours)



        listOfDocuments2 = arrayListOf()
        listOfDocuments2.clear()
        myAdapter = MyAdapter(listOfDocuments2)
        recyclerView.adapter = myAdapter




        database = Firebase.firestore




        calendarB.setOnClickListener {

            listOfDocuments2.clear()

            calculator = 0.0

            nameDataSearchText = nameDataSearch.text.toString()





                database.collection("users").document("MgSwnd5aNsOoB7FHxUBm146pktp1")
                    .collection("${nameDataSearchText} cronology").orderBy("order", Query.Direction.DESCENDING)

                    .addSnapshotListener { snapshot, e ->
                        if (snapshot != null) {

                            for (document in snapshot.documents) {

                                objectDataItem2 = document.toObject()!!


                                    listOfDocuments2.add(objectDataItem2)

                                    calculator = calculator + objectDataItem2.hours


                                    totalOwnerHours.text = "Total hours in cronology $calculator"






                                    myAdapter.notifyDataSetChanged()







                            }
                        }
                    }









        }


        monthB.setOnClickListener {


            calculator = 0.0
            listOfDocuments2.clear()

            nameDataSearchText = nameDataSearch.text.toString()





                database.collection("users").document("MgSwnd5aNsOoB7FHxUBm146pktp1")
                    .collection("${nameDataSearchText}").orderBy("order", Query.Direction.DESCENDING)

                    .addSnapshotListener { snapshot, e ->
                        if (snapshot != null) {

                            for (document in snapshot.documents) {

                                objectDataItem2 = document.toObject()!!

                                listOfDocuments2.add(objectDataItem2)

                                calculator = calculator + objectDataItem2.hours


                                    totalOwnerHours.text = "Total hours this month $calculator"





                                    myAdapter.notifyDataSetChanged()





                            }
                        }
                    }





        }



    }
}