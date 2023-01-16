package com.example.company_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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
    lateinit var editNameImg : ImageView


   lateinit var objectDataItem : objectData




    var calculator : Double = 0.0
    var counter : Int = 0
    private var touches = 0


    lateinit var database : FirebaseFirestore
    lateinit var nameInDatabase : username


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

        listOfDocuments.clear()







        database = Firebase.firestore
        auth = Firebase.auth
        val user = auth.currentUser





        personC = findViewById(R.id.editTextTextPersonComment)
        personN = findViewById(R.id.nameText)
        dateWork = findViewById(R.id.dayOfWork)
        workHoursEditText = findViewById(R.id.workHoursEditText)
        totalTextview = findViewById(R.id.totalTextView)
        logOut = findViewById(R.id.workerLogOut)
        editNameImg = findViewById(R.id.editNameImg)



        totalTextview.isVisible = false


        /*

        val sharedEdited = getSharedPreferences("isNameEdited", AppCompatActivity.MODE_PRIVATE)
        var isNameEdited = sharedEdited.getBoolean("isNameEdited", false)


         */

        var path = personN.text.toString()









        if (user != null && personN.text != null) {

            database.collection("users").document(user.uid)
                .collection("Data of user")

                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {
                        for (document in snapshot.documents) {

                            nameInDatabase = document.toObject()!!




                            Log.d("!!!", "${nameInDatabase.name}" )



                            personN.text = "${nameInDatabase.name}"
                            path = personN.text.toString()



                            /*

                            if (isNameEdited) {

                                isNameEdited = false

                                val editName = sharedEdited.edit()
                                editName.putBoolean("isNameEdited", isNameEdited)
                                editName.commit()


                                val intent = Intent(this, WorkerProfile::class.java)
                                startActivity(intent)


                            }

                            */





                            if (user != null && personN.text != null) {



                                database.collection("users").document("Main")
                                    .collection("$path cronology").orderBy("order", Query.Direction.DESCENDING)

                                    .addSnapshotListener { snapshot, e ->
                                        if (snapshot != null) {
                                            for (document in snapshot.documents) {

                                                objectDataItem = document.toObject()!!

                                                listOfDocuments.add(objectDataItem)


                                                counter += objectDataItem.preOrder
                                                calculator += objectDataItem.hours



                                                myAdapter.notifyDataSetChanged()

                                                Log.d("!!!", "$path" )



                                                totalTextview.text = "$calculator"


                                            }
                                        }
                                    }

                            }






                        }
                    }
                }



        }






        editNameImg.setOnClickListener {


            if (touches < 10) {

                touches ++

                Toast.makeText(this, "Touch 10 times", Toast.LENGTH_SHORT).show()

            }



            if (touches == 10) {

                val intent = Intent(this, createUsername::class.java)
                startActivity(intent)



            }




        }









        logOut.setOnClickListener {






             saveItem()


        }










    }






    fun saveItem() {

        var path = personN.text.toString()
        var dayOfWork = dateWork.text.toString()
        val finalHours = calculator + workHoursEditText.text.toString().toDouble()


        counter += 1

        var docNumberId : Int = counter
        var docNumberIdString = docNumberId.toString()


        val item = objectData(comment = personC.text.toString(), hours = workHoursEditText.text.toString().toDouble(),
            totalHours = finalHours, userIdentity = personN.text.toString(), order = counter, date = dayOfWork,
            preOrder = 1)



        listOfDocuments.clear()



         val user = auth.currentUser

        if (user != null) {

            database.collection("users").document("Main")
                .collection("$path Month").document(docNumberIdString).set(item)


                .addOnCompleteListener {


                    Log.d("!!!", "item saved")


                }


        }














        personC.text = ""
        workHoursEditText.text = ""
        dateWork.text = ""


        calculator = 0.0
        counter = 0






        if (user == null) {
            return
        }



        path = "$path cronology"


        database.collection("users").document("Main")
            .collection("$path").add(item)


            .addOnCompleteListener {


                Log.d("!!!", "item saved")


            }












/*

update

        database.collection("users").document(user.uid).collection("Itemsssss")
            .document("hmkogjk").set(item)


            .addOnCompleteListener {


                Log.d("!!!", "item saved")


            }

*/

       /* database.collection("users").document(user.uid).collection("ncidnvci").document("ku")
            .update(mapOf(
                "age" to 21,
                "favorites.color" to "Red"
            ))


*/






        item.totalHours = 0.0



    }






}