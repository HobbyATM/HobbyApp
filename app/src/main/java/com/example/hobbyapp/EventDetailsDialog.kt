package com.example.hobbyapp

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.TextView
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore

class EventDetailsDialog(context: Context, private val eventId: String) : Dialog(context) {

    private lateinit var db: DatabaseReference
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.event_details_dialog)

        val eventNameTextView: TextView = findViewById(R.id.textViewEventName)
        val maxParticipantsTextView: TextView = findViewById(R.id.textViewMaxParticipants)
        val eventDateTextView: TextView = findViewById(R.id.textViewEventDate)
        val eventDetailsTextView: TextView = findViewById(R.id.textViewEventDetails)
        val eventLocationTextView: TextView = findViewById(R.id.textViewEventLocation)
        val createdByTextView: TextView = findViewById(R.id.textViewCreatedBy)

        // Initialize Firebase Realtime Database reference
        db = FirebaseDatabase.getInstance("https://hobbyapp-75fdb-default-rtdb.europe-west1.firebasedatabase.app").reference
        firestore = FirebaseFirestore.getInstance()

        // Fetch the event details from Realtime Database
        db.child("Events").child(eventId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val event = dataSnapshot.getValue(Event::class.java)
                if (event != null) {
                    eventNameTextView.text = event.eventName
                    maxParticipantsTextView.text = "Max Participants: ${event.maxParticipants}"
                    eventDateTextView.text = "Event Date: ${event.eventDate}"
                    eventDetailsTextView.text = "Details: ${event.eventDetails}"
                    eventLocationTextView.text = "Location: ${event.eventLocation}"

                    // Fetch the creator's name from Firestore using the createdBy ID
                    if (event.createdBy.isNotEmpty()) {
                        firestore.collection("User").document(event.createdBy).get()
                            .addOnSuccessListener { document ->
                                if (document != null && document.exists()) {
                                    val creatorName = document.getString("username")
                                    createdByTextView.text = "Created By: ${creatorName ?: "Unknown"}"
                                    Log.d("EventDetailsDialog", "Creator Name: $creatorName")
                                } else {
                                    createdByTextView.text = "Created By: Unknown"
                                    Log.d("EventDetailsDialog", "Document does not exist")
                                }
                            }
                            .addOnFailureListener { e ->
                                createdByTextView.text = "Created By: Unknown"
                                Log.e("EventDetailsDialog", "Error fetching creator name", e)
                            }
                    } else {
                        createdByTextView.text = "Created By: Unknown"
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
                Log.e("EventDetailsDialog", "Database error: ${databaseError.message}")
            }
        })
    }
}
