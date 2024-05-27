package com.example.hobbyapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hobbyapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore

class Profile : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var createdEventsAdapter: CreatedEventsAdapter
    private lateinit var joinedEventsAdapter: JoinedEventsAdapter

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance("https://hobbyapp-75fdb-default-rtdb.europe-west1.firebasedatabase.app").reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        // RecyclerView setup
        binding.createdEventsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.joinedEventsRecyclerView.layoutManager = LinearLayoutManager(context)

        createdEventsAdapter = CreatedEventsAdapter(requireContext(), emptyList(), emptyList())
        joinedEventsAdapter = JoinedEventsAdapter(requireContext(), emptyList(), emptyList())

        binding.createdEventsRecyclerView.adapter = createdEventsAdapter
        binding.joinedEventsRecyclerView.adapter = joinedEventsAdapter

        loadUserProfile()
        loadCreatedEvents()
        loadJoinedEvents()

        return view
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("User").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userName = document.getString("username")
                        val userEmail = document.getString("email")

                        binding.profileName.text = userName
                        binding.profileEmail.text = userEmail
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle error
                }
        }
    }

    private fun loadCreatedEvents() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            database.child("Events").orderByChild("createdBy").equalTo(currentUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val eventList = mutableListOf<Event>()
                        val eventIds = mutableListOf<String>()
                        for (eventSnapshot in snapshot.children) {
                            val event = eventSnapshot.getValue(Event::class.java)
                            if (event != null) {
                                eventList.add(event)
                                eventIds.add(eventSnapshot.key ?: "")
                            }
                        }
                        createdEventsAdapter.updateList(eventList, eventIds)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
        }
    }

    private fun loadJoinedEvents() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("User").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    val joinedEventIds = document.get("joinedEvents") as? List<String> ?: emptyList()

                    database.child("Events").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val eventList = mutableListOf<Event>()
                            val eventIds = mutableListOf<String>()
                            for (eventSnapshot in snapshot.children) {
                                val event = eventSnapshot.getValue(Event::class.java)
                                if (event != null && joinedEventIds.contains(eventSnapshot.key)) {
                                    eventList.add(event)
                                    eventIds.add(eventSnapshot.key ?: "")
                                }
                            }
                            joinedEventsAdapter.updateList(eventList, eventIds)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle error
                        }
                    })
                }
                .addOnFailureListener { exception ->
                    // Handle error
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
