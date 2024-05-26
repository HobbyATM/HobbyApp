package com.example.hobbyapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class EventsAdapter(private val context: Context, private val eventList: List<Event>, private val eventIds: List<String>) : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventName: TextView = itemView.findViewById(R.id.textViewName)
        val eventDate: TextView = itemView.findViewById(R.id.textViewDate)
        val joinButton: Button = itemView.findViewById(R.id.buttonJoin)
        val joinedText: TextView = itemView.findViewById(R.id.textViewJoined)
        val detailsButton: Button = itemView.findViewById(R.id.buttonDetails)
    }

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance("https://hobbyapp-75fdb-default-rtdb.europe-west1.firebasedatabase.app").reference
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentEvent = eventList[position]
        val currentEventId = eventIds[position]

        holder.eventName.text = currentEvent.eventName
        holder.eventDate.text = currentEvent.eventDate

        checkIfJoined(currentEventId, holder)

        holder.joinButton.setOnClickListener {
            joinEvent(currentEventId, holder)
        }

        holder.detailsButton.setOnClickListener {
            EventDetailsDialog.show(context, currentEventId)
        }
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    private fun checkIfJoined(eventId: String, holder: EventViewHolder) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val eventRef = database.child("Events").child(eventId).child("participants")

            eventRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(userId)) {
                        holder.joinButton.visibility = View.GONE
                        holder.joinedText.visibility = View.VISIBLE
                    } else {
                        holder.joinButton.visibility = View.VISIBLE
                        holder.joinedText.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    private fun joinEvent(eventId: String, holder: EventViewHolder) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val eventRef = database.child("Events").child(eventId).child("participants")

            eventRef.runTransaction(object : Transaction.Handler {
                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                    val participants = mutableData.getValue(object : GenericTypeIndicator<MutableMap<String, Boolean>>() {}) ?: mutableMapOf()

                    if (participants.containsKey(userId)) {
                        return Transaction.success(mutableData) // Already joined
                    }

                    participants[userId] = true
                    mutableData.value = participants

                    return Transaction.success(mutableData)
                }

                override fun onComplete(databaseError: DatabaseError?, committed: Boolean, dataSnapshot: DataSnapshot?) {
                    if (committed) {
                        // User successfully joined the event, add to Firestore
                        firestore.collection("User").document(userId).get().addOnSuccessListener { document ->
                            val joinedEvents = document.get("joinedEvents") as? MutableList<String> ?: mutableListOf()
                            joinedEvents.add(eventId)
                            firestore.collection("User").document(userId)
                                .set(mapOf("joinedEvents" to joinedEvents), SetOptions.merge())
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Successfully joined the event", Toast.LENGTH_SHORT).show()
                                    holder.joinButton.visibility = View.GONE
                                    holder.joinedText.visibility = View.VISIBLE
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Failed to join the event: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(context, "Failed to join the event: ${databaseError?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }
}
