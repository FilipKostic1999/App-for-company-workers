package com.example.company_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

class Owner : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner)


        var bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationViewOwner)



        bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId) {


                R.id.company -> {
                    val   intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }

                R.id.worker -> {
                    val   intent = Intent(this, WorkerSignIn::class.java)
                    startActivity(intent)
                }



                else -> {



                }

            }

            true

        }







    }
}