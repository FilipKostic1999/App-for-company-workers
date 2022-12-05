package com.example.company_app

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.jar.Attributes.Name

class WorkerProfile : AppCompatActivity() {



    lateinit var logOut : Button
    lateinit var personC : TextView
    lateinit var personN : TextView
    lateinit var workHoursEditText : TextView
    lateinit var totalTextview : TextView

   lateinit var objectDataItem : objectData



    var selector : Int = 0
    var calculator : Double = 0.0



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

        EventChangeListener()





        var sharedN = getSharedPreferences("Name", AppCompatActivity.MODE_PRIVATE)
        var Name = sharedN.getString("Name", "")




         //  sharedN = getSharedPreferences("Name", AppCompatActivity.MODE_PRIVATE)
        //   Name = sharedN.getString("Name", "")




        // Esperienza = Esperienza + espBottino










        database = Firebase.firestore

        auth = Firebase.auth

        val user = auth.currentUser



        personC = findViewById(R.id.editTextTextPersonComment)
        personN = findViewById(R.id.editName)
        workHoursEditText = findViewById(R.id.workHoursEditText)
        totalTextview = findViewById(R.id.totalTextView)















        if (user != null) {
            database.collection("users").document(user.uid)
                .collection("Items")

                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {
                        for (document in snapshot.documents) {

                            objectDataItem = document.toObject()!!

                            listOfDocuments.add(objectDataItem)


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




    private fun EventChangeListener() {



    }





















    fun saveItem() {

           var path = personN.text.toString()

        val finalHours = calculator + workHoursEditText.text.toString().toDouble()

        val item = objectData(comment = personC.text.toString(), hours = workHoursEditText.text.toString().toDouble(),
            totalHours = finalHours, userIdentity = personN.text.toString())




        database.collection("users").document("MgSwnd5aNsOoB7FHxUBm146pktp1")
            .collection("$path").add(item)


            .addOnCompleteListener {


                Log.d("!!!", "item saved")


            }














        personC.text = ""
        workHoursEditText.text = ""


        calculator = 0.0


        val user = auth.currentUser


        if (user == null) {
            return
        }



        path = "$path long"


        database.collection("users").document("MgSwnd5aNsOoB7FHxUBm146pktp1")
            .collection("$path").add(item)


            .addOnCompleteListener {


                Log.d("!!!", "item saved")


            }







      /*  database.collection("users").document(user.uid).collection("Items").add(item)


            .addOnCompleteListener {


                Log.d("!!!", "item saved")


            } */













        item.totalHours = 0.0



    }






}