package com.example.company_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class createUsername : AppCompatActivity() {



    lateinit var  createNameEdit : TextView
    lateinit var nameInDatabase : username

    lateinit var saveNameBtn : Button



    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var database : FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_username)



        saveNameBtn = findViewById(R.id.saveNameBtn)
        createNameEdit = findViewById(R.id.createNameEdit)



        firebaseAuth = FirebaseAuth.getInstance()
        database = Firebase.firestore
        firebaseAuth = Firebase.auth


        val user = firebaseAuth.currentUser




        if (user != null) {

            database.collection("users").document(user.uid)
                .collection("Data of user")

                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {
                        for (document in snapshot.documents) {

                            nameInDatabase = document.toObject()!!





                            createNameEdit.text = "${nameInDatabase.name}"


                        }

                    }

                }


        }



















        saveNameBtn.setOnClickListener {


            saveName()

            val intent = Intent(this, WorkerSignIn::class.java)
            startActivity(intent)



        }





    }








    fun saveName() {




        val user = firebaseAuth.currentUser


        val name = username(name = createNameEdit.text.toString())


        if (user != null) {

            database.collection("users").document(user.uid)
                .collection("Data of user").document("Username").set(name)


                .addOnCompleteListener {


                    Log.d("!!!", "item saved")


                }

        }










    }


    override fun onBackPressed() {
        Toast.makeText(this, "Confirm your username", Toast.LENGTH_SHORT).show()
    }










}