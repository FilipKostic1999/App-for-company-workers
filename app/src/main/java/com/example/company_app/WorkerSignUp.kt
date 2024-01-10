package com.example.company_app

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.example.company_app.classes.username
import com.example.company_app.databinding.ActivityWorkerSignUpBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.random.Random


class WorkerSignUp : AppCompatActivity() {

    private lateinit var binding: ActivityWorkerSignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWorkerSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        database = Firebase.firestore


        binding.backBtn.setOnClickListener {
            val intent = Intent(this, OwnerShowRecycleview::class.java)
            startActivity(intent)
        }




        binding.signUpWorkerBut.setOnClickListener {
            if (isInternetAvailable()) {
                val email = binding.workerSignUpEmailEditTexst.text.toString().trim()
                val pass = binding.workerSignUpPasEditTexst.text.toString().trim()
                val confirmPass = binding.workerSignUpPas2EditTexst.text.toString().trim()
                val name = binding.nameEt.text.toString()

                if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() && name.isNotEmpty()) {
                    if (pass == confirmPass) {
                        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // User account created successfully

                                    // Save user's name to Firestore
                                    val user = firebaseAuth.currentUser
                                    val userId = user?.uid

                                    /* Random code is generated to give any user a unique Id in the database
                                to avoid a possible same name user to fetch the same data of another
                                user with the same name
                                 */
                                    val timestamp = System.currentTimeMillis()
                                    val random = Random(timestamp)
                                    val codeID = String.format("%06d", random.nextInt(1000000))

                                    if (userId != null) {

                                        val userData = username(name, codeID, false)

                                        database.collection("Users")
                                            .document(user.uid).collection("user data")
                                            .document("$name $codeID").set(userData)

                                        database.collection("Director view")
                                            .document("$name $codeID").set(userData)


                                            .addOnSuccessListener {
                                                // Document successfully written
                                                Toast.makeText(
                                                    this,
                                                    "User registered",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                val intent = Intent(this, OwnerShowRecycleview::class.java)
                                                startActivity(intent)

                                            }
                                            .addOnFailureListener { e ->
                                                // Handle errors writing the document
                                                Toast.makeText(
                                                    this,
                                                    "Error saving user data: ${e.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }


                                    }
                                } else {
                                    // Account creation failed
                                    Toast.makeText(
                                        this,
                                        task.exception?.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "There are empty fields", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }





}
