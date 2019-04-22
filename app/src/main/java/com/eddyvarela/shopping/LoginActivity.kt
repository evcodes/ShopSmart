package com.eddyvarela.shopping

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login2.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest


private var mAuth: FirebaseAuth? = null

class LoginActivity : AppCompatActivity() {

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth?.currentUser
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)
        mAuth = FirebaseAuth.getInstance()

        FirebaseApp.initializeApp(this)

        registerButton.setOnClickListener {
            registerUser()
        }

        loginButton.setOnClickListener {
            loginUser()
        }

    }

    private fun loginUser() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            etEmail.text.toString(), etPassword.text.toString()
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))

            } else {
                Toast.makeText(
                    this@LoginActivity, "Error: " +
                            it.exception?.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.addOnFailureListener {
            Toast.makeText(
                this@LoginActivity,
                "Error: ${it.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun registerUser() {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
            etEmail.text.toString(), etPassword.text.toString()
        ).addOnCompleteListener {
            if (it.isSuccessful) {

                Toast.makeText(
                    this@LoginActivity, "Register success",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@LoginActivity, "Error: " +
                            it.exception?.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.addOnFailureListener {
            Toast.makeText(
                this@LoginActivity,
                "Error: ${it.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}
