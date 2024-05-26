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
        val database: DatabaseReference = FirebaseDatabase.getInstance("https://hobbyapp-75fdb-default-rtdb.europe-west1.firebasedatabase.app").reference
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        val eventRef = database.child("Events").child(eventId)
        eventRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val event = dataSnapshot.getValue(Event::class.java)
                if (event != null) {
                    val builder = AlertDialog.Builder(context)
                    val inflater = LayoutInflater.from(context)
                    val view = inflater.inflate(R.layout.event_details_dialog, null)

                    val eventNameTextView: TextView = view.findViewById(R.id.textViewEventName)
                    val eventDateTextView: TextView = view.findViewById(R.id.textViewEventDate)
                    val eventDetailsTextView: TextView = view.findViewById(R.id.textViewEventDetails)
                    val eventLocationTextView: TextView = view.findViewById(R.id.textViewEventLocation)
                    val eventCreatedByTextView: TextView = view.findViewById(R.id.textViewCreatedBy)
                    val maxParticipantsTextView: TextView = view.findViewById(R.id.textViewMaxParticipants)

                    eventNameTextView.text = event.eventName
                    eventDateTextView.text = event.eventDate
                    eventDetailsTextView.text = event.eventDetails
                    eventLocationTextView.text = event.eventLocation
                    maxParticipantsTextView.text = event.maxParticipants.toString()

                    firestore.collection("User").document(event.createdBy).get()
                        .addOnSuccessListener { document ->
                            val createdByUsername = document.getString("username") ?: "Unknown"
                            eventCreatedByTextView.text = "Created By: $createdByUsername"
                        }
                        .addOnFailureListener {
                            eventCreatedByTextView.text = "Created By: Unknown"
                        }

                    builder.setView(view)
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Hata yönetimi
            }
        })
    }
}
