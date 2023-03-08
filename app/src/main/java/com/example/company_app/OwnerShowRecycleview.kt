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


    lateinit var database: FirebaseFirestore

    lateinit var monthB: ImageView
    lateinit var calendarB: ImageView
    lateinit var nameDataSearch: TextView
    lateinit var totalOwnerHours: TextView
    lateinit var fromEditTxt: TextView
    lateinit var toEditTxt: TextView
    lateinit var deleteDayBtn: Button


    var calculator: Double = 0.0
    var counter: Int = 0
    var touches = 0

    var nameDataSearchText = ""
    var listOfCounters = ArrayList<Int>()


    lateinit var objectDataItem2: objectData

    private lateinit var recyclerView: RecyclerView
    private lateinit var listOfDocuments2: ArrayList<objectData>
    private lateinit var myAdapter: MyAdapter


    lateinit var names: username

    private lateinit var recyclerViewNames: RecyclerView
    private lateinit var listOfNames: ArrayList<username>
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
        deleteDayBtn = findViewById(R.id.deleteDayBtn)
        toEditTxt = findViewById(R.id.toEditTxt)
        fromEditTxt = findViewById(R.id.fromEditTxt)



        listOfDocuments2 = arrayListOf()
        listOfDocuments2.clear()
        deleteDayBtn.isEnabled = false
        myAdapter = MyAdapter(listOfDocuments2)
        recyclerView.adapter = myAdapter


        listOfNames = arrayListOf()
        listOfNames.clear()
        myAdapterNames = MyAdapterName(listOfNames)
        recyclerViewNames.adapter = myAdapterNames



        database = Firebase.firestore








        database.collection("users").document("Main")
            .collection("Names")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                    names = document.toObject()!!
                    listOfNames.add(names)
                    myAdapterNames.notifyDataSetChanged()

                }
            }
            .addOnFailureListener { exception ->
                Log.d("!!!", "Error getting documents: ")
            }







        calendarB.setOnClickListener {


            listOfDocuments2.clear()
            deleteDayBtn.isEnabled = false
            calculator = 0.0

            nameDataSearchText = nameDataSearch.text.toString()


            database.collection("users").document("Main")
                .collection("$nameDataSearchText cronology")
                .orderBy("order", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {

                        objectDataItem2 = document.toObject()!!
                        listOfDocuments2.add(objectDataItem2)
                        calculator += objectDataItem2.hours
                        totalOwnerHours.text = "Total hours in cronology $calculator"
                        myAdapter.notifyDataSetChanged()

                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("!!!", "Error getting documents: ")
                }


        }





        deleteDayBtn.setOnClickListener {

            deleteFrom()


        }






        monthB.setOnClickListener {


            calculator = 0.0
            listOfDocuments2.clear()
            deleteDayBtn.isEnabled = true
            myAdapter.notifyDataSetChanged()

            nameDataSearchText = nameDataSearch.text.toString()



            database.collection("users").document("Main")
                .collection("$nameDataSearchText Month")
                .orderBy("order", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {

                        objectDataItem2 = document.toObject()!!
                        listOfDocuments2.add(objectDataItem2)
                        calculator += objectDataItem2.hours
                        totalOwnerHours.text = "Total hours this month $calculator"
                        myAdapter.notifyDataSetChanged()

                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("!!!", "Error getting documents: ")
                }


        }


    }








    fun deleteFrom() {

        var from = fromEditTxt.text.toString().toInt()
        var to = toEditTxt.text.toString().toInt()
        var path = nameDataSearch.text.toString()
        var docNumberId: Int = from
        var docNumberIdString = docNumberId.toString()
        var onSuccessCounter = 0



        if (to >= from) {

            while (true) {

                database.collection("users").document("Main")
                    .collection("$path Month").document(docNumberIdString).delete()


                    .addOnCompleteListener {

                        onSuccessCounter++

                    }

                from++
                docNumberId = from
                docNumberIdString = docNumberId.toString()


                if (from > to) {
                    onSuccessCounter = 0
                    break
                }


            }
        } else if (to < from) {
            Toast.makeText(this, "from smaller number to higher only!", Toast.LENGTH_SHORT).show()
        }


    }

}