package com.example.company_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        var bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)



        bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId) {


                R.id.owner -> {
                    val   intent = Intent(this, AllWorkersData::class.java)
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


    override fun onBackPressed() {
        Toast.makeText(applicationContext, "This action is", Toast.LENGTH_SHORT).show()
    }






}