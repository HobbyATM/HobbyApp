package com.example.hobbyapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JoinedEventsAdapter(
    private val context: Context,
    private var eventList: List<Event>,
    private var eventIds: List<String> // Event IDs to pass to the dialog
) : RecyclerView.Adapter<JoinedEventsAdapter.EventViewHolder>() {

    inner class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewName: TextView = view.findViewById(R.id.textViewName)
        val textViewDate: TextView = view.findViewById(R.id.textViewDate)
        val buttonDetails: Button = view.findViewById(R.id.buttonDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_joined_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]
        val eventId = eventIds[position] // Get the corresponding event ID
        holder.textViewName.text = event.eventName
        holder.textViewDate.text = event.eventDate

        holder.buttonDetails.setOnClickListener {
            EventDetailsDialog.show(context, eventId) // Use the static method to show the dialog
        }
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    fun updateList(newList: List<Event>, newEventIds: List<String>) {
        eventList = newList
        eventIds = newEventIds // Update the event IDs
        notifyDataSetChanged()
    }
}
