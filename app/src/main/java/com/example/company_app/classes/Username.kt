package com.example.company_app.classes

import com.google.firebase.firestore.DocumentId

data class username(@DocumentId var DocumentId: String? = null, var name : String? = null,
    var numberID : String = "")
