package com.example.hobbyapp

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import com.google.firebase.database.*

class EventDetailsDialog(context: Context, private val event: Event) : Dialog(context) {

    private lateinit var db: DatabaseReference

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

        eventNameTextView.text = event.eventName
        maxParticipantsTextView.text = "Max Participants: ${event.maxParticipants}"
        eventDateTextView.text = "Event Date: ${event.eventDate}"
        eventDetailsTextView.text = "Details: ${event.eventDetails}"
        eventLocationTextView.text = "Location: ${event.eventLocation}"

        // Initialize Firebase Realtime Database reference
        db = FirebaseDatabase.getInstance("https://hobbyapp-75fdb-default-rtdb.europe-west1.firebasedatabase.app").reference

        // Fetch the creator's name from Realtime Database
        if (event.createdBy.isNotEmpty()) {
            db.child("Users").child(event.createdBy).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val creatorName = dataSnapshot.child("name").getValue(String::class.java)
                    createdByTextView.text = "Created By: ${creatorName ?: "Unknown"}"
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    createdByTextView.text = "Created By: Unknown"
                }
            })
        } else {
            createdByTextView.text = "Created By: Unknown"
        }
    }
}
