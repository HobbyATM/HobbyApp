package com.example.hobbyapp

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore

class EventDetailsDialog {

    companion object {
        fun show(context: Context, eventId: String) {
            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.dialog_event_details, null)
            builder.setView(dialogView)

            val eventNameTextView: TextView = dialogView.findViewById(R.id.textViewEventName)
            val eventDateTextView: TextView = dialogView.findViewById(R.id.textViewEventDate)
            val eventDetailsTextView: TextView = dialogView.findViewById(R.id.textViewEventDetails)
            val eventLocationTextView: TextView = dialogView.findViewById(R.id.textViewEventLocation)
            val maxParticipantsTextView: TextView = dialogView.findViewById(R.id.textViewEventMaxParticipants)
            val createdByTextView: TextView = dialogView.findViewById(R.id.textViewCreatedBy)

            val database = FirebaseDatabase.getInstance("https://hobbyapp-75fdb-default-rtdb.europe-west1.firebasedatabase.app").reference
            val eventRef = database.child("Events").child(eventId)

            eventRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val event = snapshot.getValue(Event::class.java)
                    event?.let {
                        eventNameTextView.text = it.eventName
                        maxParticipantsTextView.text = "${it.participants.size}/${it.maxParticipants}"
                        eventDateTextView.text = it.eventDate
                        eventDetailsTextView.text = it.eventDetails
                        eventLocationTextView.text = it.eventLocation

                        val firestore = FirebaseFirestore.getInstance()
                        firestore.collection("User").document(it.createdBy).get().addOnSuccessListener { document ->
                            val createdByName = document.getString("username") ?: "Unknown"
                            createdByTextView.text = "Created by: $createdByName"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })

            builder.setPositiveButton("OK", null)
            builder.show()
        }
    }
}
