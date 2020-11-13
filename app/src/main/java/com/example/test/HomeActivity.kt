package com.example.test

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType {
    BASIC,
    GOOGLE
}

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val bundle = intent.extras
        val username = bundle?.getString("et_user_name")
        title = "Inicio"
        val UsernameTextView: TextView = findViewById<TextView>(R.id.UsernameTextView)
        val btn_logout = findViewById<Button>(R.id.btn_login)
        UsernameTextView.text = username
        //Guardado de datos
        // fichero
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("username", username)
        prefs.apply()

        btn_logout.setOnClickListener {
            prefs.clear()
            prefs.apply()
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

    }

}