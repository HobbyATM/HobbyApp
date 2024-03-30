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
import com.example.hobbyapp.databinding.ActivitySignUpBinding
import com.example.hobbyapp.databinding.ActivitySigninBinding
import com.google.firebase.auth.FirebaseAuth

class SigninActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)


        if (isLoggedIn()) {
            startMainApp()
            return
        }
        binding.gotosignupactivitybutton.setOnClickListener(){
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        binding.signinbutton.setOnClickListener() {
            val email = binding.signinemailtext.text.toString()
            val password = binding.signinpasswordtext.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        saveLoginStatus(true)
                        startMainApp()
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else
            {
                Toast.makeText(this, "Empty Fields are now allowed !!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun saveLoginStatus(status: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", status)
        editor.apply()
    }

    private fun startMainApp() {
        val intent = Intent(this, MainApp::class.java)
        startActivity(intent)
        finish()
    }
}