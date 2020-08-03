package com.example.icard_ex

class Contact() {
    var id          =   0
    var fullname    =   ""
    var forename    =   ""
    var surname     =   ""
    var country     =   "N/A"
    var gender      =   "N/A"
    var email       =   "N/A"
    var phone       =   "N/A"

    constructor( forename: String, surname: String, email: String, phone: String, gender: String, country: String) : this(){
        this.forename   =   forename
        this.surname    =   surname
        this.email      =   email
        this.phone      =   phone
        this.gender     =   gender
        this.country    =   country
        this.fullname   =   "$forename $surname"
    }

    constructor( fullname: String, id: Int) : this(){
        this.fullname = fullname
        this.id = id
    }

}