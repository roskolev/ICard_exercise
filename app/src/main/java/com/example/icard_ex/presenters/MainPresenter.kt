package com.example.icard_ex.presenters

import android.content.Context
import com.example.icard_ex.helpers.ContactsHelper
import com.example.icard_ex.MainActivity.Companion.allContacts
import com.example.icard_ex.R
import com.example.icard_ex.models.Contact

class MainPresenter(context: Context) : BasePresenter<MainView>(context) {

    private lateinit var contactsHelper: ContactsHelper

    override fun onViewAttached(view: MainView) {
        super.onViewAttached(view)
        contactsHelper = ContactsHelper(context)
    }

    override fun subscribe() {
        super.subscribe()
        getContacts(allContacts)
    }

    fun getContacts(filter : String){
        when (filter) {
            allContacts -> view?.setUpContacts(contactsHelper.initAllContacts())
            context.getString(R.string.radio_male)      -> view?.setUpContacts(contactsHelper.getContactsByGender(true))
            context.getString(R.string.radio_female)    -> view?.setUpContacts(contactsHelper.getContactsByGender(false))
            else        -> view?.setUpContacts(contactsHelper.getFilteredContacts(filter))
        }
    }
}

interface MainView{
    fun setUpContacts(contacts: MutableList<Contact>)
}