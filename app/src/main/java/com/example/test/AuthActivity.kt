package com.example.test

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.*
import com.google.firebase.auth.GoogleAuthProvider
import java.util.regex.Pattern
import java.util.regex.Pattern.compile as compile1

class AuthActivity : AppCompatActivity() {
    override fun onStart() {
        val auth_layout = findViewById<LinearLayout>(R.id.AuthLayout)
        super.onStart()
        auth_layout.visibility = View.VISIBLE

    }
    
    private fun validation(pass: String): Boolean {
        val UpperCasePattern = compile1("[A-Z ]")
        val lowerCasePattern  = compile1("[a-z ]")
        val digitCasePattern = compile1("[0-9 ]")
        val specialCharPattern = compile1("[/*!@#$%^&()¨{}|?<>]")
        val et_user_name = findViewById<EditText>(R.id.et_user)
        val et_password = findViewById<EditText>(R.id.et_password)
        val user_name = et_user_name.text
        val password = et_password.text
        val caracteres = listOf('"', '\'', ',', 'ü', 'Ü', 'á', 'é', 'í', 'ó', 'ú', 'Á', 'É', 'Í', 'Ó', 'Ú', '\n', '\r')
        val flag = true

        if (user_name.isEmpty() or password.isEmpty()) {
            Toast.makeText(this@AuthActivity, "el usuario y/o contraseña no debe estar vacio", Toast.LENGTH_SHORT).show()
            return false
        }
        if ((user_name.length < 8) or (password.length < 8)) {
            Toast.makeText(this@AuthActivity, "El usuario y/o contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }
        for (x in caracteres.indices) {
            if ((user_name.indexOf(caracteres[x]) != -1) or (password.indexOf(caracteres[x]) != -1)) {
                Toast.makeText(this@AuthActivity, "El usuario no puede tener los siguientes caracteres : Comillas, punto y coma, diéresis, tildes, escapes", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        if (!UpperCasePattern.matcher(pass).find()) {
            Toast.makeText(this@AuthActivity, "La contraseña debe tener una mayúscula", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!lowerCasePattern.matcher(pass).find()) {
            Toast.makeText(this@AuthActivity, "La contraseña debe tener una minúscula", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!digitCasePattern.matcher(pass).find()) {
            Toast.makeText(this@AuthActivity, "La contraseña debe tener un número", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!specialCharPattern.matcher(pass).find()) {
            Toast.makeText(this@AuthActivity, "La contraseña debe tener un caracter especial", Toast.LENGTH_SHORT).show()
            return false
        }
        return flag
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        analytics.logEvent("InitScreen", bundle)

        /////////////////////////////
        title = "Autenticación"

        val et_user_name = findViewById<EditText>(R.id.et_user)
        val et_password = findViewById<EditText>(R.id.et_password)
        val btn_submit = findViewById<Button>(R.id.btn_login)
        val btn_register = findViewById<Button>(R.id.btn_register)
        val btn_google = findViewById<Button>(R.id.googleButton)
        val user_name = et_user_name.text
        val password = et_password.text
        val user = user_name.toString()
        val GOOGLE_SIGN_IN = 1

        btn_google.setOnClickListener {
            //Configuracion
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail().build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
            session()
        }

        btn_submit.setOnClickListener {

            if (validation(password.toString())){
                getInstance().signInWithEmailAndPassword(user_name.toString(), password.toString()).addOnCompleteListener{
                    if (it.isSuccessful) {
                        Toast.makeText(this@AuthActivity, getString(R.string.logsucc), Toast.LENGTH_SHORT).show()
                        showHome(user)
                    } else {

                        showAlert()
                    }
                }
            }
            //Se logueara el usuario, si existe lo lleva al Home, si no, lo notifica.
        }

        btn_register.setOnClickListener {

            if (validation(password.toString())) {
                Toast.makeText(this@AuthActivity, "Register Successful", Toast.LENGTH_SHORT).show()
                //Se creara el usuario
                getInstance().createUserWithEmailAndPassword(user, password.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(user)
                    } else {
                        this.showAlert()
                    }
                }
            }
        }


    }

    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE)
        val email = prefs.getString("et_user_name", null)
        if (email != null) {
            val auth_layout = findViewById<LinearLayout>(R.id.AuthLayout)
            auth_layout.visibility = View.INVISIBLE
            showHome(email)
        }
    }

    /////////////////////////////////////////////////
    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error al iniciar sesión, revise el usuario y/o contraseña")
        builder.setPositiveButton("aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    /////////////////////////////


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val GOOGLE_SIGN_IN = 1
        if (requestCode == GOOGLE_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val user = findViewById<EditText>(R.id.et_user).text.toString()
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            showHome(user)
                        } else {
                            showAlert()
                        }
                    }
                }
            } catch (e: ApiException) {
                showAlert()
            }
        }

    }

    private fun showHome(et_user_name: String) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("et_user_name", et_user_name)
        }
        this.startActivity(homeIntent)
    }
}