package com.example.loginscreen

class UserData {
    lateinit var email:String
    lateinit var name:String
    lateinit var password:String
    constructor(){

    }
    constructor(name:String,email:String,password:String){
        this.name=name
        this.password=password
        this.email=email
    }
}