package com.example.company_app

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.company_app.classes.objectData
import com.example.company_app.classes.username
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale

class changeName : AppCompatActivity() {


    lateinit var auth : FirebaseAuth
    lateinit var database : FirebaseFirestore
    private lateinit var listOfDocuments : ArrayList<objectData>
    lateinit var objectDataItem: objectData


    lateinit var editNameEt: TextView
    lateinit var editNameBtn: Button

    var workerId = ""
    var workerName = ""
    var isAccountEnabled = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_name)


        editNameEt = findViewById(R.id.editNameEt)
        editNameBtn = findViewById(R.id.editNameBtn)



        auth = Firebase.auth
        val user = auth.currentUser
        database = Firebase.firestore
        listOfDocuments = arrayListOf()


        var documentPath = intent.getStringExtra("documentPath")!!
        workerId = intent.getStringExtra("workerIdNumber")!!
        workerName = intent.getStringExtra("nameWorker")!!
        editNameEt.text = workerName





        if (user != null) {
            database.collection("Director view").document(documentPath)
                .collection("Days")
                .get()
                .addOnSuccessListener { documents ->
                        for (document in documents) {
                            objectDataItem = document.toObject()
                            listOfDocuments.add(objectDataItem)
                        }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Log in failed", Toast.LENGTH_SHORT).show()
                }
        }



        if (user != null) {
            database.collection("Director view")
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {
                        for (document in snapshot.documents) {
                            val file = document.toObject<username>()!!
                            if (file.name == workerName && file.numberID == workerId && file.isAccountDisabled) {
                                isAccountEnabled = false
                            } else if (file.name == workerName && file.numberID == workerId && file.isAccountDisabled == false) {
                                isAccountEnabled = true
                            }
                        }
                    }
                }
        }






        editNameBtn.setOnClickListener {
            if (isInternetAvailable()) {
                if (isAccountEnabled) {
                    editName()
                } else {
                    Toast.makeText(this, "Your account is disabled", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }








    }


    override fun onBackPressed() {

    }



    fun editName() {

        auth = Firebase.auth
        val user = auth.currentUser


        val newName = editNameEt.text.toString()

        if (newName != workerName) {

            for (document in listOfDocuments) {
                document.userIdentity = newName
            }


            if (user != null) {
                val newUserId = username(newName, workerId, false)
                database.collection("Director view")
                    .document("$newName $workerId").set(newUserId)
                    .addOnSuccessListener {

                        Toast.makeText(this, "New user id created", Toast.LENGTH_SHORT).show()

                        // Save all users documents with updated name

                        for (document in listOfDocuments) {
                            val selectedDateNumbers = document.date!!.replace(Regex("[^0-9]"), "")
                            database.collection("Director view")
                                .document("$newName $workerId")
                                .collection("Days").document(selectedDateNumbers).set(document)
                                .addOnSuccessListener {
                                    // Toast.makeText(this, "Item correctly saved", Toast.LENGTH_SHORT).show()
                                }
                        }


                        // Delete all documents from the old profile


                        for (document in listOfDocuments) {
                            val selectedDateNumbers = document.date!!.replace(Regex("[^0-9]"), "")
                            database.collection("Director view")
                                .document("$workerName $workerId")
                                .collection("Days").document(selectedDateNumbers)
                                .delete()
                                .addOnSuccessListener {

                                }
                        }


                        // Delete old name from the global list of the app


                        database.collection("Director view")
                            .document("$workerName $workerId").delete()
                            .addOnSuccessListener {

                            }


                        /* Delete old profile and create new profile
                    in same database location with updated name
                     */

                        database.collection("Users")
                            .document(user.uid).collection("user data")
                            .document("$workerName $workerId").delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Old name deleted", Toast.LENGTH_SHORT).show()
                                database.collection("Users")
                                    .document(user.uid).collection("user data")
                                    .document("$newName $workerId")
                                    .set(newUserId)
                                    .addOnSuccessListener {
                                        val intent = Intent(this, WorkerSignIn::class.java)
                                        startActivity(intent)
                                    }
                            }

                    }
            }


        } else {
            Toast.makeText(this, "You did not change your name", Toast.LENGTH_SHORT).show()
        }



    }


    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

}