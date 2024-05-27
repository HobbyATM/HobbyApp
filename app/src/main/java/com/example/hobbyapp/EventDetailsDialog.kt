package com.example.hobbyapp

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore

class EventDetailsDialog {
    companion object {
        fun show(context: Context, eventId: String, showParticipants: Boolean = false) {
            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.dialog_event_details, null)
            builder.setView(dialogView)

            val dialog = builder.create()
            dialog.show()

            val eventNameTextView: TextView = dialogView.findViewById(R.id.textViewEventName)
            val eventDateTextView: TextView = dialogView.findViewById(R.id.textViewEventDate)
            val eventDetailsTextView: TextView = dialogView.findViewById(R.id.textViewEventDetails)
            val eventLocationTextView: TextView = dialogView.findViewById(R.id.textViewEventLocation)
            val eventMaxParticipantsTextView: TextView = dialogView.findViewById(R.id.textViewEventMaxParticipants)
            val createdByTextView: TextView = dialogView.findViewById(R.id.textViewCreatedBy)
            val participantsTextView: TextView = dialogView.findViewById(R.id.textViewParticipants)

            val database: DatabaseReference = FirebaseDatabase.getInstance("https://hobbyapp-75fdb-default-rtdb.europe-west1.firebasedatabase.app").reference
            val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

            database.child("Events").child(eventId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val event = snapshot.getValue(Event::class.java)
                    if (event != null) {
                        eventNameTextView.text = event.eventName
                        eventDateTextView.text = event.eventDate
                        eventDetailsTextView.text = event.eventDetails
                        eventLocationTextView.text = event.eventLocation
                        eventMaxParticipantsTextView.text = "${event.participants.size}/${event.maxParticipants}"

                        firestore.collection("User").document(event.createdBy).get()
                            .addOnSuccessListener { document ->
                                val createdBy = document.getString("username")
                                createdByTextView.text = "Created By: $createdBy"
                            }
                            .addOnFailureListener {
                                createdByTextView.text = "Created By: Unknown"
                            }

                        if (showParticipants) {
                            val participants = event.participants.keys
                            val participantsInfo = StringBuilder()
                            for (participantId in participants) {
                                firestore.collection("User").document(participantId).get()
                                    .addOnSuccessListener { document ->
                                        val participantName = document.getString("username")
                                        val participantPhone = document.getString("phone")
                                        participantsInfo.append("$participantName $participantPhone\n")
                                        participantsTextView.text = participantsInfo.toString()
                                    }
                            }
                        } else {
                            participantsTextView.visibility = View.GONE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }
}
