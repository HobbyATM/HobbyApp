package com.example.hobbyapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hobbyapp.databinding.ActivityMainAppBinding
import com.example.hobbyapp.databinding.ActivitySigninBinding
import com.google.firebase.auth.FirebaseAuth

class MainApp : AppCompatActivity() {
    private lateinit var binding: ActivityMainAppBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.logoutbutton.setOnClickListener(){
            saveLoginStatus(false)
            firebaseAuth.signOut()
            startActivity(Intent(this, SigninActivity::class.java))
            Toast.makeText(this, "Çıkış yapılıyor", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    private fun saveLoginStatus(status: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", status)
        editor.apply()
    }
}