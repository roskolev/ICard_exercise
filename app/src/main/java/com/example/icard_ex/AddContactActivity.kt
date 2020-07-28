package com.example.icard_ex

import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_base_contact.*

open class AddContactActivity : BaseContactActivity() {
    private lateinit var contactsHelper :   ContactsHelper
    private var contactID               =   0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        radioSex.clearCheck()
        supportActionBar?.title     = getString(R.string.new_contact)
        contactID                   =   intent.getIntExtra("IDDevice", -1)

        if(contactID != defaultID)
            setUpContactInfo()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var isOptionSelected = true

        when (item.itemId) {
            R.id.action_confirm ->
                if(validation())
                    buildConfirmDialog(-1, R.string.create_success, R.string.create_error, R.string.create_contact)
            android.R.id.home ->
                finish()
            else -> {
                super.onOptionsItemSelected(item)
                isOptionSelected = false
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
        editTextEmailAddress.setText(contact.email)
        editTextPhone.setText(contact.phone)
    }
    companion object {
        const val   defaultID = -1
    }
}