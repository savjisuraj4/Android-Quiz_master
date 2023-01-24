package com.example.loginscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel


class ResultActivity : AppCompatActivity() {
    private lateinit var pieChart: PieChart
    private lateinit var correct: TextView
    private lateinit var no_question:TextView
    private lateinit var incorrect: TextView
   private lateinit var skip: TextView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var homeresult:Button
    private lateinit var quizcode:String
    private lateinit var attemptedclass: attemptedclass
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        firestore=FirebaseFirestore.getInstance()
        firebaseAuth=FirebaseAuth.getInstance()
        correct = findViewById(R.id.correct)
        homeresult=findViewById(R.id.homeresult)
        incorrect = findViewById(R.id.incoorect)
        skip = findViewById(R.id.skipped)
        no_question = findViewById(R.id.Total)
        pieChart = findViewById(R.id.piechart)
        val bundle: Bundle? = intent.extras
        homeresult.setOnClickListener { onBackPressed() }
        try {

            val correctquestionIn = bundle?.get("Correctanswer").toString().toInt()
            val IncorrectquestionIn = bundle?.get("InCorrectanswer").toString().toInt()
            val totalquestionIn = bundle?.get("No_of_equcation").toString().toInt()
            quizcode=bundle?.get("QuizCode").toString()

            correct.text= correctquestionIn.toString()
            incorrect.text = IncorrectquestionIn.toString()
            skip.text = (totalquestionIn - (correctquestionIn + IncorrectquestionIn)).toString()
            no_question.text = totalquestionIn.toString()
        pieChart.innerPadding= 50F
        pieChart.addPieSlice(
            PieModel(
                "Correct", correct.text.toString().toInt().toFloat(),
                Color.GREEN
            )
        )
        pieChart.addPieSlice(
            PieModel(
                "Incorrect", incorrect.text.toString().toFloat(),
                Color.RED
            )
        )
        pieChart.addPieSlice(
            PieModel(
                "Skip", skip.text.toString().toFloat(),
                Color.DKGRAY
            )
        )
            firestore.collection(quizcode).get().addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    val ss=task.result.documents.toString().contains(firebaseAuth.currentUser?.email.toString())
                    if (!ss) {
                        addusertodatabase(quizcode)
                    }
                }
                }
        }catch (e:Exception){
            Toast.makeText(this,e.message.toString(),Toast.LENGTH_LONG).show()
        }
    }
    private fun addusertodatabase(quizCodes:String){

        try {
            firestore.collection("users").document(firebaseAuth.currentUser?.email.toString())
                    .get().addOnCompleteListener(OnCompleteListener { task ->
                        attemptedclass = attemptedclass(

                            firebaseAuth.currentUser?.email.toString(),
                            true,
                            correct.text.toString(),
                            incorrect.text.toString(),
                            skip.text.toString(),
                            no_question.text.toString()
                        )
                        firestore.collection(quizCodes)
                            .document(firebaseAuth.currentUser?.email.toString())
                            .set(attemptedclass)
                            .addOnSuccessListener(OnSuccessListener {
                            })
                            .addOnFailureListener(OnFailureListener { e ->
                                Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            })

                    })


        }catch (e:Exception){
            Toast.makeText(this,e.message.toString(),Toast.LENGTH_LONG).show()
        }
    }
    override fun onBackPressed() {
        startActivity(Intent(applicationContext,AfterLogin::class.java))
    }
}