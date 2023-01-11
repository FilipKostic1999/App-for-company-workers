package com.example.company_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.example.company_app.databinding.ActivityWorkerSignUpBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class WorkerSignUp : AppCompatActivity() {

    private lateinit var binding: ActivityWorkerSignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    lateinit var database : FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWorkerSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()




        database = Firebase.firestore
        firebaseAuth = Firebase.auth









        binding.signInTxt.setOnClickListener {


            val intent = Intent(this, WorkerSignIn::class.java)

            startActivity(intent)
        }










        binding.signUpWorkerBut.setOnClickListener {
            val email = binding.workerSignUpEmailEditTexst.text.toString()
            val pass = binding.workerSignUpPasEditTexst.text.toString()
            val confirmPass = binding.workerSignUpPas2EditTexst.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {

                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {




                            val intent = Intent(this, createUsername::class.java)

                            startActivity(intent)



                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Pasword does not mach", Toast.LENGTH_SHORT).show()
                }


            } else {
                Toast.makeText(this, "there are empty fields", Toast.LENGTH_SHORT).show()
            }












        }
    }
}