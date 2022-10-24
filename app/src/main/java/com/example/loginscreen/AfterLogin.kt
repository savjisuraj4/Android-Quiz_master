package com.example.loginscreen

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FieldValue.arrayRemove
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.annotation.Nonnull


class AfterLogin : AppCompatActivity() {
    private var quizcode: EditText?=null
    private lateinit var startbutton: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var attemptedquiz: Boolean?=false
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_after_login)
        quizcode = findViewById(R.id.quizno)
        firebaseAuth=FirebaseAuth.getInstance()
        firestore=FirebaseFirestore.getInstance()
        databaseReference=FirebaseDatabase.getInstance().getReferenceFromUrl("https://login-9e300-default-rtdb.firebaseio.com/")
        startbutton = findViewById(R.id.startquiz)

        startbutton.setOnClickListener { startbuttonpressed(quizcode?.text.toString())}
    }
    private fun checkattempteduser(quizcodes: String) {
        quizcodes.replace(" ", "")

        if (quizcodes.isBlank()) {
            quizcode?.error = "Enter Quiz Code"
        }
        else {
            try {
                firestore.collection(quizcodes).get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                    if (task.result.documents.toString().contains(firebaseAuth.currentUser?.email.toString())){
                        attemptedquiz=true
                        AlertDialog.Builder(this)
                            .setTitle(R.string.app_name)
                            .setIcon(R.drawable.ic_baseline_error_24)
                            .setMessage("You have been already attempted the quiz")
                            .setNeutralButton("Ok", DialogInterface.OnClickListener { dialog, which ->
                                resultActivity(quizcodes)
                            }).create().show()

                    }
                        else{
                        val intent =
                            Intent(applicationContext, quizquestionActivity::class.java)
                        intent.putExtra("quizcode", quizcodes)
                        startActivity(intent)
                    }

                    } else {
                        Toast.makeText(
                            this,
                            task.exception?.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this,  e.message.toString(), Toast.LENGTH_LONG).show()
            } catch (e: FirebaseFirestoreException) {
                Toast.makeText(this,  e.message.toString(), Toast.LENGTH_LONG).show()

            }
        }
    }
    private fun startbuttonpressed(quizcodes:String) {
        try {
                try {
                    databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.child(quizcodes).exists()) {
                                checkquizvalidty(quizcodes)
                            } else {
                                AlertDialog.Builder(this@AfterLogin)
                                    .setTitle(R.string.app_name)
                                    .setIcon(R.drawable.ic_baseline_error_24)
                                    .setMessage("Quiz code -$quizcodes does not exist.\n\nPlease enter the correct quiz code")
                                    .setNeutralButton("Ok", DialogInterface.OnClickListener { dialog, which ->
                                    }).create().show()

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG)
                                .show()
                        }

                    })

                } catch (e: Exception) {
                    Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
                } catch (e: FirebaseFirestoreException) {
                    Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()

                }

        }catch (e:Exception){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show()
        }
    }
    private fun checkquizvalidty(quizcodes: String){
        databaseReference.child(quizcodes).addListenerForSingleValueEvent(object :ValueEventListener{
            @SuppressLint("SimpleDateFormat")
            override fun onDataChange(snapshot: DataSnapshot) {
                val enddate=snapshot.child("enddate").value.toString()
                val simpleDateFormat= SimpleDateFormat("dd/MM/yyyy")
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val cuurentdate:Date=simpleDateFormat.parse(LocalDate.now().format(formatter))
                val enddate1: Date =simpleDateFormat.parse(enddate.toString())
                val difference :Long= (enddate1.time-cuurentdate.time)

                val differenceDates = difference / (24 * 60 * 60 * 1000)

                if(differenceDates<0){
                    deleteQuiz(quizcodes)
                }
                else{
                    checkattempteduser(quizcodes)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,error.message.toString(),Toast.LENGTH_LONG).show()
            }

        })
    }
    private fun resultActivity(quizcodes: String){
        try{
        firestore.collection(quizcodes).document(firebaseAuth.currentUser?.email.toString()).get().addOnCompleteListener() { task ->

            if (task.isComplete) {
                val intent1 = Intent(applicationContext, ResultActivity::class.java)
                intent1.putExtra("Correctanswer",task.result["right"].toString())
                intent1.putExtra("InCorrectanswer",task.result["wrong"].toString())
                intent1.putExtra("No_of_equcation",task.result["total"].toString())
                intent1.putExtra("QuizCode",quizcodes).toString()
                startActivity(intent1)
            }
        }
        }catch (e:Exception){
            Toast.makeText(this,  e.message.toString(), Toast.LENGTH_LONG).show()

        }
    }
    override fun onBackPressed() {
        startActivity(Intent(applicationContext,MainActivity::class.java))
    }
    fun deleteQuiz(quizcodes: String){
        try {
                                        databaseReference.child(quizcodes)
                                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                                override fun onDataChange(@Nonnull snapshot: DataSnapshot) {
                                                    snapshot.ref.removeValue()
                                                        .addOnCompleteListener(
                                                            OnCompleteListener {
                                                                Toast.makeText(
                                                                    applicationContext,
                                                                    "Quiz does not exist",
                                                                    Toast.LENGTH_LONG
                                                                ).show()
                                                            }
                                                        )
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    AlertDialog.Builder(applicationContext)
                                                        .setIcon(R.drawable.ic_baseline_error_24)
                                                        .setMessage(error.message.toString())
                                                        .setTitle("Error")
                                                        .setNeutralButton("OK") { dialog, which ->
                                                        }
                                                        .create().show()
                                                }

                                            })






        }catch (e:Exception){
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
        }
    }
}