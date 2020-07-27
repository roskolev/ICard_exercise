// Icons by Icons8
package com.example.icard_ex

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var linearLayoutManager        :   LinearLayoutManager
    private lateinit var contactsAdapter            :   ContactsRecyclerAdapter
    private lateinit var dbHelper                   :   DatabaseHelper

    private val PERMISSIONS_REQUEST_READ_CONTACTS   =   100

    override fun onResume() {
        setUpContactsRecyclerAdapter(dbHelper.getAllContacts())
        invalidateOptionsMenu()
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper                            =   DatabaseHelper(this)
        linearLayoutManager                 =   LinearLayoutManager(this)
        contactsRecycler.layoutManager      =   linearLayoutManager

        setUpContactsRecyclerAdapter(dbHelper.getAllContacts())
        setSupportActionBar(toolbar)

        contactsRecycler.addItemDecoration(DividerItemDecoration(
                                                                    contactsRecycler.context,
                                                                    linearLayoutManager.orientation))

        floatingActionButton.setOnClickListener {
            startActivity(Intent(this, AddContactActivity::class.java))
        }
    }

    override fun onStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_CONTACTS) !=
                PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),
                                PERMISSIONS_REQUEST_READ_CONTACTS)
        }
        super.onStart()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val searchContactItem = menu?.findItem(R.id.action_search)

        menuInflater.inflate(R.menu.action_bar_menu, menu)

        if (searchContactItem != null) {
            val searchContactsView      =   searchContactItem.actionView as SearchView

            searchContactsView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(constraint: String): Boolean {
                    return true
                }

                override fun onQueryTextChange(partialvalue: String): Boolean {
                    setUpContactsRecyclerAdapter(dbHelper.filterContacts(partialvalue))
                    return true
                }

            })
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var isOptionSelected = false
        when (item.itemId) {
            R.id.action_all -> {
                setUpContactsRecyclerAdapter(dbHelper.getAllContacts())
                isOptionSelected = true
            }
            R.id.action_male -> {
                setUpContactsRecyclerAdapter(dbHelper.getMaleContacts())
                isOptionSelected = true
            }
            R.id.action_female -> {
                setUpContactsRecyclerAdapter(dbHelper.getFemaleContacts())
                isOptionSelected = true
            }
            else -> super.onOptionsItemSelected(item)
        }
        return isOptionSelected
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[readContacts] == PackageManager.PERMISSION_GRANTED) {
                setUpContactsRecyclerAdapter(dbHelper.getAllContacts())
            } else {
                 Toast.makeText(
                     this, R.string.read_contacts_permission_needed,
                     Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setUpContactsRecyclerAdapter(contactsCursor: Cursor){
        val contactsList    =   mutableListOf<String>()
        val idsList         =   mutableListOf<Int>()
        //TODO - MOVE TO NEW CLASS
        with(contactsCursor){
            while (moveToNext()){
                val forename    =   getString(getColumnIndexOrThrow(ContactsTable.COLUMN_FORENAME))
                val surname     =   getString(getColumnIndexOrThrow(ContactsTable.COLUMN_SURNAME))
                val id          =   getInt(getColumnIndexOrThrow(BaseColumns._ID))

                contactsList.add("$forename $surname")
                idsList.add(id)
            }
        }
        val inAppNumber = contactsList.size

        //Get device contacts if we have permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
        } else {
            val cr: ContentResolver         =   this.contentResolver
            val sortOrder                   =   "${ContactsContract.Contacts.DISPLAY_NAME} ASC"
            val deviceCursor                =   cr.query(
                                                     ContactsContract.Contacts.CONTENT_URI,
                                                    null,
                                                    null,
                                                    null,
                                                    sortOrder)

            if (deviceCursor != null) {
                if (deviceCursor.count > 0) {
                    while (deviceCursor.moveToNext()) {
                        val id = deviceCursor.getString(
                            deviceCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
                        )
                        val name = deviceCursor.getString(
                            deviceCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                        contactsList.add(name)
                        idsList.add(id.toInt())
                    }
                }
            }
            deviceCursor?.close()
        }
        contactsAdapter             =   ContactsRecyclerAdapter(contactsList, idsList, inAppNumber)
        contactsRecycler.adapter    =    contactsAdapter
        //TODO ENDS HERE
    }

    companion object {
        const val   readContacts = 0
    }
}