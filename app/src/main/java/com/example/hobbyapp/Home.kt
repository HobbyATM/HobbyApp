package com.example.hobbyapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class Home : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var eventsAdapter: EventsAdapter
    private lateinit var database: DatabaseReference
    private lateinit var eventList: MutableList<Event>
    private lateinit var eventIds: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        searchView = view.findViewById(R.id.searchView)

        recyclerView.layoutManager = LinearLayoutManager(context)
        eventList = mutableListOf()
        eventIds = mutableListOf()
        eventsAdapter = EventsAdapter(requireContext(), eventList, eventIds)
        recyclerView.adapter = eventsAdapter

        database = FirebaseDatabase.getInstance("https://hobbyapp-75fdb-default-rtdb.europe-west1.firebasedatabase.app").reference.child("Events")

        fetchEvents()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterEvents(newText)
                return true
            }
        })

        return view
    }

    private fun fetchEvents() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventList.clear()
                eventIds.clear()
                for (eventSnapshot in snapshot.children) {
                    val event = eventSnapshot.getValue<Event>()
                    if (event != null) {
                        eventList.add(event)
                        eventIds.add(eventSnapshot.key!!)
                    }
                }
                eventsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load events: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterEvents(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            eventList
        } else {
            eventList.filter {
                it.eventName.contains(query, ignoreCase = true)
            }
        }
        eventsAdapter.updateList(filteredList)
    }
}
