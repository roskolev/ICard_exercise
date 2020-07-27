package com.example.icard_ex

import android.os.Bundle
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_edit.*

open class AddContactActivity : ManageContactActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        radioSex.clearCheck()
        supportActionBar?.title = "New contact"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var isOptionSelected = false

        when (item.itemId) {
            R.id.action_confirm -> {
                if(validation())
                    addContact()
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

    private fun addContact(){
        val forename            =       editTextForename.text.toString()
        val surname             =       editTextSurname.text.toString()
        val email               =       editTextEmailAddress.text.toString()
        val phone               =       editTextPhone.text.toString()
        val gender              =       findViewById<RadioButton>(radioSex.checkedRadioButtonId).text.toString()
        val country             =       countryValueField.text.toString()

        AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_action_contact)
            .setTitle(R.string.create_contact)
            .setMessage(forename + " " + surname +
                    "\n" + R.string.number + " " + editTextPhoneCode.text + phone +
                    "\n" + R.string.email + " " + email +
                    "\n" + R.string.gender + " " + gender)
            .setPositiveButton(R.string.okay){ _, _ ->
                val errorCode: Long = -1
                if(dbHelper.addContact(forename, surname, email, phone, gender, country) != errorCode) {
                    Toast.makeText(
                        this,
                        R.string.create_success,
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }else{
                    Toast.makeText(
                        this,
                        R.string.create_error,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .setNegativeButton(R.string.cancel){ _, _ ->
            }
            .show()
    }
}