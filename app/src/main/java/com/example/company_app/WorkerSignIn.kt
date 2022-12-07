package com.example.company_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.company_app.databinding.ActivityWorkerSignInBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class WorkerSignIn : AppCompatActivity() {





    private lateinit var binding: ActivityWorkerSignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var navigationMenu : BottomNavigationView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)





        var auth = FirebaseAuth.getInstance()


        val user = auth.currentUser







        binding = ActivityWorkerSignInBinding.inflate(layoutInflater)

        setContentView(binding.root)







        firebaseAuth = FirebaseAuth.getInstance()


        binding.createA.setOnClickListener {
            val intent = Intent(this, WorkerSignUp:: class.java)
            startActivity(intent)
        }





        binding.workerSignInButton.setOnClickListener {


            val email = binding.workerSignUpEmailEditTexst.text.toString()
            val pass = binding.workerSignUpPasEditTexst.text.toString()






            if (email.isNotEmpty() && pass.isNotEmpty()) {


                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, WorkerProfile:: class.java)

                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "there are empty fields", Toast.LENGTH_SHORT).show()
            }

        }












        var bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationViewWorkerSignIn)




        bottomNavigationView.setOnItemSelectedListener {

            when (it.itemId) {


                R.id.company -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }

                R.id.owner -> {
                    val intent = Intent(this, Owner::class.java)
                    startActivity(intent)
                }


                else -> {


                }

            }

            true

        }








    }

    override fun onBackPressed() {

    }

}