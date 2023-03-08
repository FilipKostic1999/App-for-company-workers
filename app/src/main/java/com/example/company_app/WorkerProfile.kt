package com.example.company_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
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
    lateinit var workerProfLogOutBtn : Button


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
        workerProfLogOutBtn = findViewById(R.id.workerProfLogOutBtn)



        totalTextview.isVisible = false


        val sharedUserN = getSharedPreferences("userName", AppCompatActivity.MODE_PRIVATE)
        var userName = sharedUserN.getString("userName", "")


        personN.text = "$userName"
        var path = userName





        fetch()



        editNameImg.setOnClickListener {




            Toast.makeText(this, "Only administrator can edit names", Toast.LENGTH_SHORT).show()


        }




        workerProfLogOutBtn.setOnClickListener {

            userName = ""

            val editUserName = sharedUserN.edit()
            editUserName.putString("userName", userName)
            editUserName.commit()

            val intent = Intent(this, WorkerSignIn::class.java)
            startActivity(intent)


        }






        logOut.setOnClickListener {


             saveItem()

            Toast.makeText(this, "Saving post", Toast.LENGTH_SHORT).show()

            Handler().postDelayed({

                                  fetch()

            }, 2000)


        }










    }




    fun fetch() {


        val user = auth.currentUser
        val path = personN.text.toString()

        counter = 0
        calculator = 0.0
        listOfDocuments.clear()

        if (user != null && personN.text != null) {


            database.collection("users").document("Main")
                .collection("$path cronology")
                .orderBy("order", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {

                        objectDataItem = document.toObject()!!
                        listOfDocuments.add(objectDataItem)
                        counter += objectDataItem.preOrder
                        calculator += objectDataItem.hours
                        myAdapter.notifyDataSetChanged()
                        Log.d("!!!", "$path")
                        totalTextview.text = "$calculator"

                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("!!!", "Error getting documents: ")
                }

        }



    }




    fun saveItem() {

        var path = personN.text.toString()
        var dayOfWork = dateWork.text.toString()
        val finalHours = calculator + workHoursEditText.text.toString().toDouble()


        counter += 1

        var docNumberId : Int = counter
        var docNumberIdString = docNumberId.toString()
        var counterDouble = counter.toDouble()


        val item = objectData(comment = personC.text.toString(), hours = workHoursEditText.text.toString().toDouble(),
            totalHours = counterDouble, userIdentity = personN.text.toString(), order = counter, date = dayOfWork,
            preOrder = 1)



        listOfDocuments.clear()



         val user = auth.currentUser

        if (user != null) {

            database.collection("users").document("Main")
                .collection("$path Month").document(docNumberIdString).set(item)


                .addOnSuccessListener {

                    Toast.makeText(this, "Item correctly saved", Toast.LENGTH_SHORT).show()



                }


        }














        personC.text = "Add comment"
        workHoursEditText.text = "0"
        dateWork.text = "Date"


        calculator = 0.0
        counter = 0
        counterDouble = 0.0









        path = "$path cronology"


        database.collection("users").document("Main")
            .collection("$path").add(item)


            .addOnCompleteListener {


                Log.d("!!!", "item saved")


            }






        if (user == null) {
            return
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




    override fun onBackPressed() {
        Toast.makeText(this, "Use the logout button!", Toast.LENGTH_SHORT).show()
    }







}