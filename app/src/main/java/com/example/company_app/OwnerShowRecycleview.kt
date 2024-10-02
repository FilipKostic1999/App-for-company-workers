package com.example.company_app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.company_app.adapters.MyAdapterName
import com.example.company_app.classes.userData
import com.example.company_app.classes.username
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class OwnerShowRecycleview : AppCompatActivity(), MyAdapterName.OnShowClickListener, MyAdapterName.OnDeleteUserClickListener {


    lateinit var database: FirebaseFirestore



    private lateinit var recyclerViewNames: RecyclerView
    private lateinit var listOfUsers: ArrayList<userData>
    private lateinit var myAdapterNames: MyAdapterName
    lateinit var user: userData
    lateinit var plusImg: ImageView


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_show_recycleview)




        plusImg = findViewById(R.id.plusImg)



        recyclerViewNames = findViewById(R.id.recyclerViewOwnerNames)
        recyclerViewNames.layoutManager = LinearLayoutManager(this)
        recyclerViewNames.setHasFixedSize(true)






        listOfUsers = arrayListOf()
        listOfUsers.clear()
        myAdapterNames = MyAdapterName(listOfUsers)
        recyclerViewNames.adapter = myAdapterNames
        myAdapterNames.setOnShowClickListener(this)
        myAdapterNames.setOnDeleteUserClickListener(this)



        database = Firebase.firestore




            database.collection("Users")
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {
                        listOfUsers.clear()
                        myAdapterNames.notifyDataSetChanged()
                        for (document in snapshot.documents) {
                            user = document.toObject()!!
                            if (user.isBlocked == false) {
                                listOfUsers.add(user)
                            }
                        }
                        for (document in snapshot.documents) {
                            user = document.toObject()!!
                            if (user.isBlocked == true) {
                                listOfUsers.add(user)
                            }
                        }
                        myAdapterNames.notifyDataSetChanged()
                    }
                }



        plusImg.setOnClickListener {
            val intent = Intent(this, WorkerSignUp::class.java)
            startActivity(intent)
        }




    }


    override fun onBackPressed() {
        val intent = Intent(this, WorkerSignIn::class.java)
        startActivity(intent)
    }



    override fun onShowClick(name: userData) {
        val selectedName = name.name
        val userId = name.userUID
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("selectedName", selectedName)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }



    override fun onDeleteUserClick(name: userData) {

        var isAccountDisabled = name.isBlocked

        if (isAccountDisabled) {
            name.isBlocked = false
            database.collection("Users")
                .document(name.userUID).set(name)
                .addOnSuccessListener {
                    Toast.makeText(this, "${name.name}`s account enabled", Toast.LENGTH_SHORT).show()
                }
        } else if (!isAccountDisabled) {
            name.isBlocked = true
            database.collection("Users")
                .document(name.userUID).set(name)
                .addOnSuccessListener {
                    Toast.makeText(this, "${name.name}`s account disabled", Toast.LENGTH_SHORT).show()
                }
        }


    }





}