package com.example.hobbyapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hobbyapp.databinding.ActivityMainAppBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainApp : AppCompatActivity() {
    private lateinit var binding: ActivityMainAppBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        firebaseAuth = FirebaseAuth.getInstance()

        val uid = sharedPreferences.getString("uid", null)

        Toast.makeText(this, "Kullanıcı ID == $uid", Toast.LENGTH_SHORT).show()
        if (uid != null) {
            fetchUserData(uid)
        } else {
            Toast.makeText(this, "Kullanıcı ID bulunamadı", Toast.LENGTH_SHORT).show()
        }

        binding.logoutbutton.setOnClickListener {
            saveLoginStatus(false)
            firebaseAuth.signOut()
            startActivity(Intent(this, SigninActivity::class.java))
            Toast.makeText(this, "Çıkış yapılıyor", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun fetchUserData(uid: String) {
        db.collection("User").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userName = document.getString("name")
                    val userEmail = document.getString("email")
                    binding.userNameTextView.text = userName
                    binding.userEmailTextView.text = userEmail
                } else {
                    Toast.makeText(this, "Belge bulunamadı", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Veri çekme hatası: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveLoginStatus(status: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", status)
        editor.apply()
    }
}
