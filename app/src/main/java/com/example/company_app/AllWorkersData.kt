package com.example.company_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.company_app.classes.objectData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AllWorkersData : AppCompatActivity() {


    lateinit var putSyu: TextView


    lateinit var database: FirebaseFirestore


    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_workers_data)









        database = Firebase.firestore


        auth = Firebase.auth








        putSyu = findViewById(R.id.datacreate)


        val button2 = findViewById<Button>(R.id.datacreatebutton)





        button2.setOnClickListener {


            saveItem()


        }


    }


    fun saveItem() {


        val item = objectData(comment = putSyu.text.toString())


        putSyu.setText("")


        val user = auth.currentUser





        if (user == null) {


            return


        }





        database.collection("users").document(user.uid).collection("AllWorkersData").add(item)


            .addOnCompleteListener {


                Log.d("!!!", "add item")


            }


    }

}






















