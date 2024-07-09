package com.example.company_app

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.company_app.adapters.MyAdapterName
import com.example.company_app.adapters.workDayAdapter
import com.example.company_app.classes.objectData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(), workDayAdapter.OnDeleteClickListener, workDayAdapter.OnEditClickListener {


    lateinit var database : FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var specificRecyclerview: RecyclerView
    private lateinit var listOfDocuments : ArrayList<objectData>
    private lateinit var listOfSelectedDocuments : ArrayList<objectData>
    private lateinit var myAdapter: workDayAdapter
    private lateinit var adapterSelectedItem: workDayAdapter
    lateinit var objectDataItem : objectData
    lateinit var hoursWorkedBetweenDatesTxt: TextView
    lateinit var totalHoursWorkedTxt: TextView
    lateinit var calculateBtn: Button
    lateinit var deleteBtn: Button
    lateinit var deleteAllBtn: Button

    var dataWorker = ""
    var selectedFromDate = ""
    var selectedToDate = ""
    var selectedDate = ""
    var isAllDataFetched = false


    val handler = Handler(Looper.getMainLooper())



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = Firebase.firestore



        recyclerView = findViewById(R.id.ownerRecyclerview)
        specificRecyclerview = findViewById(R.id.specificRecyclerview)
        hoursWorkedBetweenDatesTxt = findViewById(R.id.hoursWorkedBetweenDatesTxt)
        calculateBtn = findViewById(R.id.calculateBtn)
        totalHoursWorkedTxt = findViewById(R.id.totalHoursWorkedTxt)
        deleteBtn = findViewById(R.id.deleteBtn)
        deleteAllBtn = findViewById(R.id.deleteAllBtn)
        specificRecyclerview.layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        specificRecyclerview.setHasFixedSize(true)
        listOfSelectedDocuments = arrayListOf()
        listOfDocuments = arrayListOf()
        myAdapter = workDayAdapter(listOfDocuments)
        adapterSelectedItem = workDayAdapter(listOfSelectedDocuments)

        recyclerView.adapter = myAdapter
        specificRecyclerview.adapter = adapterSelectedItem
        adapterSelectedItem.setOnDeleteClickListener(this)
        adapterSelectedItem.setOnEditClickListener(this)
        myAdapter.setOnDeleteClickListener(this)
        myAdapter.setOnEditClickListener(this)
        listOfSelectedDocuments.clear()
        listOfDocuments.clear()


        calculateBtn.isEnabled = false
        deleteBtn.isEnabled = false
        deleteAllBtn.isEnabled = false






        val myDatePicker = findViewById<DatePicker>(R.id.datePicker)

        // Set a default date if needed
        val calendar = Calendar.getInstance()
        myDatePicker.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            null
        )






        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        selectedDate = dateFormat.format(calendar.time)



        // Set the visibility of the DatePicker based on your requirements
        myDatePicker.visibility = View.VISIBLE // Show the DatePicker initially


        // Set the listener for date changes
        myDatePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            selectedDate = "$dayOfMonth/${monthOfYear + 1}/$year"
            if (isAllDataFetched) {  // makes sure all data is available first before interaction
                onDateSelected(selectedDate)
            }
        }




        val selectedName = intent.getStringExtra("selectedName")
        val userId = intent.getStringExtra("userId")
        dataWorker = "$selectedName $userId"





        /* The enabling and disabling of the buttons in this pattern
        in the snapshot makes sure that the user can interact
        with the database only after all documents are
        confirmed to have been successfully fetched and displayed
        to make sure that the user has all the information
        needed first, before proceeding with any kind of activity.
        In this way we can have a massive database without
        worrying about downloading speeds
         */




        var documentsCounter = 0 // Counter to keep track of documents




            database.collection("Director view").document(dataWorker)
                .collection("Days")
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {
                        listOfDocuments.clear()
                        myAdapter.notifyDataSetChanged()
                        calculateBtn.isEnabled = false
                        deleteBtn.isEnabled = false
                        deleteAllBtn.isEnabled = false
                        isAllDataFetched = false

                        // Reset the counter
                        documentsCounter = 0

                        if (snapshot.isEmpty) {
                            // No documents in the snapshot, enable the button
                            calculateBtn.isEnabled = true
                            deleteBtn.isEnabled = true
                            deleteAllBtn.isEnabled = true
                            isAllDataFetched = true
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
                                    calculateBtn.isEnabled = true
                                    deleteBtn.isEnabled = true
                                    deleteAllBtn.isEnabled = true
                                    isAllDataFetched = true
                                    myAdapter.notifyDataSetChanged()
                                    onDateSelected(selectedDate)
                                }
                            }
                        }
                    }
                }






        val fromDateSpinner = findViewById<Spinner>(R.id.fromSpinner)

        // Set up dates for the spinner
        val datesList = generateDatesList()

        // Create an ArrayAdapter using a simple spinner layout and the dates
        val fromDateAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, datesList)

        // Specify the layout to use when the list of choices appears
        fromDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        fromDateSpinner.adapter = fromDateAdapter

        // Set current date as default selected date
        val currentFromDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        fromDateSpinner.setSelection(fromDateAdapter.getPosition(currentFromDate))
        selectedFromDate = currentFromDate

        // Set listener for spinner item selection
        fromDateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: android.view.View?,
                position: Int,
                id: Long
            ) {
                selectedFromDate = datesList[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Handle case where nothing is selected
            }
        }



        val toDateSpinner = findViewById<Spinner>(R.id.toSpinner)

