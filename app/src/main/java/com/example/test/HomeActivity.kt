package com.example.test

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

enum class ProviderType {
    BASIC,
    GOOGLE
}

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val db = FirebaseFirestore.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val username: String? = intent.getStringExtra("et_email")
        title = "Inicio"
        val emailTextView: TextView = findViewById<TextView>(R.id.emailTextView)
        val nombreTextView: TextView = findViewById<TextView>(R.id.nameTextView)
        val apellidoTextView: TextView = findViewById<TextView>(R.id.apellidoTextView)
        val addressTextView: TextView = findViewById<TextView>(R.id.addressTextView)
        val btn_logout = findViewById<Button>(R.id.btn_logout)
        val btn_save = findViewById<Button>(R.id.btn_save)
        val btn_get = findViewById<Button>(R.id.btn_get)
        emailTextView.text = username
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

        btn_save.setOnClickListener {
            if (username != null) {

                Toast.makeText(this@HomeActivity, "Subiendo a la db", Toast.LENGTH_SHORT).show()
                db.collection("users").document(username).set(
                        hashMapOf(
                                "nombre" to nombreTextView.text.toString(),
                                "apellido" to apellidoTextView.text.toString(),
                                "address" to addressTextView.text.toString()
                        )
                )
            }
        }

        btn_get.setOnClickListener{
            if (username != null) {
                db.collection("users").document(username).get().addOnSuccessListener {
                    nombreTextView.setText(it.get("nombre") as String?)
                    apellidoTextView.setText(it.get("apellido") as String?)
                    addressTextView.setText(it.get("address") as String?)
                }
            }
        }

    }

}