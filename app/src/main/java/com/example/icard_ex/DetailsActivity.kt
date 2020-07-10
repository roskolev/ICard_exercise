package com.example.icard_ex

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private var id = -1
    private var idDevice = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        dbHelper = DatabaseHelper(this)
        //If a device contact was clicked id is -1
        id = intent.getIntExtra("ID",-1)
        if(id == -1){
            idDevice = intent.getIntExtra("IDDevice", 0)
        }
        setSupportActionBar(toolbarDetails)
        setUpDetails()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.details_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_delete -> {
            dbHelper.deleteContact(id)
            finish()
            true
        }
        R.id.action_edit -> {
            val intent = Intent(this, EditActivity::class.java)
            if(id == -1) {
                intent.putExtra("ID", -2)
                intent.putExtra("IDDevice", idDevice)
            }
            else
                intent.putExtra("ID", id)
            this.startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        setUpDetails()
        super.onResume()
    }
    @SuppressLint("SetTextI18n")
    private fun setUpDetails(){
        var forename = ""
        var surname = ""
        var country = "N/A"
        var gender = "N/A"
        var email = "N/A"
        var numberOnDevice = "N/A"
        if(id != -1) {
            //Set up ontact
            val cursor = dbHelper.getContact(id)
            cursor.moveToNext()
            forename =
                cursor.getString(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_FORENAME))
            surname =
                cursor.getString(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_SURNAME))
            country =
                cursor.getString(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_COUNTRY))
            gender = cursor.getString(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_GENDER))
            email = cursor.getString(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_EMAIL))
            val number = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_NUMBER))
            val countryCode = dbHelper.getCountryCode(country)
            numberValue.text = "+$countryCode$number"
        }else{
            //Set Up device Contact
            val cr: ContentResolver = this.contentResolver
            var deviceCursor = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                arrayOf(idDevice.toString()), null
            )
            if (deviceCursor != null) {
                while(deviceCursor.moveToNext())
                    numberOnDevice = deviceCursor.getString(
                        deviceCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
            }
            deviceCursor = cr.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null, ContactsContract.CommonDataKinds.Email.CONTACT_ID +" = ?"
                , arrayOf(idDevice.toString()), null
            )
            if (deviceCursor != null) {
                while(deviceCursor.moveToNext())
                    email = deviceCursor.getString(
                        deviceCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DATA))
            }
            deviceCursor = cr.query(
                ContactsContract.Contacts.CONTENT_URI,
                null, ContactsContract.Contacts._ID +" = ?",
                arrayOf(idDevice.toString()), null
            )
            if (deviceCursor != null) {
                while((deviceCursor.moveToNext()))
                    forename = deviceCursor.getString(
                        deviceCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
            }
            numberValue.text = numberOnDevice
            deviceCursor?.close()
        }

        countryText.text = getString(R.string.country)
        countryValue.text = country
        countryText.textSize = 16F
        countryValue.textSize = 16F

        numberText.text = getString(R.string.number)
        numberText.textSize = 16F
        numberValue.textSize = 16F

        emailText.text = getString(R.string.email)
        emailValue.text = email
        emailText.textSize = 16F
        emailValue.textSize = 16F

        genderText.text = getString(R.string.gender)
        genderValue.text = gender
        genderText.textSize = 16F
        genderValue.textSize = 16F

        supportActionBar?.title = "$forename $surname"
    }
}