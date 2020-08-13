package com.example.icard_ex

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.icard_ex.helpers.ContactsHelper
import com.example.icard_ex.helpers.DatabaseHelper
import com.example.icard_ex.models.Contact
import com.example.icard_ex.models.Country
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {
    private lateinit var dbHelper       : DatabaseHelper
    private lateinit var contactsHelper : ContactsHelper
    private lateinit var contact        :   Contact
    private var id                      =   -1
    private var idDevice                =   0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        dbHelper        = DatabaseHelper(this)
        contactsHelper  = ContactsHelper(this)
        id              =   intent.getIntExtra(getString(R.string.id), defaultID)

        if(id == defaultID)
            idDevice    =   intent.getIntExtra(getString(R.string.device_id), defaultID)

        setSupportActionBar(toolbarDetails)
        setUpDetails()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var isOptionSelected = true

        when (item.itemId) {
            R.id.actionDelete   ->  {
                AlertDialog.Builder(this).setMessage(getString(R.string.delete_name, contact.fullname))
                    .setPositiveButton(R.string.yes) { _, _ ->
                        dbHelper.deleteContact(id)
                        finish()
                    }
                    .setNegativeButton(R.string.no){_, _ ->
                    }.show()
            }
            R.id.actionEdit     ->  {
                if (id == defaultID)
                    this.startActivity(
                        Intent(this, AddContactActivity::class.java)
                            .putExtra(getString(R.string.device_id), idDevice)
                    )
                else
                    this.startActivity(
                        Intent(this, EditContactActivity::class.java)
                            .putExtra(getString(R.string.id), id)
                    )
            }
            else                ->  {
                super.onOptionsItemSelected(item)
                isOptionSelected = false
            }
        }
        return isOptionSelected
    }

    override fun onResume() {
        super.onResume()
        setUpDetails()
    }

    private fun setUpDetails(){
        val country :   Country
        if(id != defaultID) {
            contact             =   dbHelper.getContact(id)
            country             =   contact.country
            numberValue.text    =   getString(R.string.whole_number, country.code, contact.phone)
            countryValue.text   =   country.name
        }
        else{
            //Set Up device Contact
            contact             =   contactsHelper.getDeviceContact(idDevice)
            numberValue.text    =   contact.phone
        }

        emailValue.text         =   contact.email
        genderValue.text        =   contact.gender
        supportActionBar?.title =   contact.fullname

        countryText.text        =   getString(R.string.country)
        countryText.textSize    =   16F
        countryValue.textSize   =   16F

        numberText.text         =   getString(R.string.number)
        numberText.textSize     =   16F
        numberValue.textSize    =   16F

        emailText.text          =   getString(R.string.email)
        emailText.textSize      =   16F
        emailValue.textSize     =   16F

        genderText.text         =   getString(R.string.gender)
        genderText.textSize     =   16F
        genderValue.textSize    =   16F

    }

    companion object {
        const val   defaultID = -1
    }
}