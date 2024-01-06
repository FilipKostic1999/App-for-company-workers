package com.example.company_app

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

class MainActivity : AppCompatActivity(), workDayAdapter.OnDeleteClickListener {


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

    var dataWorker = ""
    var selectedFromDate = ""
    var selectedToDate = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = Firebase.firestore



        recyclerView = findViewById(R.id.ownerRecyclerview)
        specificRecyclerview = findViewById(R.id.specificRecyclerview)
        hoursWorkedBetweenDatesTxt = findViewById(R.id.hoursWorkedBetweenDatesTxt)
        calculateBtn = findViewById(R.id.calculateBtn)
        totalHoursWorkedTxt = findViewById(R.id.totalHoursWorkedTxt)
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
        myAdapter.setOnDeleteClickListener(this)
        listOfSelectedDocuments.clear()
        listOfDocuments.clear()


        val myDatePicker = findViewById<DatePicker>(R.id.datePicker)

        // Set a default date if needed
        val calendar = Calendar.getInstance()
        myDatePicker.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            null
        )

        // Set the visibility of the DatePicker based on your requirements
        myDatePicker.visibility = View.VISIBLE // Show the DatePicker initially

        // Set the listener for date changes
        myDatePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${monthOfYear + 1}/$year"
            onDateSelected(selectedDate)
        }




        val selectedName = intent.getStringExtra("selectedName")
        val userId = intent.getStringExtra("userId")
        dataWorker = "$selectedName $userId"





            database.collection("Director view").document(dataWorker)
                .collection("Days")
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {
                        listOfDocuments.clear()
                        myAdapter.notifyDataSetChanged()
                        for (document in snapshot.documents) {
                            objectDataItem = document.toObject()!!
                            listOfDocuments.add(objectDataItem)
                        }

                        // Sort the list based on the date
                        listOfDocuments.sortByDescending { it.date?.let { it1 -> dateToMillis(it1) } }
                        myAdapter.notifyDataSetChanged()
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

        // Set listener for spinner item selection
        toDateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: android.view.View?,
                position: Int,
                id: Long
            ) {
                selectedToDate = datesList[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Handle case where nothing is selected
            }
        }




        calculateBtn.setOnClickListener {
            var totalHoursWorked = 0.0

            val fromDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(selectedFromDate)
            val toDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(selectedToDate)

            for (document in listOfDocuments) {
                // Check if the document date is within the selected date range
                if (isDateWithinRange(document.date, fromDate, toDate)) {
                    totalHoursWorked += document.hours
                }
            }

            totalHoursWorkedTxt.text = "$totalHoursWorked h"
            hoursWorkedBetweenDatesTxt.text = "Hours worked between $selectedFromDate and $selectedToDate:"
        }




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
            Toast.makeText(this, "No matching dates", Toast.LENGTH_SHORT).show()
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




    override fun onDeleteClick(manifesto: objectData) {
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





    /*
    override fun onBackPressed() {

    }

     */








}