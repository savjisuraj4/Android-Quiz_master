package com.example.loginscreen

class attemptedclass {
    lateinit var name:String
    lateinit var email:String
    var attempted:Boolean=false
    lateinit var right:String
    lateinit var wrong:String
    lateinit var skip:String
    lateinit var total:String
    constructor(){

    }
    constructor(email:String,attempted:Boolean,right:String,wrong:String,skip:String,total:String){
        this.attempted=attempted
        this.email=email
        this.right=right
        this.wrong=wrong
        this.skip=skip
        this.total=total
    }
}