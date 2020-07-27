package com.example.icard_ex

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit.*

abstract class ManageContactActivity : AppCompatActivity() {
    protected lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        dbHelper = DatabaseHelper(this)

        setSupportActionBar(toolbarEdit)
        setUpCountryField()
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_quit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun validation(): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val phonePattern = "^[0-9]*$"
        var validated = false

        if (!dbHelper.countryInDB(countryValueField.text.toString())) {
            Toast.makeText(this, R.string.country_wrong, Toast.LENGTH_LONG).show()
        } else if (editTextForename.text.isEmpty() || editTextSurname.text.isEmpty()) {
            Toast.makeText(this, R.string.name_wrong, Toast.LENGTH_LONG).show()
        } else if (radioSex.checkedRadioButtonId == -1) {
            Toast.makeText(this, R.string.gender_wrong, Toast.LENGTH_LONG).show()
        } else if (editTextEmailAddress.text.isEmpty() || !editTextEmailAddress.text.matches(
                emailPattern.toRegex()
            )
        ) {
            Toast.makeText(this, R.string.email_wrong, Toast.LENGTH_LONG).show()
        } else if (editTextPhone.text.isEmpty() || !editTextPhone.text.matches(phonePattern.toRegex())) {
            Toast.makeText(this, R.string.number_wrong, Toast.LENGTH_LONG).show()
        } else {
            validated = true
        }

        return validated
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpCountryField() {
        editTextPhoneCode.keyListener           =           null
        val countries                           =           dbHelper.getAllCountries()
        val countriesAdapter                    =           ArrayAdapter(this, android.R.layout.simple_list_item_1, countries)

        countryValueField.setAdapter(countriesAdapter)

        countryValueField.addTextChangedListener(object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable) {
                val code = dbHelper.getCountryCode(s.toString())
                if (code != 0) {
                    editTextPhoneCode.setText("+$code")
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {}
        })

        // Fix suggestions drop down list to drop on touch with no characters entered
        countryValueField.setOnTouchListener { _, _ ->
            if (countryValueField.text.toString() != "") {
                countriesAdapter.filter.filter(null)
            }
            countryValueField.showDropDown()
            false
        }
    }
}