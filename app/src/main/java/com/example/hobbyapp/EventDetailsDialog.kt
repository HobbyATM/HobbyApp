package com.example.hobbyapp

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore

object EventDetailsDialog {
    fun show(context: Context, eventId: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.event_details_dialog, null)
        val textViewEventName = dialogView.findViewById<TextView>(R.id.textViewEventName)
        val textViewEventDate = dialogView.findViewById<TextView>(R.id.textViewEventDate)
        val textViewEventDetails = dialogView.findViewById<TextView>(R.id.textViewEventDetails)
        val textViewEventLocation = dialogView.findViewById<TextView>(R.id.textViewEventLocation)
        val textViewEventCreator = dialogView.findViewById<TextView>(R.id.textViewCreatedBy)
        val textViewParticipants = dialogView.findViewById<TextView>(R.id.textViewMaxParticipants)

        val database = FirebaseDatabase.getInstance("https://hobbyapp-75fdb-default-rtdb.europe-west1.firebasedatabase.app").reference.child("Events").child(eventId)
        val firestore = FirebaseFirestore.getInstance()

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val event = snapshot.getValue(Event::class.java)
                if (event != null) {
                    textViewEventName.text = event.eventName
                    textViewEventDate.text = event.eventDate
                    textViewEventDetails.text = event.eventDetails
                    textViewEventLocation.text = event.eventLocation

                    // Set participants count
                    val participantsCount = event.participants.size
                    val maxParticipants = event.maxParticipants
                    textViewParticipants.text = "Participants: $participantsCount/$maxParticipants"

                    // Fetch the creator's name from Firestore
                    firestore.collection("User").document(event.createdBy).get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                val creatorName = document.getString("username") ?: "Unknown"
                                textViewEventCreator.text = "Created By: $creatorName"
                            } else {
                                textViewEventCreator.text = "Created By: Unknown"
                            }
                        }
                        .addOnFailureListener {
                            textViewEventCreator.text = "Created By: Unknown"
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        AlertDialog.Builder(context)
            .setTitle("Event Details")
            .setView(dialogView)
            .setPositiveButton("OK", null)
            .show()
    }
}
