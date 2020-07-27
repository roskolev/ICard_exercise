package com.example.icard_ex

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_edit.*

class EditContactActivity : ManageContactActivity() {
    private var contactID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactID = intent.getIntExtra("ID", 0)
        setUpEditView()
    }

    @SuppressLint("SetTextI18n")
    private fun setUpEditView() {
        supportActionBar?.title     =    "Edit contact"
        val cursor                  =    dbHelper.getContact(contactID)

        cursor.moveToNext()

        val forename                =    cursor.getString(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_FORENAME))
        val surname                 =    cursor.getString(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_SURNAME))
        val country                 =    cursor.getString(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_COUNTRY))
        val gender                  =    cursor.getString(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_GENDER))
        val email                   =    cursor.getString(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_EMAIL))
        val number                  =    cursor.getInt(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_NUMBER))

        countryValueField.setText(country)
        editTextForename.setText(forename)
        editTextSurname.setText(surname)
        editTextEmailAddress.setText(email)
        editTextPhone.setText(number.toString())

        val code = dbHelper.getCountryCode(country)

        editTextPhoneCode.setText("+$code")

        if (gender == "Male") {
            radioMale.isChecked = true
        } else {
            radioFemale.isChecked = true
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var isOptionSelected = false

        when (item.itemId) {
            R.id.action_confirm -> {
                if (validation())
                    editContact()
                isOptionSelected = true
            }

            android.R.id.home -> {
                finish()
                isOptionSelected = true
            }
            else -> super.onOptionsItemSelected(item)
        }

        return isOptionSelected
    }
    private fun editContact(){
        val forename            =       editTextForename.text.toString()
        val surname             =       editTextSurname.text.toString()
        val email               =       editTextEmailAddress.text.toString()
        val phone               =       editTextPhone.text.toString()
        val gender              =       findViewById<RadioButton>(radioSex.checkedRadioButtonId).text.toString()
        val country             =       countryValueField.text.toString()

        AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_action_contact)
            .setTitle(R.string.edit_contact)
            .setMessage(forename + " " + surname +
                        "\n" + R.string.number + " " + editTextPhoneCode.text + phone +
                        "\n" + R.string.email + " " + email +
                        "\n" + R.string.gender + " " + gender)
            .setPositiveButton(R.string.okay){ _, _ ->
                if(dbHelper.editContact(contactID, forename, surname, email, phone, gender, country) == 1) {
                    Toast.makeText(
                        this,
                        R.string.edit_success,
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }else{
                    Toast.makeText(
                        this,
                        R.string.edit_error,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .setNegativeButton(R.string.cancel){ _, _ ->
            }
            .show()
    }
}