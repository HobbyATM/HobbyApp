package com.example.hobbyapp

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textViewName: TextView = itemView.findViewById(R.id.textViewName)
    val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
    val buttonJoin: Button = itemView.findViewById(R.id.buttonJoin)
    val buttonDetails: Button = itemView.findViewById(R.id.buttonDetails)
}
