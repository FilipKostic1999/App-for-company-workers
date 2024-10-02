package com.example.company_app

import NotificationWorker
import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.company_app.classes.userData
import com.example.company_app.classes.username
import com.example.company_app.databinding.ActivityWorkerSignInBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import java.util.concurrent.TimeUnit

class WorkerSignIn : AppCompatActivity() {


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            scheduleNotificationAtFivePM()
        } else {
            // Permission denied, handle accordingly
        }
    }


    private lateinit var binding: ActivityWorkerSignInBinding
    lateinit var adminImg: ImageView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var plåtImg: ImageView

    lateinit var database : FirebaseFirestore
    lateinit var nameInDatabase : username
    var dataWorker = ""


    private lateinit var cardViewShake: CardView
    private val handler = Handler()


    private lateinit var workerSignUpPasEditTexst: EditText
    private lateinit var eyeImg: ImageView
    private var isPasswordVisible: Boolean = false

    private val shakeRunnable = object : Runnable {
        override fun run() {
            // Load shake animation
            val shakeAnimation = AnimationUtils.loadAnimation(this@WorkerSignIn, R.anim.shake)

            // Start animation on the cardViewShake
            cardViewShake.startAnimation(shakeAnimation)

            // Schedule the next shake after 3 seconds (3000 milliseconds)
            handler.postDelayed(this, 4000)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        var auth = FirebaseAuth.getInstance()
        database = Firebase.firestore
        val user = auth.currentUser





        binding = ActivityWorkerSignInBinding.inflate(layoutInflater)

        setContentView(binding.root)







        firebaseAuth = FirebaseAuth.getInstance()



        // Create notification channel
        createNotificationChannel()
        showNotification()


        // Check for notification permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Begär tillstånd från användaren
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        } else {
            // Schemalägg din notifikation om tillstånd redan har beviljats
            scheduleNotificationAtFivePM()
        }
        // Check for notification permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            scheduleNotificationAtFivePM()// Schedule your notification if permission already granted
        }



        eyeImg = findViewById(R.id.eyeImg)

        binding.adminImg.setOnClickListener {
            showPasswordDialog()
        }

        // Set initial drawable
        eyeImg.setImageResource(R.drawable.baseline_remove_red_eye_24)
        // Initialize views
        workerSignUpPasEditTexst = findViewById(R.id.workerSignUpPasEditTexst)

        // Set click listener for eyeImageView
        eyeImg.setOnClickListener {
            togglePasswordVisibility()
        }



        binding.workerSignInButton.setOnClickListener {

            val email = binding.workerSignUpEmailEditTexst.text.toString()
            val pass = binding.workerSignUpPasEditTexst.text.toString()

            if (isInternetAvailable()) {
                if (email.isNotEmpty() && pass.isNotEmpty()) {
                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            val user = firebaseAuth.currentUser
                            if (user != null) {
                                database.collection("Users").document(user.uid)
                                    .get()
                                    .addOnSuccessListener { document ->
                                        if (document.exists()) {
                                            val userData = document.toObject<userData>()
                                            val intent = Intent(this, WorkerProfile::class.java)
                                            intent.putExtra("userId", userData?.userUID)
                                            intent.putExtra("isWorkerBlocked", userData?.isBlocked)
                                            intent.putExtra("workerName", userData?.name)
                                            intent.putExtra("workerEmail", userData?.email)
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(this, "Document not found", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(this, "Log in failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                                    }

                            } else {
                                Toast.makeText(this, "The user is null", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, authTask.exception.toString(), Toast.LENGTH_SHORT).show()
                            Log.d("!!!", authTask.exception.toString())
                        }
                    }
                } else {
                    Toast.makeText(this, "There are empty fields", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }

        plåtImg = findViewById(R.id.plåtImg)

        // Load the animation from XML
        val animation = AnimationUtils.loadAnimation(this, R.anim.circular_path_animation)

        // Apply the animation to the ImageView
        plåtImg.startAnimation(animation)


        // Initialize cardViewShake after setContentView
        cardViewShake = findViewById(R.id.cardViewShake)

        // Start the initial shake animation
        handler.post(shakeRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove any pending callbacks to prevent memory leaks
        handler.removeCallbacks(shakeRunnable)
    }





    // Function to check internet connectivity
    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }




    private fun showPasswordDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.password_dialog_layout, null)
        val passwordEditText: EditText = dialogView.findViewById(R.id.passwordEditText)
        val submitButton: Button = dialogView.findViewById(R.id.submitButton)
        val cancelButton: Button = dialogView.findViewById(R.id.cancelButton)

        val alertDialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)


        val alertDialog = alertDialogBuilder.create()

        submitButton.setOnClickListener {
            val enteredPassword = passwordEditText.text.toString()
            if (enteredPassword == "404090") {
                // Password is correct, navigate to the desired activity
                val intent = Intent(this, OwnerShowRecycleview::class.java)
                startActivity(intent)
                alertDialog.dismiss()
            } else {
                // Wrong password, show a message
                Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show()
            }
        }

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }






    override fun onBackPressed() {

    }



    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide the password
            workerSignUpPasEditTexst.transformationMethod = PasswordTransformationMethod.getInstance()
            eyeImg.setImageResource(R.drawable.baseline_remove_red_eye_24)
        } else {
            // Show the password
            workerSignUpPasEditTexst.transformationMethod = HideReturnsTransformationMethod.getInstance()
            eyeImg.setImageResource(R.drawable.outline_remove_red_eye_24)
        }
        // Toggle the flag
        isPasswordVisible = !isPasswordVisible

        // Move the cursor to the end of the text
        workerSignUpPasEditTexst.setSelection(workerSignUpPasEditTexst.text.length)
    }

    // Notification channel creation
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Default Channel"
            val descriptionText = "Channel for notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("YOUR_CHANNEL_ID", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification() {
        createNotificationChannel() // Ensure channel exists

        // Ladda den stora ikonen från drawable-resurser
        val largeIcon = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.work) // Ersätt med din stora ikon

        // Bygg notifikationen
        val builder = NotificationCompat.Builder(applicationContext, "YOUR_CHANNEL_ID")
            .setSmallIcon(R.drawable.baseline_notifications_24) // Din notifikationsikon
            .setLargeIcon(largeIcon) // Den stora ikonen som visas
            .setContentTitle("Check in!") // Titeln på notifikationen
            .setContentText("Remember to check in after finishing work at 5:00 PM.") // Texten i notifikationen
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Sätt prioritet
            .setAutoCancel(true) // Auto avbryt när den trycks
            .setStyle(NotificationCompat.BigTextStyle().bigText("Remember to check in after finishing work at 5:00 PM.")) // Utökad textstil

        // Visa notifikationen
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(1, builder.build()) // Visa notifikationen med ID 1
        }
    }





    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotificationAtFivePM() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java)

        // Lägg till FLAG_IMMUTABLE till PendingIntent
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Hämta nuvarande tid och sätt alarmet till 17:00
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 16)
        calendar.set(Calendar.MINUTE, 50)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Om tiden för dagen redan har passerat, schemalägg för nästa dag
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        // Schemalägg alarmet
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}


