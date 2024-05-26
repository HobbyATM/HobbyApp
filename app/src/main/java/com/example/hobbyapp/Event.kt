package com.example.hobbyapp

data class Event(
    val eventName: String = "",
    val maxParticipants: Int = 0,
    val eventDate: String = "",
    val eventDetails: String = "",
    val eventLocation: String = "",
    val createdBy: String = "",
    val participants: Map<String, Boolean> = emptyMap()
)
