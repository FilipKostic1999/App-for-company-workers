package com.example.company_app

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import java.util.jar.Attributes.Name

class WorkerProfile : AppCompatActivity() {



    lateinit var logOut : Button
    lateinit var personC : TextView
    lateinit var personN : TextView
    lateinit var dateWork : TextView
    lateinit var workHoursEditText : TextView
    lateinit var totalTextview : TextView
    lateinit var counterText : TextView


   lateinit var objectDataItem : objectData




    var calculator : Double = 0.0
    var counter : Int = 0


    lateinit var database : FirebaseFirestore


    lateinit var auth : FirebaseAuth




    private lateinit var recyclerView: RecyclerView
    private lateinit var listOfDocuments : ArrayList<objectData>
    private lateinit var myAdapter: MyAdapter





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker_profile)


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)


        listOfDocuments = arrayListOf()

        myAdapter = MyAdapter(listOfDocuments)



        recyclerView.adapter = myAdapter








        var sharedN = getSharedPreferences("Name", AppCompatActivity.MODE_PRIVATE)
        var Name = sharedN.getString("Name", "")








        database = Firebase.firestore

        auth = Firebase.auth

        val user = auth.currentUser



        personC = findViewById(R.id.editTextTextPersonComment)
        personN = findViewById(R.id.editName)
        dateWork = findViewById(R.id.dayOfWork)
        counterText = findViewById(R.id.counterText)
        workHoursEditText = findViewById(R.id.workHoursEditText)
        totalTextview = findViewById(R.id.totalTextView)

        totalTextview.isVisible = false




        counterText.isVisible = false





        if (personN.text != null) {
            personN.text = "$Name"
        }







        if (user != null && personN.text != null) {
            database.collection("users").document("MgSwnd5aNsOoB7FHxUBm146pktp1")
                .collection("${personN.text} cronology").orderBy("order", Query.Direction.DESCENDING)

                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {
                        for (document in snapshot.documents) {

                            objectDataItem = document.toObject()!!

                            listOfDocuments.add(objectDataItem)


                                counter = counter + objectDataItem.preOrder
                                calculator = calculator + objectDataItem.hours
                                Log.d("!!!", "$calculator" )


                            myAdapter.notifyDataSetChanged()



                            totalTextview.text = "$calculator"


                        }
                    }
                }

              }























        logOut = findViewById(R.id.workerLogOut)

        logOut.setOnClickListener {


            listOfDocuments.clear()


            saveItem()

            Name = personN.text.toString()

            val editName = sharedN.edit()
            editName.putString("Name", Name)
            editName.commit()

            personN.text = "$Name"




        }










    }


    fun saveItem() {

        var path = personN.text.toString()
        var dayOfWork = dateWork.text.toString()
        val finalHours = calculator + workHoursEditText.text.toString().toDouble()


      var IntCounterText = counter + counterText.text.toString().toInt()

        val item = objectData(comment = personC.text.toString(), hours = workHoursEditText.text.toString().toDouble(),
            totalHours = finalHours, userIdentity = personN.text.toString(), order = IntCounterText, date = dayOfWork,
        preOrder = counterText.text.toString().toInt())




        database.collection("users").document("MgSwnd5aNsOoB7FHxUBm146pktp1")
            .collection("$path").add(item)


            .addOnCompleteListener {


                Log.d("!!!", "item saved")


            }














        personC.text = ""
        workHoursEditText.text = ""
        dateWork.text = "Date"


        calculator = 0.0
        counter = 0


        val user = auth.currentUser


        if (user == null) {
            return
        }



        path = "$path cronology"


        database.collection("users").document("MgSwnd5aNsOoB7FHxUBm146pktp1")
            .collection("$path").add(item)


            .addOnCompleteListener {


                Log.d("!!!", "item saved")


            }







     /*   database.collection("users").document(user.uid).collection("Items").add(item)


            .addOnCompleteListener {


                Log.d("!!!", "item saved")


            } */













        item.totalHours = 0.0



    }






}