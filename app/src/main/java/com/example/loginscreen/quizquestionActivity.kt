package com.example.loginscreen

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.DecimalFormat
import java.text.NumberFormat
@Suppress("DEPRECATION","SetTextI18n")
class quizquestionActivity : AppCompatActivity(), View.OnClickListener {
    private var databaseReference: DatabaseReference? = null
    private var firebaseAuth: FirebaseAuth? = null
    private lateinit var questionanswer: questionanswer
    private lateinit var question: TextView
    private var option1: Button? = null
    private var option2: Button? = null
    private var option3: Button? = null
    private var option4: Button? = null
    private var scoretext:TextView?=null
    private lateinit var progressdiaglog:ProgressDialog
    private var size:Int=0
    private var mCorrectAnswers:Int=0
    private lateinit var timetext: TextView
    private var currentquestion: Int = 0
    private var incurrectquestion:Int=0
    private var time: Int = 0
    private lateinit var progressbartextview:TextView
    private var previous:Button?=null
    private var mSelectedOptionPosition: Int = 0
    private lateinit var progrssBar: ProgressBar
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var quizcode: String
    private var questionanswers: ArrayList<questionanswer>?=null
    private var submitb: Button?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quizquestion)
        question = findViewById(R.id.tv_question)
        option1 = findViewById(R.id.tv_option_one)
        option2 = findViewById(R.id.tv_option_two)
        option3 = findViewById(R.id.tv_option_three)
        option4 = findViewById(R.id.tv_option_four)
        timetext = findViewById(R.id.editTextTime)
        option1?.setOnClickListener(this)
        option2?.setOnClickListener(this)
        option3?.setOnClickListener(this)
        option4?.setOnClickListener(this)
        scoretext=findViewById(R.id.edityourscore)
        previous=findViewById(R.id.previous)
        previous?.setOnClickListener(this)
        progrssBar=findViewById(R.id.progressBar)
        quizcode = intent.getStringExtra("quizcode").toString()
        questionanswers = ArrayList()
        progressbartextview=findViewById(R.id.tv_progress_textview)
        submitb = findViewById(R.id.next)
        progressdiaglog= ProgressDialog(this@quizquestionActivity)
        progressdiaglog.setCancelable(false)
        progressdiaglog.onBackPressed()
        progressdiaglog.setMessage("Loading...")

        progressdiaglog.show()
        databaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://login-9e300-default-rtdb.firebaseio.com/")
        firebaseAuth = FirebaseAuth.getInstance()
        submitb?.setOnClickListener(this)
        try {
            databaseReference?.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    time =
                        snapshot.child(quizcode).child("time").value.toString().toInt().times(60000)

                    try {
                        for (dataSnapshot: DataSnapshot in snapshot.child(quizcode).children) {
                            val question: String = dataSnapshot.child("question").value.toString()
                            val option1: String = dataSnapshot.child("option1").value.toString()
                            val option2: String = dataSnapshot.child("option2").value.toString()
                            val option3: String = dataSnapshot.child("option3").value.toString()
                            val option4: String = dataSnapshot.child("option4").value.toString()
                            val answers: String = dataSnapshot.child("answer").value.toString()
                            questionanswer =
                                questionanswer(question, option1, option2, option3, option4, answers, false, ""
                                )
                            questionanswers?.add(questionanswer)
                            progrssBar.max = questionanswers!!.size.minus(3)
                            progressbartextview.text =
                                (currentquestion + 1).toString() + "/" + questionanswers!!.size.minus(3).toString()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@quizquestionActivity, e.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                    starttimer(time.toFloat())
                    option1?.isEnabled = true
                    option2?.isEnabled = true
                    option3?.isEnabled = true
                    option4?.isEnabled = true
                    setQuestion()
                    progressdiaglog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
                }

            })
        }catch (e:Exception){
            Toast.makeText(this,e.message.toString(),Toast.LENGTH_LONG).show()
        }
    }

    fun starttimer(time: Float) {
        countDownTimer = object : CountDownTimer(time.toLong(), 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val f: NumberFormat = DecimalFormat("00")
                val hour=millisUntilFinished/3600000 % 60
                val min = millisUntilFinished / 60000 % 60
                val sec = millisUntilFinished / 1000 % 60
                timetext.text=Html.fromHtml("<font color=${R.color.black}>Time Left : </font> <font color=#EF4208>"+f.format(hour)+":" + f.format(min) + ":" + f.format(sec)+"</font>")
            }

            override fun onFinish() {
                Toast.makeText(applicationContext, "Time Over", Toast.LENGTH_SHORT).show()
                val intent1 = Intent(applicationContext, ResultActivity::class.java)

                intent1.putExtra("Correctanswer",mCorrectAnswers)
                intent1.putExtra("InCorrectanswer",incurrectquestion)
                intent1.putExtra("No_of_equcation",size)
                intent1.putExtra("QuizCode",quizcode)
                Toast.makeText(
                    applicationContext,
                    "You have successfully completed the quiz. Your Score is : $mCorrectAnswers",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(intent1)

            }
        }.start()

    }

    override fun onClick(view: View?) {
        when (view?.id) {

            R.id.tv_option_one -> {
                option1?.let {
                    selectedOptionView(it, 1)
                }

            }

            R.id.tv_option_two -> {
                option2?.let {
                    selectedOptionView(it, 2)
                }

            }

            R.id.tv_option_three -> {
                option3?.let {
                    selectedOptionView(it, 3)
                }

            }

            R.id.tv_option_four -> {
                option4?.let {
                    selectedOptionView(it, 4)
                }

            }
            R.id.next-> {
                try {
                    submitb?.let {
                        if (mSelectedOptionPosition == 0) {

                            currentquestion++
                            size= questionanswers?.size?.minus(3)!!
                            val question=questionanswers?.get(currentquestion)
                            when {
                                (currentquestion < size && question?.attempted!=true) -> {
                                    option1?.isEnabled=true
                                    option2?.isEnabled=true
                                    option3?.isEnabled=true
                                    option4?.isEnabled=true
                                setQuestion()
                                }
                                (currentquestion < size && question?.attempted==true) -> {
                                    setQuestion()
                                    answerView(question.selectedoption.toInt(),R.drawable.wrong_option_border_bg)
                                    answerView(question.answer.toInt(),R.drawable.correct_option_border_bg)
                                }
                                else -> {
                                    submitb?.text="NEXT"
                                    progrssBar.progress=currentquestion

                                    val intent1 = Intent(applicationContext, ResultActivity::class.java)

                                    intent1.putExtra("Correctanswer",mCorrectAnswers)
                                    intent1.putExtra("InCorrectanswer",incurrectquestion)
                                    intent1.putExtra("No_of_equcation",size)
                                    intent1.putExtra("QuizCode",quizcode)
                                    Toast.makeText(
                                        this,
                                        "You have successfully completed the quiz. Your Score is : $mCorrectAnswers",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(intent1)

                                }
                            }
                        } else {
                            val question=questionanswers?.get(currentquestion)
                            if (question?.answer?.toInt() != mSelectedOptionPosition) {
                                answerView(
                                    mSelectedOptionPosition,
                                    R.drawable.wrong_option_border_bg
                                )
                                incurrectquestion++
                                question?.attempted=true
                                question?.selectedoption=mSelectedOptionPosition.toString()
                            }

                            else {
                                mCorrectAnswers++
                                question.attempted=true
                                question.selectedoption=mSelectedOptionPosition.toString()

                                try {
                                }catch (e:Exception){
                                    Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show()
                                }

                            }
                            question?.answer?.toInt()
                                ?.let { it1 ->
                                    answerView(
                                        it1,
                                        R.drawable.correct_option_border_bg
                                    )
                                    question.attempted=true
                                }

                            if ((currentquestion) == (questionanswers?.size)) {
                                submitb?.text = "FINISH"
                            } else {
                                submitb?.text = "NEXT"
                            }

                            mSelectedOptionPosition = 0
                        }
                    }


                }catch (e:Exception) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
                }
            }


            R.id.previous-> {
                    previous.let {
                        try {
                            currentquestion--
                            val question = questionanswers?.get(currentquestion)
                            setQuestion()
                            if (question?.attempted == true) {
                                answerView(
                                    question.selectedoption.toInt(),
                                    R.drawable.wrong_option_border_bg
                                )
                                answerView(
                                    question.answer.toInt(),
                                    R.drawable.correct_option_border_bg
                                )
                            }
                        }catch (e1:ArrayIndexOutOfBoundsException){
                            Toast.makeText(this,"No Questions Before",Toast.LENGTH_LONG).show()

                        } catch (e: RuntimeException) {
                            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }

    private fun selectedOptionView(tv: Button, selectedOptionNum: Int) {

        defaultOptionsView()

        mSelectedOptionPosition = selectedOptionNum

        tv.setTextColor(
            Color.parseColor("#363A43")
        )
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(
            this,
            R.drawable.selected_option_border_bg
        )
    }

    private fun defaultOptionsView() {

        val options = ArrayList<TextView>()
        option1?.let {
            options.add(0, it)
        }
        option2?.let {
            options.add(1, it)
        }
        option3?.let {
            options.add(2, it)
        }
        option4?.let {
            options.add(3, it)
        }

        for (option in options) {
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(
                this,
                R.drawable.default_option_border_bg
            )
        }
    }
    private fun answerView(answer: Int, drawableView: Int) {
        option1?.isEnabled=false
        option2?.isEnabled=false
        option3?.isEnabled=false
        option4?.isEnabled=false

        when (answer) {

            1 -> {
                option1?.background = ContextCompat.getDrawable(
                    this,
                    drawableView
                )
            }
            2 -> {
                option2?.background = ContextCompat.getDrawable(
                    this,
                    drawableView
                )
            }
            3 -> {
                option3?.background = ContextCompat.getDrawable(
                    this,
                    drawableView
                )
            }
            4 -> {
                option4?.background = ContextCompat.getDrawable(
                    this,
                    drawableView
                )
            }
        }


    }
    @SuppressLint("SetTextI18n")
    private fun setQuestion() {
        try {

            progressbartextview.text=(currentquestion+1).toString()+"/"+questionanswers!!.size.minus(3).toString()
            val question1: questionanswer =
                questionanswers!![currentquestion] // Getting the question from the list with the help of current position.
            defaultOptionsView()
            progrssBar.progress=currentquestion
            if ((currentquestion) == (size+1)) {
                submitb?.text = "FINISH"
            } else {
                submitb?.text = "NEXT"
            }
            scoretext?.text =
                Html.fromHtml("<font color=${R.color.black}>Your Score: </font><font color=#FA0505>" + mCorrectAnswers.toString() + "</font>")

            question.text =
                "Q.\t" + (currentquestion + 1).toString() + ")\t\t" + question1.question
//        ivImage?.setImageResource(question.image)
            option1?.text = "1.\t"+question1.option1
            option2?.text = "2.\t"+question1.option2
            option3?.text = "3.\t"+question1.option3
            option4?.text = "4.\t"+question1.option4

        }catch (e:Exception){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        val alertDialog=AlertDialog.Builder(this)
        alertDialog.setIcon(R.drawable.ic_baseline_error_24)
            .setTitle("Submit")
            .setMessage("Are You Sure Want to Exit")
            .setPositiveButton("Yes",DialogInterface.OnClickListener{dialog, which ->
                val intent1 = Intent(applicationContext, ResultActivity::class.java)

                intent1.putExtra("Correctanswer",mCorrectAnswers)
                intent1.putExtra("InCorrectanswer",incurrectquestion)
                intent1.putExtra("No_of_equcation",size)
                intent1.putExtra("QuizCode",quizcode)
                Toast.makeText(
                    this,
                    "You have successfully completed the quiz. Your Score is : $mCorrectAnswers",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(intent1)

            })
            .setNegativeButton("NO",DialogInterface.OnClickListener{dialog, which ->

            }).create().show()
    }
}





