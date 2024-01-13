package com.example.company_app

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.company_app.adapters.workDayAdapter
import com.example.company_app.classes.objectData
import com.example.company_app.classes.username
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WorkerProfile : AppCompatActivity(), workDayAdapter.OnDeleteClickListener, workDayAdapter.OnEditClickListener {



    lateinit var saveBtn : Button
    lateinit var personC : TextView
    lateinit var personN : TextView
    lateinit var workHoursEditText : TextView
    lateinit var workerProfLogOutBtn : Button


   lateinit var objectDataItem : objectData




    var calculator : Double = 0.0
    var counter : Int = 0
    var dataWorker = ""
    private var touches = 0


    lateinit var database : FirebaseFirestore
    lateinit var nameInDatabase : username


    lateinit var auth : FirebaseAuth




    private lateinit var recyclerView: RecyclerView
    private lateinit var listOfDocuments : ArrayList<objectData>
    private lateinit var myAdapter: workDayAdapter
    private lateinit var selectedDate: String


    var isAccountEnabled = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker_profile)


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        listOfDocuments = arrayListOf()
        myAdapter = workDayAdapter(listOfDocuments)
        recyclerView.adapter = myAdapter
        myAdapter.setOnDeleteClickListener(this)
        myAdapter.setOnEditClickListener(this)
        listOfDocuments.clear()




        database = Firebase.firestore
        auth = Firebase.auth
        val user = auth.currentUser





        personC = findViewById(R.id.editTextTextPersonComment)
        personN = findViewById(R.id.nameText)
        workHoursEditText = findViewById(R.id.workHoursEditText)
        saveBtn = findViewById(R.id.workerLogOut)
        workerProfLogOutBtn = findViewById(R.id.workerProfLogOutBtn)
        saveBtn.isEnabled = false





        dataWorker = intent.getStringExtra("dataWorker")!!
        var workerName = intent.getStringExtra("workerName")
        var workerId = intent.getStringExtra("workerId")
        personN.text = workerName




        val spinner = findViewById<Spinner>(R.id.dateSpinner)

        // Set up dates for the spinner
        val datesList = generateDatesList()

        // Create an ArrayAdapter using a simple spinner layout and the dates
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, datesList)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        spinner.adapter = adapter

        // Set current date as default selected date
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        spinner.setSelection(adapter.getPosition(currentDate))
        selectedDate = currentDate

        // Set listener for spinner item selection
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: android.view.View?,
                position: Int,
                id: Long
            ) {
                selectedDate = datesList[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Handle case where nothing is selected
            }
        }





        /* The enabling and disabling of saveBtn in this pattern
        in the snapshot makes sure that the user can interact
        with the database only after all documents are
        confirmed to have been successfully fetched and displayed.
        In this way we can have a massive database without
         without worrying about downloading speeds
         */


        var documentsCounter = 0 // Counter to keep track of documents

        if (user != null) {
            database.collection("Director view").document(dataWorker)
                .collection("Days")
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {
                        listOfDocuments.clear()
                        myAdapter.notifyDataSetChanged()
                        saveBtn.isEnabled = false

                        // Reset the counter
                        documentsCounter = 0

                        if (snapshot.isEmpty) {
                            // No documents in the snapshot, enable the button
                            saveBtn.isEnabled = true
                        } else {
                            for (document in snapshot.documents) {
                                objectDataItem = document.toObject()!!
                                listOfDocuments.add(objectDataItem)

                                documentsCounter++

                                // Check if all documents are fetched
                                if (documentsCounter == snapshot.size()) {
                                    // Sort the list based on the date
                                    listOfDocuments.sortByDescending { it.date?.let { it1 -> dateToMillis(it1) } }

                                    // Activate save button now that all documents are fetched
                                    saveBtn.isEnabled = true
                                    myAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
        } else {
            Toast.makeText(this, "User is logged out due to a problem", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, WorkerSignIn::class.java)
            startActivity(intent)
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








        workerProfLogOutBtn.setOnClickListener {
           auth.signOut()
            // Navigate to the sign-in screen
                val intent = Intent(this, WorkerSignIn::class.java)
                startActivity(intent)
           // finish()
        }





        saveBtn.setOnClickListener {
            if (isInternetAvailable()) {
                if (isAccountEnabled) {
                    var allowSave = true
                    for (document in listOfDocuments) {
                        if (selectedDate == document.date) {
                            allowSave = false
                            Toast.makeText(this, "Document with same date already exists!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    if (allowSave) {
                        saveItem()
                    }

                } else {
                    Toast.makeText(this, "Your account is disabled", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }










    }



    private fun dateToMillis(dateString: String): Long {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = format.parse(dateString)
        return date?.time ?: 0
    }






    fun saveItem() {

        var selectedDateNumbers = selectedDate.replace(Regex("[^0-9]"), "")

        val workHoursInput = workHoursEditText.text.toString()

        try {
            val workHours = workHoursInput.toDouble()

            // Continue with your code here
            val item = objectData(
                comment = personC.text.toString(),
                hours = workHours,
                totalHours = 0.0,
                userIdentity = personN.text.toString(),
                order = counter,
                date = selectedDate,
                preOrder = 1
            )



            val user = auth.currentUser

            if (user != null) {
                database.collection("Director view").document(dataWorker)
                    .collection("Days").document(selectedDateNumbers).set(item)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Item correctly saved", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "You are logged out due to a problem", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, WorkerSignIn::class.java)
                startActivity(intent)
            }



        } catch (e: NumberFormatException) {
            // Handle the case where input is not a valid double
            // For example, show a Toast message or set a default value
            Toast.makeText(this, "Invalid input for work hours", Toast.LENGTH_SHORT).show()
        }



    }




    override fun onBackPressed() {
        Toast.makeText(this, "Use the logout button!", Toast.LENGTH_SHORT).show()
    }





    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }




    private fun generateDatesList(): List<String> {
        val datesList = mutableListOf<String>()
        val calendar = Calendar.getInstance()

        // Add dates from current date to three months ago
        repeat(90) {
            datesList.add(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time))
            calendar.add(Calendar.DAY_OF_MONTH, -1)
        }

        // Reverse the list to have the latest date first
        return datesList.reversed()
    }


    override fun onDeleteClick(manifesto: objectData) {

        Toast.makeText(this, "Only the company owner can delete", Toast.LENGTH_SHORT).show()


    }



    override fun onEditClick(manifesto: objectData) {

        Toast.makeText(this, "Only the company owner can edit", Toast.LENGTH_SHORT).show()


    }






}