// Create an ArrayAdapter using a simple spinner layout and the dates
        val toDateAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, datesList)

// Specify the layout to use when the list of choices appears
        toDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

// Apply the adapter to the spinner
        toDateSpinner.adapter = toDateAdapter

// Set current date as default selected date
        val currentToDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        toDateSpinner.setSelection(toDateAdapter.getPosition(currentToDate))
        selectedToDate = currentToDate

// Apply custom background for the spinner when pressed
        val customBackgroundDrawable = ContextCompat.getDrawable(this, R.drawable.item_background)
        toDateSpinner.background = customBackgroundDrawable

// Set listener for spinner item selection
        toDateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: android.view.View?, position: Int, id: Long) {
                selectedToDate = datesList[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Handle case where nothing is selected
            }
        }





        calculateBtn.setOnClickListener {
            if (isInternetAvailable()) {
                var totalHoursWorked = 0.0

                val fromDate =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(selectedFromDate)
                val toDate =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(selectedToDate)

                for (document in listOfDocuments) {
                    // Check if the document date is within the selected date range
                    if (isDateWithinRange(document.date, fromDate, toDate)) {
                        totalHoursWorked += document.hours
                    }
                }

                totalHoursWorkedTxt.text = "$totalHoursWorked h"
                if (selectedFromDate == selectedToDate) {
                    hoursWorkedBetweenDatesTxt.text = "Hours worked in $selectedFromDate:"
                } else {
                    hoursWorkedBetweenDatesTxt.text =
                        "Hours worked between $selectedFromDate and $selectedToDate:"
                }
            } else {
                Toast.makeText(this, "No stable internet connection", Toast.LENGTH_SHORT).show()
            }
        }



        deleteBtn.setOnClickListener {
            if (isInternetAvailable()) {
                val fromDate =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(selectedFromDate)
                val toDate =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(selectedToDate)

                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle("Confirmation")
                if (selectedFromDate == selectedToDate) {
                    alertDialogBuilder.setMessage("Are you sure you want to delete the document of $selectedFromDate")
                } else {
                    alertDialogBuilder.setMessage("Are you sure you want to delete the documents between $selectedFromDate and $selectedToDate?")
                }
                alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                    deleteDocuments(fromDate, toDate)
                }
                alertDialogBuilder.setNegativeButton("No") { _, _ ->
                    // Do nothing or handle the case where the user chooses not to delete
                }

                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            } else {
                Toast.makeText(this, "No stable internet connection", Toast.LENGTH_SHORT).show()
            }
        }



        deleteAllBtn.setOnClickListener {
            if (isInternetAvailable()) {

                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle("Confirmation")

                alertDialogBuilder.setMessage("Are you sure you want to delete all documents of this user")

                alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                    deleteAllDocuments()
                }
                alertDialogBuilder.setNegativeButton("No") { _, _ ->
                    // Do nothing or handle the case where the user chooses not to delete
                }

                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            } else {
                Toast.makeText(this, "No stable internet connection", Toast.LENGTH_SHORT).show()
            }
        }




    }





    private fun deleteDocuments(fromDate: Date, toDate: Date) {
        var documentsDeleted = 0
        for (document in listOfDocuments) {
            if (isDateWithinRange(document.date, fromDate, toDate)) {
                val dateNumbers = document.date!!.replace(Regex("[^0-9]"), "")
                database.collection("Director view").document(dataWorker)
                    .collection("Days").document(dateNumbers).delete()
                    .addOnSuccessListener {
                        documentsDeleted++
                    }
            }
        }

        handler.postDelayed({
            Toast.makeText(this, "$documentsDeleted documents deleted", Toast.LENGTH_SHORT).show()
        }, 2000)
    }


    fun deleteAllDocuments() {

        var documentsDeleted = 0

        for (document in listOfDocuments) {
                val dateNumbers = document.date!!.replace(Regex("[^0-9]"), "")
                database.collection("Director view").document(dataWorker)
                    .collection("Days").document(dateNumbers).delete()
                    .addOnSuccessListener {
                        documentsDeleted++
                    }
        }

        handler.postDelayed({
            Toast.makeText(this, "$documentsDeleted documents deleted", Toast.LENGTH_SHORT).show()
        }, 2000)


    }





    private fun onDateSelected(selectedDate: String) {
        // Parse selectedDate into a Date object
        val selectedDateTime = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(selectedDate)

        // Format the Date object back to the desired string format
        val formattedSelectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDateTime)

        val selectedObjectData = listOfDocuments.find { it.date == formattedSelectedDate }

        if (selectedObjectData != null) {
            listOfSelectedDocuments.clear()
            adapterSelectedItem.notifyDataSetChanged()
            listOfSelectedDocuments.add(selectedObjectData)
            adapterSelectedItem.notifyDataSetChanged()
        } else {
            listOfSelectedDocuments.clear()
            adapterSelectedItem.notifyDataSetChanged()
           // Toast.makeText(this, "No matching dates", Toast.LENGTH_SHORT).show()
        }
    }




    private fun dateToMillis(dateString: String): Long {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = format.parse(dateString)
        return date?.time ?: 0
    }





    private fun isDateWithinRange(dateString: String?, fromDate: Date, toDate: Date): Boolean {
        if (dateString.isNullOrBlank()) {
            return false
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Parse the date from the document
        val documentDate = sdf.parse(dateString)

        // Check if the document date is within the selected date range
        return documentDate in fromDate..toDate || documentDate in toDate..fromDate
    }




    @SuppressLint("SuspiciousIndentation")
    override fun onDeleteClick(manifesto: objectData) {
        if (isAllDataFetched) { // makes sure all data is available first before interaction
            var dateNumbers = manifesto.date
            dateNumbers = dateNumbers!!.replace(Regex("[^0-9]"), "")


            database.collection("Director view").document(dataWorker)
                .collection("Days").document(dateNumbers).delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Item not deleted, try again", Toast.LENGTH_SHORT).show()
                }

        }
    }






    override fun onEditClick(manifesto: objectData) {
        if (isAllDataFetched) { // makes sure all data is available first before interaction
            val dialogView = LayoutInflater.from(this).inflate(R.layout.edit_dialog_layout, null)
            val numberEditText: EditText = dialogView.findViewById(R.id.numberEditText)
            val commentEditText: EditText = dialogView.findViewById(R.id.commentEditText)
            val confirmButton: Button = dialogView.findViewById(R.id.confirmButton)
            val cancelButton: Button = dialogView.findViewById(R.id.cancelButton)


            var dateNumbers = manifesto.date
            dateNumbers = dateNumbers!!.replace(Regex("[^0-9]"), "")

            val alertDialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Edit Document of ${manifesto.date}")

            val alertDialog = alertDialogBuilder.create()

            confirmButton.setOnClickListener {
                val numberValue = numberEditText.text.toString().toDoubleOrNull()
                val commentValue = commentEditText.text.toString()

                if (numberValue != null) {
                    manifesto.hours = numberValue
                    manifesto.comment = commentValue
                    database.collection("Director view").document(dataWorker)
                        .collection("Days").document(dateNumbers).set(manifesto)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Item edited", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Item not edited, try again", Toast.LENGTH_SHORT)
                                .show()
                        }
                } else {
                    // Invalid number value, show a message
                    Toast.makeText(this, "Invalid Number", Toast.LENGTH_SHORT).show()
                }

                alertDialog.dismiss()
            }

            cancelButton.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
        }
    }






    private fun generateDatesList(): List<String> {
        val datesList = mutableListOf<String>()
        val calendar = Calendar.getInstance()

        // Add dates from current date to three months ago
        repeat(400) {
            datesList.add(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time))
            calendar.add(Calendar.DAY_OF_MONTH, -1)
        }

        // Reverse the list to have the latest date first
        return datesList.reversed()
    }



    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }




    /*
    override fun onBackPressed() {

    }

     */








}