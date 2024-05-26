package com.example.hobbyapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventsAdapter(private val context: Context, private val eventList: List<Event>, private val eventIds: List<String>) : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventName: TextView = itemView.findViewById(R.id.textViewName)
        val eventDate: TextView = itemView.findViewById(R.id.textViewDate)
        val joinButton: Button = itemView.findViewById(R.id.buttonJoin)
        val detailsButton: Button = itemView.findViewById(R.id.buttonDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentEvent = eventList[position]
        val currentEventId = eventIds[position]
        holder.eventName.text = currentEvent.eventName
        holder.eventDate.text = currentEvent.eventDate

        holder.detailsButton.setOnClickListener {
            val dialog = EventDetailsDialog(context, currentEventId)
            dialog.show()
        }

        // Join button functionality can be implemented here
    }

    override fun getItemCount() = eventList.size
}