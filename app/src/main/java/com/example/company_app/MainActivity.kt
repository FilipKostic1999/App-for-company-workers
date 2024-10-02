package com.example.company_app

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.view.animation.Transformation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.company_app.adapters.workDayAdapter
import com.example.company_app.classes.objectData
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
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
    lateinit var historyTxt: TextView
    lateinit var selectedTxt: TextView
    lateinit var totalHoursWorkedTxt: TextView
    lateinit var calculateBtn: Button
    lateinit var deleteBtn: Button
    lateinit var deleteAllBtn: Button

    private lateinit var pressToSelectTxt: TextView
    private lateinit var fadeInAnimation: Animation
    private lateinit var fadeOutAnimation: Animation
    private lateinit var fadeInOutAnimationSet: AnimationSet
    private var isTextVisible = true

    private lateinit var scaleOpenAnimation: Animation
    private lateinit var translateUpAnimation: Animation
    private lateinit var animationSet: AnimationSet
    private lateinit var arrowDownImg: ImageView


    var dataWorker = ""
    var selectedFromDate = ""
    var selectedToDate = ""
    var selectedDate = ""
    var userId = ""
    var isAllDataFetched = false



    private lateinit var nestedScrollView2: NestedScrollView
    private lateinit var nestedScrollView6: NestedScrollView
    private lateinit var arrowImageView: ImageView

    private lateinit var cardView8: CardView

    private lateinit var cardViewAnim: CardView

    private lateinit var fadeScaleInAnimation: Animation
    private lateinit var fadeScaleOutAnimation: Animation

    private val handler = Handler()
    private val animationRunnable = object : Runnable {
        override fun run() {
            if (isTextVisible) {
                // Repeat the fade in and fade out animation
                pressToSelectTxt.startAnimation(fadeInOutAnimationSet)
                handler.postDelayed(this, fadeInOutAnimationSet.duration * 2) // Run every 2 seconds
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        // Create the notification channel
        NotificationUtils.createNotificationChannel(this)

        // Schedule daily notification
        NotificationUtils.scheduleDailyNotification(this)

        // Optionally, show a notification immediately for testing
        NotificationUtils.showNotification(this)
        // Check notification permission


        // Request notification permission if needed
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        } else {
            // Create notification channel
            NotificationUtils.createNotificationChannel(this)
            // Schedule daily notification
            NotificationUtils.scheduleDailyNotification(this)
        }


        database = Firebase.firestore

        // Initialize views
        arrowDownImg = findViewById(R.id.arrowDownImg)
        cardViewAnim = findViewById(R.id.cardViewAnim)
        nestedScrollView2 = findViewById(R.id.nestedScrollView2)
        nestedScrollView6 = findViewById(R.id.nestedScrollView6)
        arrowImageView = findViewById(R.id.arrowImageView)
        cardView8 = findViewById(R.id.cardView8)

        historyTxt = findViewById(R.id.historyTxt)
        selectedTxt = findViewById(R.id.selectedTxt)


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



        val cardView8: CardView = findViewById(R.id.cardView8)

        val upDownAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.up_down)

        cardView8.startAnimation(upDownAnimation)
        // Locate the arrowImageView from the layout
        val arrowImageView = findViewById<ImageView>(R.id.arrowImageView)




        // Initialize views

        pressToSelectTxt = findViewById(R.id.pressToSelectTxt)

        // Load animations
        scaleOpenAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_open)
        translateUpAnimation = AnimationUtils.loadAnimation(this, R.anim.translate_up)
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)



        // Create an AnimationSet to combine animations
        animationSet = AnimationSet(true).apply {
            addAnimation(scaleOpenAnimation)
            addAnimation(translateUpAnimation)
            addAnimation(fadeInAnimation)
            duration = 1000
            fillAfter = true
        }


        // Automatically show and animate the CardView
        cardViewAnim.visibility = View.VISIBLE
        cardViewAnim.startAnimation(animationSet)




        // Load animations
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        // Load animations
        fadeScaleInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_scale_in)
        fadeScaleOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_scale_out)


        // Create an AnimationSet to loop fade-in and fade-out
        fadeInOutAnimationSet = AnimationSet(true)
        fadeInOutAnimationSet.addAnimation(fadeInAnimation)
        fadeInOutAnimationSet.addAnimation(fadeOutAnimation)
        fadeInOutAnimationSet.duration = 2000 // Total duration of one cycle (1 second fade in, 1 second fade out)
        fadeInOutAnimationSet.repeatCount = Animation.INFINITE // Repeat forever

        // Initially show the text with fade-in animation
        pressToSelectTxt.visibility = View.VISIBLE
        pressToSelectTxt.startAnimation(fadeInOutAnimationSet)

        // Start the repeating animation
        handler.post(animationRunnable)

        // Set click listener for arrowImageView
        arrowImageView.setOnClickListener {
            toggleNestedScrollViews()
            if (isTextVisible) {
                // Stop the animation and hide the TextView
                pressToSelectTxt.clearAnimation()
                arrowDownImg.clearAnimation()
                arrowDownImg.visibility = View.GONE
                cardViewAnim.clearAnimation()
                cardViewAnim.visibility = View.GONE
                pressToSelectTxt.visibility = View.GONE
                isTextVisible = false
                handler.removeCallbacks(animationRunnable) // Stop repeating animation
            }
        }
    }




    private fun toggleNestedScrollViews() {
        if (nestedScrollView2.visibility == View.VISIBLE) {

            // Collapse NestedScrollViews
            nestedScrollView2.visibility = View.GONE
            nestedScrollView6.visibility = View.GONE
            recyclerView.visibility = View.GONE // Hide RecyclerView
            historyTxt.visibility = View.GONE // Hide historyTxt
            selectedTxt.visibility = View.GONE // Hide selectedTxt
            arrowImageView.setImageResource(R.drawable.sharp_arrow_drop_up_24)
        } else {


            arrowImageView.setImageResource(R.drawable.baseline_arrow_drop_down_24)

            // Expand NestedScrollViews
            nestedScrollView2.visibility = View.VISIBLE
            nestedScrollView6.visibility = View.VISIBLE

            recyclerView.visibility = View.VISIBLE // Show RecyclerView
            historyTxt.visibility = View.VISIBLE // Show historyTxt
            selectedTxt.visibility = View.VISIBLE // Show selectedTxt
            arrowImageView.setImageResource(R.drawable.baseline_arrow_drop_down_24)

            // Scroll nestedScrollView2 to top
            nestedScrollView2.post {
                nestedScrollView2.smoothScrollTo(0, 0)
            }

            // Scroll nestedScrollView6 to top
            nestedScrollView6.post {
                nestedScrollView6.smoothScrollTo(0, 0)
            }
        }




    val myDatePicker = findViewById<DatePicker>(R.id.datePicker)

        // Set a default date if needed
        val calendar = Calendar.getInstance()
        myDatePicker.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            null
        )

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        selectedDate = dateFormat.format(calendar.time)


        // Set the visibility of the DatePicker based on your requirements
        myDatePicker.visibility = View.VISIBLE // Show the DatePicker initially

        // Set the listener for date changes
        myDatePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, monthOfYear, dayOfMonth)
            val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            if (isAllDataFetched) {
                onDateSelected(selectedDate)
            }
        }



        val selectedName = intent.getStringExtra("selectedName")
        userId = intent.getStringExtra("userId")!!
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




            database.collection("Users").document(userId!!)
                .collection("Manifesto")
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
        val currentFromDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
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
        val currentToDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
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
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedFromDate)
                val toDate =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedToDate)

                for (document in listOfDocuments) {
                    // Check if the document date is within the selected date range
                    if (isDateWithinRange(document.date, fromDate, toDate)) {
                        totalHoursWorked += document.hours.toDouble()
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
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedFromDate)
                val toDate =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedToDate)

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
                val dateNumbers = document.date!!
                database.collection("Users").document(userId!!)
                    .collection("Manifesto").document(dateNumbers).delete()
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
                val date = document.date!!
            database.collection("Users").document(userId!!)
                .collection("Manifesto").document(date).delete()
                    .addOnSuccessListener {
                        documentsDeleted++
                    }
        }

        handler.postDelayed({
            Toast.makeText(this, "$documentsDeleted documents deleted", Toast.LENGTH_SHORT).show()
        }, 2000)


    }





    private fun onDateSelected(selectedDate: String) {
        // No need to re-parse the selectedDate since it's already in the correct format
        val selectedObjectData = listOfDocuments.find {
            println("Comparing: ${it.date} with $selectedDate") // Debugging
            it.date == selectedDate
        }

        listOfSelectedDocuments.clear()

        if (selectedObjectData != null) {
            listOfSelectedDocuments.add(selectedObjectData)
        }

        adapterSelectedItem.notifyDataSetChanged()
    }




    private fun dateToMillis(dateString: String): Long {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = format.parse(dateString)
        return date?.time ?: 0
    }





    private fun isDateWithinRange(dateString: String?, fromDate: Date, toDate: Date): Boolean {
        if (dateString.isNullOrBlank()) {
            return false
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Parse the date from the document
        val documentDate = sdf.parse(dateString)

        // Check if the document date is within the selected date range
        return documentDate in fromDate..toDate || documentDate in toDate..fromDate
    }





    override fun onDeleteClick(manifesto: objectData) {
        if (isAllDataFetched) { // makes sure all data is available first before interaction
            var dateNumbers = manifesto.date


            database.collection("Users").document(userId)
                .collection("Manifesto").document(dateNumbers.toString()).delete()
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

            val alertDialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)


            val alertDialog = alertDialogBuilder.create()

            confirmButton.setOnClickListener {
                val numberValue = numberEditText.text.toString().toDoubleOrNull()
                val commentValue = commentEditText.text.toString()

                if (numberValue != null) {
                    manifesto.hours = numberValue.toString()
                    manifesto.comment = commentValue
                    database.collection("Users").document(userId)
                        .collection("Manifesto").document(dateNumbers.toString()).set(manifesto)
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
            datesList.add(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time))
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

    // Handle permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                NotificationUtils.createNotificationChannel(this)
                NotificationUtils.scheduleDailyNotification(this)
            } else {
                Snackbar.make(findViewById(android.R.id.content), "Notification permission is required to receive reminders.", Snackbar.LENGTH_LONG).show()
            }
        }
    }


}

    /*
    override fun onBackPressed() {

    }

     */








