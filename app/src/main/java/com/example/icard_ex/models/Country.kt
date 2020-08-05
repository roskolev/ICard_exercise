package com.example.icard_ex.models

class Country() {
    var name    =   ""
    var code    =   0
    var codeStr =   ""
    var id      =   0
    constructor(name : String, countryCode: Int, id: Int) : this(){
        this.name       =   name
        this.code       =   countryCode
        this.id         =   id
        this.codeStr    =   countryCode.toString()
    }
}