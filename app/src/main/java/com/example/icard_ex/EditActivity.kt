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

class EditActivity : AppCompatActivity() {
    private var id = 0
    private var idDevice = 0
    private lateinit var dbHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        dbHelper = DatabaseHelper(this)
        id = intent.getIntExtra("ID", -1)
        setSupportActionBar(toolbarEdit)
        //If id is -1 that means user clicked the new contact button,
        // if id is -2 that means user clicked edit on a device contact meaning we add it to the DB,
        // otherwise we open the edit view of the contact with that ID
        when (id) {
            -1 -> {
                supportActionBar?.title = "New contact"
                radioSex.clearCheck()
            }
            -2 -> {
                supportActionBar?.title = "New contact"
                radioSex.clearCheck()
                idDevice = intent.getIntExtra("IDDevice", 0)
                setUpContactInfo()
            }
            else -> {
                setUpEditView()
            }
        }
        setUpCountryField()
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_quit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.edit_menu, menu) //this line sets up the 'X' button in the activity
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_confirm -> {
            if(validation()){
                when (id) {
                    -1 -> {
                        setUpAction(false)
                    }
                    -2 -> {
                        setUpAction(false)
                    }
                    else -> {
                        setUpAction(true)
                    }
                }
            }
            true
        }
        //Here is the action of the 'X' button
        android.R.id.home -> {
                finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun validation(): Boolean{
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val phonePattern = "^[0-9]*$"
        if(!dbHelper.countryInDB(countryValueField.text.toString())){
            Toast.makeText(this,"Please choose a country from the list", Toast.LENGTH_LONG).show()
            return false
        }else if(editTextForename.text.isEmpty() || editTextSurname.text.isEmpty()) {
            Toast.makeText(this, "Please fill the name fields", Toast.LENGTH_LONG).show()
            return false
        }else if(radioSex.checkedRadioButtonId == -1){
            Toast.makeText(this, "Please choose gender", Toast.LENGTH_LONG).show()
            return false
        }else if(editTextEmailAddress.text.isEmpty() || !editTextEmailAddress.text.matches(emailPattern.toRegex())){
            Toast.makeText(this, "Please enter valid email address", Toast.LENGTH_LONG).show()
            return false
        }else if(editTextPhone.text.isEmpty() || !editTextPhone.text.matches(phonePattern.toRegex())){
            Toast.makeText(this, "Please enter valid phonenumber", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun setUpCountryField(){
        editTextPhoneCode.keyListener = null
        val countries = dbHelper.getAllCountries()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, countries)
        countryValueField.setAdapter(adapter)
        countryValueField.addTextChangedListener(object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable) {
                val code = dbHelper.getCountryCode(s.toString())
                if(code != 0){
                    editTextPhoneCode.setText("+$code")
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })
        // Fix suggestions drop down list to drop on touch with no characters entered
        countryValueField.setOnTouchListener { _, _ ->
            if (countryValueField.text.toString() != "") {
                adapter.filter.filter(null)
            }
            countryValueField.showDropDown()
            false
        }
    }
    @SuppressLint("SetTextI18n")
    private fun setUpEditView(){
        supportActionBar?.title = "Edit contact"
        val cursor = dbHelper.getContact(id)
        cursor.moveToNext()
        val forename = cursor.getString(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_FORENAME))
        val surname = cursor.getString(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_SURNAME))
        val country = cursor.getString(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_COUNTRY))
        val gender = cursor.getString(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_GENDER))
        val email = cursor.getString(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_EMAIL))
        val number = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_NUMBER))

        countryValueField.setText(country)
        editTextForename.setText(forename)
        editTextSurname.setText(surname)
        editTextEmailAddress.setText(email)
        editTextPhone.setText(number.toString())
        val code = dbHelper.getCountryCode(country)
        editTextPhoneCode.setText("+$code")
        if(gender == "Male"){
            radioMale.isChecked = true
        }else{
            radioFemale.isChecked = true
        }
    }
    private fun setUpContactInfo(){
        var forename = ""
        var email = ""
        var number = ""
        val cr: ContentResolver = this.contentResolver
        var deviceCursor = cr.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
            arrayOf(idDevice.toString()), null
        )
        if (deviceCursor != null) {
            while(deviceCursor.moveToNext())
                number = deviceCursor.getString(
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
        editTextForename.setText(forename)
        editTextEmailAddress.setText(email)
        editTextPhone.setText(number)
        deviceCursor?.close()
    }
    //This function sets up the confirm button to do the action the user is currently doing,
    //  either edit or add a contact
    private fun setUpAction(isEdit: Boolean){
        val forename = editTextForename.text.toString()
        val surname = editTextSurname.text.toString()
        val email = editTextEmailAddress.text.toString()
        val phone = editTextPhone.text.toString()
        val gender = findViewById<RadioButton>(radioSex.checkedRadioButtonId).text.toString()
        val country = countryValueField.text.toString()
        if(!isEdit){
            AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_action_contact)
                .setTitle("Create new contact?")
                .setMessage("$forename $surname" +
                        "\nPhone Number: ${editTextPhoneCode.text}$phone" +
                        "\nEmail: $email" +
                        "\nGender: $gender")
                .setPositiveButton("Ok"){ _, _ ->
                    val errorCode: Long = -1
                    if(dbHelper.addContact(forename, surname, email, phone, gender, country) != errorCode) {
                        Toast.makeText(
                            this,
                            "Contact successfully created",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }else{
                        Toast.makeText(
                            this,
                            "There was an error with creating the contact",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .setNegativeButton("Cancel"){ _, _ ->

                }
                .show()

        }else{
            AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_action_contact)
                .setTitle("Edit contact?")
                .setMessage("$forename $surname" +
                        "\nPhone Number: ${editTextPhoneCode.text}$phone" +
                        "\nEmail: $email" +
                        "\nGender: $gender")
                .setPositiveButton("Ok"){ _, _ ->
                    if(dbHelper.editContact(id, forename, surname, email, phone, gender, country) == 1) {
                        Toast.makeText(
                            this,
                            "Contact successfully edited",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }else{
                        Toast.makeText(
                            this,
                            "There was an error with editing the contact",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .setNegativeButton("Cancel"){ _, _ ->

                }
                .show()
        }
    }
}
