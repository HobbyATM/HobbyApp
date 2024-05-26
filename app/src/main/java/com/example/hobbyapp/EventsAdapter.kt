package com.example.hobbyapp

import android.content.Context
import android.util.Log
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

        holder.joinButton.setOnClickListener {
            joinEvent(currentEventId)
        }

        holder.detailsButton.setOnClickListener {
            EventDetailsDialog.show(context, currentEventId)
        }
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    private fun joinEvent(eventId: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val eventRef = database.child("Events").child(eventId).child("participants")

            eventRef.runTransaction(object : Transaction.Handler {
                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                    val participants = mutableData.getValue(object : GenericTypeIndicator<MutableMap<String, Boolean>>() {}) ?: mutableMapOf()

                    if (participants.containsKey(userId)) {
                        return Transaction.success(mutableData) // Zaten katılmış
                    }

                    participants[userId] = true
                    mutableData.value = participants

                    return Transaction.success(mutableData)
                }

                override fun onComplete(databaseError: DatabaseError?, committed: Boolean, dataSnapshot: DataSnapshot?) {
                    if (committed) {
                        // Kullanıcı etkinliğe başarıyla katıldı, Firestore'a ekle
                        firestore.collection("User").document(userId).get().addOnSuccessListener { document ->
                            val joinedEvents = document.get("joinedEvents") as? MutableList<String> ?: mutableListOf()
                            joinedEvents.add(eventId)
                            firestore.collection("User").document(userId)
                                .set(mapOf("joinedEvents" to joinedEvents), SetOptions.merge())
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Etkinliğe başarıyla katıldınız", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Etkinliğe katılamadı: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(context, "Etkinliğe katılamadı: ${databaseError?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }
}
