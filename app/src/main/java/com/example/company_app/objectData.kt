package com.example.company_app

import com.google.firebase.firestore.DocumentId


data class objectData(@DocumentId var DocumentId: String? = null, var hours: Double = 0.0,
                      var date: String? = null, var comment: String? = null, var userIdentity : String? = null,
                      var totalHours: Double = 0.0, var preOrder : Int = 1, var order : Int = 0)

