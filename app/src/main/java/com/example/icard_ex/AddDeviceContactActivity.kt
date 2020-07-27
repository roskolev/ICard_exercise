package com.example.icard_ex

import android.content.ContentResolver
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_edit.*

class AddDeviceContactActivity : AddContactActivity() {
    private var contactID = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title     =   "New contact"
        contactID                   =   intent.getIntExtra("IDDevice", 0)

        radioSex.clearCheck()
        setUpContactInfo()
    }

    //TODO - MOVE queries to new class
    private fun setUpContactInfo() {
        var forename                =   ""
        var email                   =   ""
        var number                  =   ""
        val cr: ContentResolver     =   this.contentResolver

        var deviceCursor            =   cr.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
            arrayOf(contactID.toString()), null
        )
        if (deviceCursor != null) {
            while (deviceCursor.moveToNext())
                number = deviceCursor.getString(
                    deviceCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                )
        }

        deviceCursor = cr.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?"
            , arrayOf(contactID.toString()), null
        )
        if (deviceCursor != null) {
            while (deviceCursor.moveToNext())
                email = deviceCursor.getString(
                    deviceCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DATA)
                )
        }

        deviceCursor = cr.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, ContactsContract.Contacts._ID + " = ?",
            arrayOf(contactID.toString()), null
        )
        if (deviceCursor != null) {
            while ((deviceCursor.moveToNext()))
                forename = deviceCursor.getString(
                    deviceCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)
                )
        }

        editTextForename.setText(forename)
        editTextEmailAddress.setText(email)
        editTextPhone.setText(number)
        deviceCursor?.close()
    }
}