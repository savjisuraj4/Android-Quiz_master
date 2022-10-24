package com.example.loginscreen

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.firestore.FirebaseFirestore

class ForgotActivity : MainActivity() {
    lateinit var emailforgot: EditText
    lateinit var sendotpbutton: Button
    lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        firestore=FirebaseFirestore.getInstance()
        emailforgot = findViewById(R.id.emailaddressforgot)
        sendotpbutton = findViewById(R.id.sendemaillinkbitton)
        sendotpbutton.setOnClickListener { forgotpassword(emailforgot.text.toString()) }
    }

    private fun forgotpassword(email: String) {
        if (email.isEmpty()) {
            emailforgot.error=("Please enter Registered Email")
        } else {
            try {

                        firebaseAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                                if (task.isSuccessful) {
                                    AlertDialog.Builder(this)
                                        .setIcon(R.drawable.ic_baseline_verified_user_24)
                                        .setMessage("Password Reset link is sent to your email\n\n" +
                                                "Note:-Please check your SPAM section")
                                        .setNeutralButton("Ok", DialogInterface.OnClickListener{
                                                dialog, which ->
                                        })
                                        .create().show()
                                } else {
                                    Toast.makeText(
                                        this,
                                        task.exception?.message.toString(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }).addOnFailureListener(OnFailureListener {
                                Toast.makeText(this, it.message.toString(), Toast.LENGTH_LONG).show()
                            })


            } catch (e: RuntimeException) {
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()

            }
        }
    }
        override fun onContextItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                android.R.id.home -> {
                    finish()
                    return true
                }
            }
            return super.onContextItemSelected(item)
        }
    }

