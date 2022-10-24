package com.example.loginscreen

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : MainActivity() {
    private lateinit var Name:EditText
    private lateinit var email:EditText
    private lateinit var password:EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var confirn_password:EditText
    private lateinit var SignUp_b:Button
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userData: UserData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        firestore= FirebaseFirestore.getInstance()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Name=findViewById(R.id.name)
        progressBar=findViewById(R.id.signupprogressbar)

        progressBar.indeterminateDrawable.setColorFilter(Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN)
        email=findViewById(R.id.emailaddresssign)
        password=findViewById(R.id.passwordsign)
        confirn_password=findViewById(R.id.password_final)
        SignUp_b=findViewById(R.id.registerbutton)
        SignUp_b.setOnClickListener { signuobuttonpressed(Name.text.toString(),email.text.toString(),password.text.toString(),confirn_password.text.toString()) }
    }

    private fun signuobuttonpressed(Name1: String, email1: String, password1: String, confirmPassword1: String) {
        if(Name1.isEmpty()){
            Name.error = "Name can't be empty"
            return
        }
        if(email1.isEmpty()){
            email.error = "Email can't be empty"
            return
        }

        if (password1.isEmpty()){
            password.error="Password can't be empty"
            return
        }
        if(confirmPassword1.isEmpty()){
            confirn_password.error="Password can't be empty"
            return
        }
        if(confirmPassword1!=password1){
            confirn_password.error="password and confirm password does not match"
            return
        }
        if (password1.length<6 || confirmPassword1.length<6){
            password.error="password length is small"
            return
        }
        else {
            try {
                progressBar.visibility=View.VISIBLE
                firebaseAuth.createUserWithEmailAndPassword(
                    email1,
                    password1
                )
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(this,MainActivity::class.java))
                            addtofirestore()
                            progressBar.visibility=View.INVISIBLE
                            finish()
                        } else {
                            Toast.makeText(this, task.exception?.message.toString(), Toast.LENGTH_LONG)
                                .show()
                        }
                    }
            } catch (e: Exception) {
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun addtofirestore() {
        userData= UserData(Name.text.toString(),email.text.toString(),password.text.toString())
        firestore.collection("users").document(firebaseAuth.currentUser?.email.toString())
            .set(userData)
            .addOnCompleteListener(OnCompleteListener {
                Toast.makeText(this, "User Added Successfully", Toast.LENGTH_LONG).show()
            })
            .addOnFailureListener(OnFailureListener {
                Toast.makeText(this,it.message.toString(),Toast.LENGTH_LONG).show()
            })

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }
}