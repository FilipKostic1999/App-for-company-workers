package com.example.company_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class fetchNameActivity : AppCompatActivity() {


    lateinit var database : FirebaseFirestore
    lateinit var nameInDatabase : username


    lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fetch_name)



        database = Firebase.firestore
        auth = Firebase.auth
        val user = auth.currentUser




        val sharedUserN = getSharedPreferences("userName", AppCompatActivity.MODE_PRIVATE)
        var userName = sharedUserN.getString("userName", "")





        if (user != null) {

            database.collection("users").document(user.uid)
                .collection("Data of user")

                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {
                        for (document in snapshot.documents) {

                            nameInDatabase = document.toObject()!!




                            Log.d("!!!", "${nameInDatabase.name}" )


                            userName = "${nameInDatabase.name}"



                                val editUserName = sharedUserN.edit()
                                editUserName.putString("userName", userName)
                                editUserName.commit()


                                val intent = Intent(this, WorkerProfile::class.java)
                                startActivity(intent)




                        }
                    }
                }

        }










    }





}