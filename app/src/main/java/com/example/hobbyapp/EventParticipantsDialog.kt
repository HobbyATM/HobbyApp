package com.example.hobbyapp

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.database.*

class EventParticipantsDialog(context: Context, private val event: Event) : Dialog(context) {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_event_participants)

        database = FirebaseDatabase.getInstance("https://hobbyapp-75fdb-default-rtdb.europe-west1.firebasedatabase.app").reference

        val textViewParticipants = findViewById<TextView>(R.id.textViewParticipants)

        database.child("Events").child(event.eventId).child("participants")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val participants = mutableListOf<String>()
                    for (participantSnapshot in snapshot.children) {
                        val userId = participantSnapshot.key
                        if (userId != null) {
                            database.child("Users").child(userId).get().addOnSuccessListener {
                                val userName = it.child("username").value.toString()
                                val userPhone = it.child("phone").value.toString()
                                participants.add("$userName - $userPhone")
                                textViewParticipants.text = participants.joinToString("\n")
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }
}
