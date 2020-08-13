// Icons by Icons8
package com.example.icard_ex

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.icard_ex.models.Contact
import com.example.icard_ex.presenters.MainPresenter
import com.example.icard_ex.presenters.MainView
import com.example.icard_ex.recyclers.ContactsRecyclerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
    MainView {
    private lateinit var linearLayoutManager        :   LinearLayoutManager
    private lateinit var presenter                  : MainPresenter
    private val permissionRequestReadContacts       =   100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        linearLayoutManager                 =   LinearLayoutManager(this)
        contactsRecycler.layoutManager      =   linearLayoutManager
        presenter                           =
            MainPresenter(this)
        presenter.attachView(this)

        setSupportActionBar(toolbar)

        contactsRecycler.addItemDecoration(DividerItemDecoration(
                                                                    contactsRecycler.context,
                                                                    linearLayoutManager.orientation
                                            )
        )

        floatingActionButton.setOnClickListener {
            startActivity(Intent(this, AddContactActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.subscribe()
        invalidateOptionsMenu()
    }

    override fun onPause() {
        super.onPause()
        presenter.unSubscribe()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onStart() {
        super.onStart()
        if  (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M                                                  &&
                checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
            )
                requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),permissionRequestReadContacts)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)

        val searchContactItem = menu?.findItem(R.id.actionSearch)

        if (searchContactItem != null) {
            val searchContactsView      =   searchContactItem.actionView as SearchView

            searchContactsView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(constraint: String): Boolean {
                    return true
                }

                override fun onQueryTextChange(partialvalue: String): Boolean {
                    presenter.getContacts(partialvalue)
                    return true
                }

            })
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var isOptionSelected = true
        when (item.itemId) {
            R.id.actionAll      ->
                presenter.getContacts(allContacts)
            R.id.actionMale     ->
                presenter.getContacts(getString(R.string.radio_male))
            R.id.actionFemale   ->
                presenter.getContacts(getString(R.string.radio_female))
            else                -> {
                isOptionSelected = false
                super.onOptionsItemSelected(item)
            }
        }
        return isOptionSelected
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == permissionRequestReadContacts) {
            if (grantResults[readContacts] == PackageManager.PERMISSION_GRANTED)
                presenter.getContacts(allContacts)
            else {
                 Toast.makeText(
                     this,
                     R.string.read_contacts_permission_needed,
                     Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun setUpContacts(contacts: MutableList<Contact>) {
        contactsRecycler.adapter = ContactsRecyclerAdapter(contacts)
    }

    companion object {
        const val   readContacts    =   0
        const val   allContacts     =   ""
    }
}
