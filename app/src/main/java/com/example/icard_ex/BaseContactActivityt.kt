package com.example.icard_ex

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_base_contact.*

abstract class BaseContactActivity : AppCompatActivity() {
    protected lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_contact)

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

    protected fun validation(): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val phonePattern = "^[0-9]*$"
        var validated = false

        when{
            !dbHelper.countryInDB(countryValueField.text.toString())                                            ->
                Toast.makeText(this, R.string.country_wrong, Toast.LENGTH_LONG).show()

            editTextForename.text.isEmpty() || editTextSurname.text.isEmpty()                                   ->
                Toast.makeText(this, R.string.name_wrong, Toast.LENGTH_LONG).show()

            radioSex.checkedRadioButtonId == -1                                                                 ->
                Toast.makeText(this, R.string.gender_wrong, Toast.LENGTH_LONG).show()

            editTextEmailAddress.text.isEmpty() || !editTextEmailAddress.text.matches(emailPattern.toRegex())   ->
                Toast.makeText(this, R.string.email_wrong, Toast.LENGTH_LONG).show()

            editTextPhone.text.isEmpty() || !editTextPhone.text.matches(phonePattern.toRegex())                 ->
                Toast.makeText(this, R.string.number_wrong, Toast.LENGTH_LONG).show()

            else -> validated = true
        }

        return validated
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpCountryField() {
        editTextPhoneCode.keyListener   =   null
        val countries                   =   dbHelper.getAllCountries()
        val countriesAdapter            =   ArrayAdapter(this, android.R.layout.simple_list_item_1, countries)

        countryValueField.setAdapter(countriesAdapter)

        countryValueField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val code = dbHelper.getCountryCode(s.toString())
                if (code != 0) {
                    editTextPhoneCode.setText(getString(R.string.country_code, code))
                }
            }

            override fun beforeTextChanged( s: CharSequence, start: Int, count: Int, after: Int )
            {}

            override fun onTextChanged( s: CharSequence, start: Int, before: Int, count: Int )
            {}
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

    protected fun buildConfirmDialog(errorCode: Long, successString: Int, errorString: Int, titleString: Int){
        val contact = Contact(
                                editTextForename.text.toString(),
                                editTextSurname.text.toString(),
                                editTextEmailAddress.text.toString(),
                                editTextPhone.text.toString(),
                                findViewById<RadioButton>(radioSex.checkedRadioButtonId).text.toString(),
                                countryValueField.text.toString()
        )
        AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_action_contact)
            .setTitle(titleString)
            .setMessage(contact.forename + " " + contact.surname +
                    "\n" + getString(R.string.number) + " " + editTextPhoneCode.text + contact.phone +
                    "\n" + getString(R.string.email) + " " + contact.email +
                    "\n" + getString(R.string.gender) + " " + contact.gender)
            .setPositiveButton(R.string.okay)
            { _, _ ->
                if(actionOnContact(contact) != errorCode) {
                    Toast.makeText(this, successString, Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    Toast.makeText(
                        this,
                        errorString,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .setNegativeButton(R.string.cancel)
            { _, _ ->}.show()
    }

    abstract fun actionOnContact(contact: Contact): Any
}