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
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerAdapter
    private lateinit var dbHelper: DatabaseHelper
    private val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbHelper = DatabaseHelper(this)
        linearLayoutManager = LinearLayoutManager(this)
        recycler.layoutManager = linearLayoutManager
        val cursor = dbHelper.getAllContacts()
        setUpRecyclerAdapter(cursor)
        recycler.addItemDecoration(
            DividerItemDecoration(recycler.context, linearLayoutManager.orientation))
        setSupportActionBar(toolbar)
        fab.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS)
        }
        super.onStart()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_bar_menu, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        if (searchItem != null) {
            val searchView = searchItem.actionView as SearchView
            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(constraint: String): Boolean {
                    return true
                }

                override fun onQueryTextChange(partialvalue: String): Boolean {
                    val cursor = dbHelper.filterContacts(partialvalue)
                    setUpRecyclerAdapter(cursor)
                    return true
                }

            })
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_all -> {
            val cursor = dbHelper.getAllContacts()
            setUpRecyclerAdapter(cursor)
            true
        }
        R.id.action_male -> {
            val cursor = dbHelper.getMaleContacts()
            setUpRecyclerAdapter(cursor)
            true
        }
        R.id.action_female -> {
            val cursor = dbHelper.getFemaleContacts()
            setUpRecyclerAdapter(cursor)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        val cursor = dbHelper.getAllContacts()
        setUpRecyclerAdapter(cursor)
        invalidateOptionsMenu()
        super.onResume()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val cursor = dbHelper.getAllContacts()
                setUpRecyclerAdapter(cursor)
            } else {
                 Toast.makeText(
                     this, "Permission must be granted in order to display device contacts information",
                     Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun setUpRecyclerAdapter(cursor: Cursor){
        val contactsList = mutableListOf<String>()
        val idsList = mutableListOf<Int>()
        with(cursor){
            while (moveToNext()){
                val forename = getString(getColumnIndexOrThrow(ContactsTable.COLUMN_FORENAME))
                val surname = getString(getColumnIndexOrThrow(ContactsTable.COLUMN_SURNAME))
                val id = getInt(getColumnIndexOrThrow(BaseColumns._ID))
                contactsList.add("$forename $surname")
                idsList.add(id)
            }
        }
        val inAppNumber = contactsList.size
        //Get device contacts if we have permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
        } else {
            val cr: ContentResolver = this.contentResolver
            val sortOrder = "${ContactsContract.Contacts.DISPLAY_NAME} ASC"
            val deviceCursor = cr.query(
                ContactsContract.Contacts.CONTENT_URI,
                null, null, null, sortOrder
            )
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
        adapter = RecyclerAdapter(contactsList, idsList, inAppNumber)
        recycler.adapter = adapter
    }
}