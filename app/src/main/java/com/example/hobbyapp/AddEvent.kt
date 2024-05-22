package com.example.hobbyapp

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class AddEvent : Fragment() {

    private lateinit var eventNameEditText: EditText
    private lateinit var maxParticipantsEditText: EditText
    private lateinit var eventDateEditText: EditText
    private lateinit var eventDetailsEditText: EditText
    private lateinit var eventLocationEditText: EditText
    private lateinit var createEventButton: Button
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_event, container, false)

        // UI bileşenlerini bağlama
        eventNameEditText = view.findViewById(R.id.eventName)
        maxParticipantsEditText = view.findViewById(R.id.maxParticipants)
        eventDateEditText = view.findViewById(R.id.eventDateEditText)
        eventDetailsEditText = view.findViewById(R.id.eventDetails)
        eventLocationEditText = view.findViewById(R.id.eventLocation)
        createEventButton = view.findViewById(R.id.createEventButton)

        // Firebase Database referansını al
        database = FirebaseDatabase.getInstance("https://hobbyapp-75fdb-default-rtdb.europe-west1.firebasedatabase.app").reference.child("Events")
        Toast.makeText(context, "database adı == $database", Toast.LENGTH_SHORT).show()
        // Tarih seçici dialogu açma
        eventDateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        // Buton tıklama olayını ayarla
        createEventButton.setOnClickListener {

            createEvent()
        }

        return view
    }
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Seçilen tarihi EditText'e yazma
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                eventDateEditText.setText(selectedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun createEvent() {
        val eventName = eventNameEditText.text.toString().trim()
        val maxParticipants = maxParticipantsEditText.text.toString().trim().toIntOrNull() ?: 0
        val eventDate = eventDateEditText.text.toString().trim()
        val eventDetails = eventDetailsEditText.text.toString().trim()
        val eventLocation = eventLocationEditText.text.toString().trim()

        if (eventName.isEmpty() || eventDate.isEmpty() || eventDetails.isEmpty() || eventLocation.isEmpty()) {
            // Hata mesajı göster
            Toast.makeText(context, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            return
        }

        val eventId = database.push().key



        if (eventId != null) {
            Toast.makeText(context, "database adı == $eventId", Toast.LENGTH_SHORT).show()
            val event = Event(eventName, maxParticipants, eventDate, eventDetails, eventLocation)
            database.child(eventId).setValue(event)
                .addOnSuccessListener {
                    Toast.makeText(context, "Etkinlik başarıyla oluşturuldu", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Etkinlik oluşturulamadı: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
        else{
            Toast.makeText(context, "eventıd girmedi.", Toast.LENGTH_SHORT).show()
        }
    }
}
