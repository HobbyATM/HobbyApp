package com.example.hobbyapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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

        binding.bottomNavigationView.selectedItemId = R.id.home
        replaceFragment(Home())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.settings -> replaceFragment(Settings())
                R.id.home -> replaceFragment(Home())
                R.id.profile -> replaceFragment(Profile())
                R.id.add -> replaceFragment(AddEvent())

                else ->{

                }
            }
            true
        }
    }

    private fun fetchUserData(uid: String) {
        db.collection("User").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userName = document.getString("name")
                    val userEmail = document.getString("email")
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

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val  fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }
}
