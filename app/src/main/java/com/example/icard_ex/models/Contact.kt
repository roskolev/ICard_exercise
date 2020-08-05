package com.example.icard_ex.models

class Contact() {
    lateinit var country     : Country

    var id          =   0
    var fullname    =   ""
    var forename    =   ""
    var surname     =   ""
    var countryID   =   0
    var gender      =   ""
    var email       =   ""
    var phone       =   ""

    constructor( forename: String, surname: String, email: String, phone: String, gender: String, country: Int) : this(){
        this.forename   =   forename
        this.surname    =   surname
        this.email      =   email
        this.phone      =   phone
        this.gender     =   gender
        this.fullname   =   "$forename $surname"
        this.countryID  =   country
    }

    constructor( fullname: String, id: Int) : this(){
        this.fullname   = fullname
        this.id         = id
    }

    constructor( forename: String, surname: String, email: String, phone: String, gender: String, country: Country) : this(){
        this.forename   =   forename
        this.surname    =   surname
        this.email      =   email
        this.phone      =   phone
        this.gender     =   gender
        this.country    =   country
        this.fullname   =   "$forename $surname"
    }

}