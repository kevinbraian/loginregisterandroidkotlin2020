package com.example.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType{
    BASIC
}
class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val bundle = intent.extras
        val username = bundle?.getString("et_user_name")
        title = "Inicio"
        val UsernameTextView: TextView = findViewById<TextView>(R.id.UsernameTextView)
        val btn_logout = findViewById<Button>(R.id.btn_logout)
        UsernameTextView.text = username

        btn_logout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

    }





}