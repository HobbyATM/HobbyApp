package com.example.hobbyapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class Home : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var eventsAdapter: EventsAdapter
    private lateinit var eventList: MutableList<Event>
    private lateinit var eventIds: MutableList<String>
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        eventList = mutableListOf()
        eventIds = mutableListOf()
        eventsAdapter = EventsAdapter(requireContext(), eventList, eventIds)
        recyclerView.adapter = eventsAdapter

        database = FirebaseDatabase.getInstance("https://hobbyapp-75fdb-default-rtdb.europe-west1.firebasedatabase.app").reference.child("Events")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventList.clear()
                eventIds.clear()
                for (eventSnapshot in snapshot.children) {
                    val event = eventSnapshot.getValue(Event::class.java)
                    val eventId = eventSnapshot.key
                    if (event != null && eventId != null) {
                        eventList.add(event)
                        eventIds.add(eventId)
                    }
                }
                eventsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        return view
    }
}
