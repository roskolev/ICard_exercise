package com.example.icard_ex

import android.os.Bundle
import android.view.MenuItem
import com.example.icard_ex.models.Contact
import kotlinx.android.synthetic.main.activity_base_contact.*

class EditContactActivity : BaseContactActivity() {
    private var contactID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactID = intent.getIntExtra(getString(R.string.id), 0)
        setUpEditView()
    }

    override fun actionOnContact(contact: Contact): Any {
        return dbHelper.editContact(contact, contactID)
    }


    private fun setUpEditView() {
        supportActionBar?.title =   getString(R.string.title_edit)
        val contact             =   dbHelper.getContact(contactID)
        val country             =   contact.country

        searchCountryDialog.setText(country.name)
        editTextForename.setText(contact.forename)
        editTextSurname.setText(contact.surname)
        editTextEmailAddress.setText(contact.email)
        editTextPhone.setText(contact.phone)

        editTextPhoneCode.setText(getString(R.string.country_code, country.codeStr))

        if (contact.gender == getString(R.string.radio_male))
            radioMale.isChecked     =   true
        else
            radioFemale.isChecked   =   true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var isOptionSelected = true

        when (item.itemId) {
            R.id.actionConfirm  ->  if (validation())
                                        buildConfirmDialog(
                                                            0,
                                                            R.string.edit_success,
                                                            R.string.edit_error,
                                                            R.string.edit_contact
                                        )
            android.R.id.home   ->  finish()
            else                ->  {
                isOptionSelected = false
                super.onOptionsItemSelected(item)
            }
        }
        return isOptionSelected
    }
}