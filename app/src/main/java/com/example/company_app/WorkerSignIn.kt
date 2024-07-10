package com.example.company_app

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
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
    private lateinit var plåtImg: ImageView

    lateinit var database : FirebaseFirestore
    lateinit var nameInDatabase : username
    var dataWorker = ""


    private lateinit var cardViewShake: CardView
    private val handler = Handler()

    private val shakeRunnable = object : Runnable {
        override fun run() {
            // Load shake animation
            val shakeAnimation = AnimationUtils.loadAnimation(this@WorkerSignIn, R.anim.shake)

            // Start animation on the cardViewShake
            cardViewShake.startAnimation(shakeAnimation)

            // Schedule the next shake after 3 seconds (3000 milliseconds)
            handler.postDelayed(this, 4000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        var auth = FirebaseAuth.getInstance()
        database = Firebase.firestore
        val user = auth.currentUser





        binding = ActivityWorkerSignInBinding.inflate(layoutInflater)

        setContentView(binding.root)







        firebaseAuth = FirebaseAuth.getInstance()



        binding.adminImg.setOnClickListener {
            showPasswordDialog()
        }





        binding.workerSignInButton.setOnClickListener {

            val email = binding.workerSignUpEmailEditTexst.text.toString()
            val pass = binding.workerSignUpPasEditTexst.text.toString()

            if (isInternetAvailable()) {
                if (email.isNotEmpty() && pass.isNotEmpty()) {
                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            val user = firebaseAuth.currentUser
                            if (user != null) {
                                database.collection("Users").document(user.uid)
                                    .collection("user data")
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        for (document in documents) {
                                            nameInDatabase = document.toObject()!!
                                            dataWorker = "${nameInDatabase.name} ${nameInDatabase.numberID}"
                                            val workerName = nameInDatabase.name
                                            val intent = Intent(this, WorkerProfile::class.java)
                                            intent.putExtra("workerId", "${nameInDatabase.numberID}")
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
                            Toast.makeText(this, authTask.exception.toString(), Toast.LENGTH_SHORT).show()
                            Log.d("!!!", authTask.exception.toString())
                        }
                    }
                } else {
                    Toast.makeText(this, "There are empty fields", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }

        plåtImg = findViewById(R.id.plåtImg)

        // Load the animation from XML
        val animation = AnimationUtils.loadAnimation(this, R.anim.circular_path_animation)

        // Apply the animation to the ImageView
        plåtImg.startAnimation(animation)


        // Initialize cardViewShake after setContentView
        cardViewShake = findViewById(R.id.cardViewShake)

        // Start the initial shake animation
        handler.post(shakeRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove any pending callbacks to prevent memory leaks
        handler.removeCallbacks(shakeRunnable)
    }





    // Function to check internet connectivity
    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }




    private fun showPasswordDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.password_dialog_layout, null)
        val passwordEditText: EditText = dialogView.findViewById(R.id.passwordEditText)
        val submitButton: Button = dialogView.findViewById(R.id.submitButton)
        val cancelButton: Button = dialogView.findViewById(R.id.cancelButton)

        val alertDialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)


        val alertDialog = alertDialogBuilder.create()

        submitButton.setOnClickListener {
            val enteredPassword = passwordEditText.text.toString()
            if (enteredPassword == "404090") {
                // Password is correct, navigate to the desired activity
                val intent = Intent(this, OwnerShowRecycleview::class.java)
                startActivity(intent)
                alertDialog.dismiss()
            } else {
                // Wrong password, show a message
                Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show()
            }
        }

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }






    override fun onBackPressed() {

    }








}