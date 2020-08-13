package com.example.icard_ex

import android.os.Bundle
import android.view.MenuItem
import com.example.icard_ex.helpers.ContactsHelper
import com.example.icard_ex.models.Contact
import kotlinx.android.synthetic.main.activity_base_contact.*

class AddContactActivity : BaseContactActivity() {
    private lateinit var contactsHelper : ContactsHelper
    private var contactID               =   0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        radioSex.clearCheck()
        contactsHelper              =
            ContactsHelper(this)
        supportActionBar?.title     =   getString(R.string.new_contact)
        contactID                   =   intent.getIntExtra(getString(R.string.device_id), -1)

        if(contactID != defaultID)
            setUpContactInfo()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var isOptionSelected = true

        when (item.itemId) {
            R.id.actionConfirm  ->  if (validation())
                                        buildConfirmDialog(
                                                            -1,
                                                            R.string.create_success,
                                                            R.string.create_error,
                                                            R.string.create_contact
                                                            )
            android.R.id.home   ->  finish()
            else                ->  {
                isOptionSelected = false
                super.onOptionsItemSelected(item)
            }
        }

        return isOptionSelected
    }

    override fun actionOnContact(contact: Contact): Any {
        return dbHelper.addContact(contact)
    }

    private fun setUpContactInfo() {
        val contact = contactsHelper.getDeviceContact(contactID)

        editTextForename.setText(contact.forename)
        editTextPhone.setText(contact.phone)
        if(contact.email != getString(R.string.NA))
            editTextEmailAddress.setText(contact.email)
    }

    companion object {
        const val   defaultID = -1
    }
}