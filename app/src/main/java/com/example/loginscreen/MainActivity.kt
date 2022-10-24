package com.example.loginscreen

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


@Suppress("DEPRECATION")
open class MainActivity : AppCompatActivity() {
    private lateinit var signup:TextView
    private  var emailaddress:EditText?=null
    lateinit var loginprogressbar: ProgressBar
    private lateinit var passwordtext:EditText
    private lateinit var login:Button
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var forgotpassword:TextView
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        forgotpassword=findViewById(R.id.forgot_password)
        signup=findViewById(R.id.signup)
        login=findViewById(R.id.login_button)
        emailaddress=findViewById(R.id.emailaddress)
        loginprogressbar=findViewById(R.id.loginprogressbar)
        loginprogressbar.indeterminateDrawable.setColorFilter(Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN)
        passwordtext=findViewById(R.id.password)
        firebaseAuth=FirebaseAuth.getInstance()
        signup.text=Html.fromHtml("<font face =${R.font.poppins_thin} color=#FF000000>Don't have a Account?</font> <font color=#EF420B>Register</font>")
        login.setOnClickListener { firebaseAuthWithEmail(emailaddress?.text.toString(),passwordtext.text.toString()) }
        try {
            signup.setOnClickListener {
                startActivity(Intent(this, SignUp::class.java))
            }
            forgotpassword.setOnClickListener {
                startActivity(Intent(this,ForgotActivity::class.java))
            }
        }catch (e:RuntimeException){
            Toast.makeText(applicationContext,e.message.toString(),Toast.LENGTH_LONG).show()
        }

    }

    private fun firebaseAuthWithEmail(email:String,password:String) {
        try {
            when {
                email.isEmpty() -> emailaddress?.error = ("Please enter email address")
                password.isEmpty()->passwordtext.error=("Please enter password")
                else ->{
                    loginprogressbar.visibility=View.VISIBLE
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(
                            this
                        ) { task ->
                                if (task.isSuccessful) {
                                    isverified()
                                } else {
                                    Toast.makeText(
                                        this@MainActivity, task.exception?.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                        }
                        .addOnFailureListener(this) {
                            loginprogressbar.visibility=View.INVISIBLE
                            Toast.makeText(this, it.message.toString(), Toast.LENGTH_LONG).show()
                        }
                }
            }
        }catch (e:RuntimeException){
            Toast.makeText(this,e.message.toString(),Toast.LENGTH_LONG).show()
        }
    }

    private fun isverified() {
        if (firebaseAuth.currentUser?.isEmailVerified == true){
            Toast.makeText(this@MainActivity, "Login successful", Toast.LENGTH_SHORT).show()
            loginprogressbar.visibility=View.INVISIBLE
            startActivity(Intent(this,AfterLogin::class.java))
        }
        else{
            loginprogressbar.visibility=View.INVISIBLE
            val alertDialogBuilder=AlertDialog.Builder(this)
            alertDialogBuilder
                .setTitle("Verification")
                .setIcon(R.drawable.ic_baseline_verified_user_24)
                .setMessage("Your Email is not verified")
                .setPositiveButton("Verify", DialogInterface.OnClickListener { dialog, which ->
                    firebaseAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener(){
                        task->
                        if (task.isSuccessful) {
                            AlertDialog.Builder(this)
                                .setTitle("Verification")
                                .setIcon(R.drawable.ic_baseline_verified_user_24)
                                .setMessage("Email verification link is sent to your email\n\n" +
                                        "Note:-Please check your SPAM section")
                                .setNeutralButton("Ok",DialogInterface.OnClickListener{
                                        dialog, which ->
                                })
                                .create().show()                        }
                        else{
                            Toast.makeText(this, task.exception?.message.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                        ?.addOnFailureListener{
                            Toast.makeText(this, it.message.toString(), Toast.LENGTH_LONG).show()
                        }
                })
                .setNegativeButton("No",DialogInterface.OnClickListener {dialog,which->
                    dialog.dismiss()
                })
                .create().show()

        }
    }

    override fun onBackPressed() {
        finish()
    }
}
