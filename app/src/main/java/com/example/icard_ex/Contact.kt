package com.example.icard_ex

class Contact() {
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
    }

}