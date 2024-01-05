package com.example.company_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.example.company_app.classes.username
import com.example.company_app.databinding.ActivityWorkerSignInBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class WorkerSignIn : AppCompatActivity() {





    private lateinit var binding: ActivityWorkerSignInBinding
    lateinit var adminImg: ImageView
    private lateinit var firebaseAuth: FirebaseAuth


    lateinit var database : FirebaseFirestore
    lateinit var nameInDatabase : username
    var dataWorker = ""




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        var auth = FirebaseAuth.getInstance()
        database = Firebase.firestore
        val user = auth.currentUser







        binding = ActivityWorkerSignInBinding.inflate(layoutInflater)

        setContentView(binding.root)







        firebaseAuth = FirebaseAuth.getInstance()


        binding.createA.setOnClickListener {
            val intent = Intent(this, WorkerSignUp:: class.java)
            startActivity(intent)
        }


        binding.adminImg.setOnClickListener {
            val intent = Intent(this, Owner:: class.java)
            startActivity(intent)
        }





        binding.workerSignInButton.setOnClickListener {


            val email = binding.workerSignUpEmailEditTexst.text.toString()
            val pass = binding.workerSignUpPasEditTexst.text.toString()






            if (email.isNotEmpty() && pass.isNotEmpty()) {


                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {

                        if (user != null) {
                            database.collection("Users").document(user.uid)
                                .collection("user data")
                                .get()
                                .addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        nameInDatabase = document.toObject()!!
                                        dataWorker = "${nameInDatabase.name} ${nameInDatabase.numberID}"
                                        val workerName = nameInDatabase.name
                                        val intent = Intent(this, WorkerProfile:: class.java)
                                        intent.putExtra("dataWorker", dataWorker)
                                        intent.putExtra("workerName", workerName)
                                        startActivity(intent)
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(this, "Log in failed", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "The user is null", Toast.LENGTH_SHORT).show()
                        }


                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        Log.d("!!!", it.exception.toString())
                    }
                }
            } else {
                Toast.makeText(this, "There are empty fields", Toast.LENGTH_SHORT).show()
            }

        }


    }

    override fun onBackPressed() {

    }

}