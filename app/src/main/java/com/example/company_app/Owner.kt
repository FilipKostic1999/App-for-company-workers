package com.example.company_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView

class Owner : AppCompatActivity() {


    lateinit var directorPass : TextView
    lateinit var openb : Button

    var pass : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner)


        directorPass = findViewById(R.id.directorPass)
        openb = findViewById(R.id.openB)



        openb.setOnClickListener {
            pass = directorPass.text.toString().toInt()

            if (pass == 404090) {

                val intent = Intent(this, OwnerShowRecycleview:: class.java)
                startActivity(intent)

            }

        }




    }

    /*
    override fun onBackPressed() {

    }

     */



}