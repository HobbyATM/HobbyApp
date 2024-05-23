package com.example.hobbyapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
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
    private lateinit var selectLocationButton: Button
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var selectedLocation: String? = null

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
        selectLocationButton = view.findViewById(R.id.selectLocationButton)

        // Firebase Database referansını al
        database = FirebaseDatabase.getInstance("https://hobbyapp-75fdb-default-rtdb.europe-west1.firebasedatabase.app").reference.child("Events")
        auth = FirebaseAuth.getInstance()

        // Places API başlatma
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "YOUR_API_KEY")
        }

        // Tarih seçici dialogu açma
        eventDateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        // Konum seçimi buton tıklama olayını ayarla
        selectLocationButton.setOnClickListener {
            openPlacePicker()
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
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                eventDateEditText.setText(selectedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private val getLocation = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(result.data!!)
            selectedLocation = place.latLng.toString()
            eventLocationEditText.setText(place.name)
        } else if (result.resultCode == AutocompleteActivity.RESULT_ERROR) {
            val status = Autocomplete.getStatusFromIntent(result.data!!)
            Toast.makeText(context, "Hata: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openPlacePicker() {
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(requireContext())
        getLocation.launch(intent)
    }

    private fun createEvent() {
        val eventName = eventNameEditText.text.toString().trim()
        val maxParticipants = maxParticipantsEditText.text.toString().trim().toIntOrNull() ?: 0
        val eventDate = eventDateEditText.text.toString().trim()
        val eventDetails = eventDetailsEditText.text.toString().trim()
        val eventLocation = eventLocationEditText.text.toString().trim()

        if (eventName.isEmpty() || eventDate.isEmpty() || eventDetails.isEmpty() || eventLocation.isEmpty()) {
            Toast.makeText(context, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(context, "Oturum açmanız gerekiyor", Toast.LENGTH_SHORT).show()
            return
        }

        val eventId = database.push().key

        if (eventId != null) {
            val event = Event(eventName, maxParticipants, eventDate, eventDetails, eventLocation, currentUser.uid, listOf(currentUser.uid))
            database.child(eventId).setValue(event)
                .addOnSuccessListener {
                    Toast.makeText(context, "Etkinlik başarıyla oluşturuldu", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Etkinlik oluşturulamadı: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
