package com.example.icard_ex

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.icard_ex.models.Contact
import com.example.icard_ex.models.Country
import com.example.icard_ex.recyclers.CountriesRecyclerAdapter
import kotlinx.android.synthetic.main.activity_base_contact.*
import kotlinx.android.synthetic.main.countries_dialog.*

abstract class BaseContactActivity : AppCompatActivity() {
    protected lateinit var dbHelper :   DatabaseHelper
    private lateinit var countries  :   MutableList<Country>
    lateinit var dialog             :   Dialog

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
        menuInflater.inflate(R.menu.base_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    protected fun validation(): Boolean {
        val emailPattern    =   "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val phonePattern    =   "^[0-9]*$"
        var validated       =   false

        when{
            !dbHelper.countryInDB(searchCountryDialog.text.toString())                                          ->
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
        dialog                          =   Dialog(this)

        dialog.setContentView(R.layout.countries_dialog)

        countries                           =   dbHelper.getAllCountries()
        dialog.countryList.layoutManager    =   LinearLayoutManager(this)
        dialog.countryList.adapter          =   CountriesRecyclerAdapter(this, countries, dialog)

        dialog.setOnDismissListener {
            dialog.searchCountry.setText("")
        }

        dialog.searchCountry.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(partialValue: CharSequence, start: Int, before: Int, count: Int) {
                (dialog.countryList.adapter as CountriesRecyclerAdapter).updateList(dbHelper.getFilteredCountries(partialValue))
            }
        })

        searchCountryDialog.setOnTouchListener { _, _ ->
            dialog.show()
            false
        }

    }

    private fun findCountryByName(name: String): Country {
        lateinit var countryFound : Country

        countries.forEach { country ->
            if (country.name == name)
                countryFound = country
        }

        return countryFound
    }

    protected fun buildConfirmDialog(errorCode: Long, successString: Int, errorString: Int, titleString: Int){
        val contact = Contact(
            editTextForename.text.toString(),
            editTextSurname.text.toString(),
            editTextEmailAddress.text.toString(),
            editTextPhone.text.toString(),
            findViewById<RadioButton>(radioSex.checkedRadioButtonId).text.toString(),
            findCountryByName(searchCountryDialog.text.toString()).id
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