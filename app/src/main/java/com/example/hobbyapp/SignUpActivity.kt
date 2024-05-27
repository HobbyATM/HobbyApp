package com.example.hobbyapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hobbyapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.signupbutton.setOnClickListener {
            val username = binding.usernametext.text.toString()
            val phone = binding.phonetext.text.toString()
            val email = binding.emailtext.text.toString()
            val password = binding.passwordtext.text.toString()
            val confirmPass = binding.passwordrepaettext.text.toString()

            val userMap = hashMapOf(
                "username" to username,
                "phone" to phone,
                "email" to email,
                "password" to password,
                "joinedEvents" to emptyList<String>(),
                "createdBy" to emptyList<String>()
            )

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPass.isNotEmpty() && username.isNotEmpty() && phone.isNotEmpty()) {
                if (password == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val userId = FirebaseAuth.getInstance().currentUser!!.uid

                            db.collection("User").document(userId).set(userMap)
                                .addOnSuccessListener {
                                    val intent = Intent(this, SigninActivity::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Failed!!", Toast.LENGTH_SHORT).show()
                                }

                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields are not allowed!!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
