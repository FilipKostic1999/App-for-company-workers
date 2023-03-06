package com.example.company_app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
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
    lateinit var deleteBtn : Button



    var calculator : Double = 0.0
    var counter : Int = 0
    var touches = 0

    var nameDataSearchText = ""
    var listOfCounters = ArrayList<Int>()



    lateinit var objectDataItem2 : objectData

    private lateinit var recyclerView: RecyclerView
    private lateinit var listOfDocuments2 : ArrayList<objectData>
    private lateinit var myAdapter: MyAdapter


    lateinit var names : username

    private lateinit var recyclerViewNames: RecyclerView
    private lateinit var listOfNames : ArrayList<username>
    private lateinit var myAdapterNames: MyAdapterName


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_show_recycleview)




        recyclerView = findViewById(R.id.recycleviewOwner)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)


        recyclerViewNames = findViewById(R.id.recyclerViewOwnerNames)
        recyclerViewNames.layoutManager = LinearLayoutManager(this)
        recyclerViewNames.setHasFixedSize(true)




        monthB = findViewById(R.id.monthB)
        calendarB = findViewById(R.id.calendarB)
        nameDataSearch = findViewById(R.id.nameDataSearch)
        totalOwnerHours = findViewById(R.id.totalOwnerHours)
        deleteBtn = findViewById(R.id.deleteBtn)



        listOfDocuments2 = arrayListOf()
        listOfDocuments2.clear()
        deleteBtn.isVisible = false
        deleteBtn.isEnabled = false
        myAdapter = MyAdapter(listOfDocuments2)
        recyclerView.adapter = myAdapter


        listOfNames = arrayListOf()
        listOfNames.clear()
        myAdapterNames = MyAdapterName(listOfNames)
        recyclerViewNames.adapter = myAdapterNames



        database = Firebase.firestore




        calendarB.setOnClickListener {

            listOfDocuments2.clear()
            deleteBtn.isVisible = false
            deleteBtn.isEnabled = false

            calculator = 0.0

            nameDataSearchText = nameDataSearch.text.toString()





                database.collection("users").document("Main")
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




            listOfDocuments2.clear()




        }










            database.collection("users").document("Main")
                .collection("Names")

                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {
                        for (document in snapshot.documents) {

                            names = document.toObject()!!


                            listOfNames.add(names)



                            myAdapterNames.notifyDataSetChanged()





                        }
                    }
                }







        deleteBtn.setOnClickListener {



            if (touches < 10) {

                touches ++

                Toast.makeText(this, "Touch 10 times", Toast.LENGTH_SHORT).show()

            }



            if (touches == 10) {

                deleteItems()


            }




        }













        monthB.setOnClickListener {


            calculator = 0.0
            listOfDocuments2.clear()
            deleteBtn.isVisible = true
            deleteBtn.isEnabled = true
            myAdapter.notifyDataSetChanged()

            nameDataSearchText = nameDataSearch.text.toString()





                database.collection("users").document("Main")
                    .collection("${nameDataSearchText} Month").orderBy("order", Query.Direction.DESCENDING)

                    .addSnapshotListener { snapshot, e ->
                        if (snapshot != null) {

                            for (document in snapshot.documents) {

                                objectDataItem2 = document.toObject()!!

                                listOfDocuments2.add(objectDataItem2)

                                calculator = calculator + objectDataItem2.hours
                                listOfCounters.add(objectDataItem2.order)
                                counter = objectDataItem2.order



                                    totalOwnerHours.text = "Total hours this month $calculator"





                                    myAdapter.notifyDataSetChanged()





                            }
                        }
                    }


            listOfDocuments2.clear()


        }



    }













    @SuppressLint("SuspiciousIndentation")
    fun deleteItems() {


        var highestCounter = listOfCounters.maxOrNull()

        if (highestCounter != null) {

            if (highestCounter > 0) {

                var numberSelector = 0

                while (numberSelector < 34) {


                    var path = nameDataSearch.text.toString()
                    var docNumberId : Int = highestCounter
                    var docNumberIdString = docNumberId.toString()






                    database.collection("users").document("Main")
                        .collection("$path Month").document(docNumberIdString).delete()


                        .addOnCompleteListener {


                            Log.d("!!!", "$docNumberId")


                        }






                    numberSelector ++


                    if (highestCounter > 0) {

                        highestCounter--



                    }



                }


                Toast.makeText(this, "Items deleted", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, OwnerShowRecycleview::class.java)
                startActivity(intent)

            }
        }





    }












}