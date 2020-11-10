package com.example.test

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.regex.Pattern

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        analytics.logEvent("InitScreen", bundle)

        /////////////////////////////
            title = "Autenticación"

            val et_user_name = findViewById<EditText>(R.id.et_user_name)
            val et_password = findViewById<EditText>(R.id.et_password)
            val btn_submit = findViewById<Button>(R.id.btn_logout)
            val btn_register = findViewById<Button>(R.id.btn_register)
            val user_name = et_user_name.text
            val password = et_password.text
            val caracteres = listOf<Char>('"', '\'', ',', 'ü', 'Ü', 'á', 'é', 'í', 'ó', 'ú', 'Á', 'É', 'Í', 'Ó', 'Ú', '\n', '\r')
            val pass = password.toString()
            val UpperCasePattern: Pattern = Pattern.compile("[A-Z ]")
            val lowerCasePattern: Pattern = Pattern.compile("[a-z ]")
            val digitCasePattern = Pattern.compile("[0-9 ]")
            val specialCharPattern = Pattern.compile("[/*!@#$%^&*()¨{}|?<>]")
            btn_submit.setOnClickListener  {
                
                var flag= true
                if(user_name.isEmpty() or password.isEmpty()){
                    Toast.makeText(this@AuthActivity, "el usuario y/o contraseña no debe estar vacio", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if((user_name.length < 8) or (password.length < 8)){
                    Toast.makeText(this@AuthActivity, "El usuario y/o contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                for (x in caracteres.indices){
                    if ((user_name.indexOf(caracteres[x]) != -1) or (password.indexOf(caracteres[x]) != -1)) {
                        Toast.makeText(this@AuthActivity, "El usuario no puede tener los siguientes caracteres : Comillas, punto y coma, diéresis, tildes, escapes", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
                if(!UpperCasePattern.matcher(pass).find()) {
                    flag = false
                }
                if (flag){
                    Toast.makeText(this@AuthActivity, "La contraseña debe tener una mayuscula", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                flag = true
                if(!lowerCasePattern.matcher(pass).find()) {
                    flag = false
                }
                if (flag){
                    Toast.makeText(this@AuthActivity, "La constraseña debe tener una minuscula", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                flag = true
                if(!digitCasePattern.matcher(pass).find()) {
                    flag = false
                }
                if (flag){
                    Toast.makeText(this@AuthActivity, "La constraseña debe tener un numero", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                flag = true
                if(!specialCharPattern.matcher(pass).find()) {
                    flag = false
                }
                if (flag){
                    Toast.makeText(this@AuthActivity, "La constraseña debe tener un caracter especial", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }


                //Se logueara el usuario, si existe lo lleva al Home, si no, lo notifica.
                FirebaseAuth.getInstance().signInWithEmailAndPassword(user_name.toString(), password.toString()).addOnCompleteListener{
                    if (it.isSuccessful) {
                        Toast.makeText(this@AuthActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                        showHome(it.result?.user)
                    } else {
                        showAlert()
                    }
                }
            }

            btn_register.setOnClickListener{

                var flag= true

                if(user_name.isEmpty() or password.isEmpty()){
                    Toast.makeText(this@AuthActivity, "el usuario y/o contraseña no debe estar vacio", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if((user_name.length < 8) or (password.length < 8)){
                    Toast.makeText(this@AuthActivity, "El usuario y/o contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                for (x in caracteres.indices){
                    if ((user_name.indexOf(caracteres[x]) != -1) or (password.indexOf(caracteres[x]) != -1)) {
                        Toast.makeText(this@AuthActivity, "El usuario no puede tener los siguientes caracteres : Comillas, punto y coma, diéresis, tildes, escapes", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }

                if(!UpperCasePattern.matcher(pass).find()) {
                        flag = false
                }
                if (flag){
                    Toast.makeText(this@AuthActivity, "La contraseña debe tener una mayuscula", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                flag = true
                if(!lowerCasePattern.matcher(pass).find()) {
                    flag = false
                }
                if (flag){
                    Toast.makeText(this@AuthActivity, "La constraseña debe tener una minuscula", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                flag = true
                if(!digitCasePattern.matcher(pass).find()) {
                    flag = false
                }
                if (flag){
                    Toast.makeText(this@AuthActivity, "La constraseña debe tener un numero", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                flag = true
                if(!specialCharPattern.matcher(pass).find()) {
                    flag = false
                }
                if (flag){
                    Toast.makeText(this@AuthActivity, "La constraseña debe tener un caracter especial", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                Toast.makeText(this@AuthActivity, "Register Successful", Toast.LENGTH_SHORT).show()
                //Se creara el usuario
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(user_name.toString(), password.toString()).addOnCompleteListener{
                    if (it.isSuccessful) {
                        showHome(it.result?.user)
                    } else {
                        showAlert()
                    }
                }
            }



    }
        /////////////////////////////////////////////////
        private fun showAlert() {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Error")
            builder.setMessage("Se ha producido un error logeando")
            builder.setPositiveButton("aceptar", null)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    /////////////////////////////
    private fun showHome(et_user_name: FirebaseUser?){
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("et_user_name", et_user_name)
        }
        this.startActivity(homeIntent)
    }
}


