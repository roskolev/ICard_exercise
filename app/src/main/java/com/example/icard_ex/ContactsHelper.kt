package com.example.icard_ex

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.ContactsContract
import com.example.icard_ex.models.Contact
import com.example.icard_ex.recyclers.ContactsRecyclerAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@Suppress("ControlFlowWithEmptyBody")
class ContactsHelper(context: Context) {
    private val dbHelper    =   DatabaseHelper(context)
    private val ctx         =   context

    fun initAllContacts(): ContactsRecyclerAdapter {
        var contactsList        =   mutableListOf<Contact>()
        val contactsListInPhone =   mutableListOf<Contact>()
        var inAppNumber         =   0

        val queryAppDB          =   GlobalScope.launch {
            contactsList    =    dbHelper.getAllContacts()
            inAppNumber     =    contactsList.size
        }

        runBlocking {
            if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ctx.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
            )
            else {
                //Get device contacts if we have read permission
                val cr: ContentResolver     =   ctx.contentResolver
                val sortOrder               =   "${ContactsContract.Contacts.DISPLAY_NAME} ASC"
                val deviceContactsCursor    =   cr.query(
                                                        ContactsContract.Contacts.CONTENT_URI,
                                                        null,
                                                        null,
                                                        null,
                                                        sortOrder
                )

                if (deviceContactsCursor != null) {
                    if (deviceContactsCursor.count > 0) {
                        while (deviceContactsCursor.moveToNext()) {
                            val id = deviceContactsCursor
                                .getString(
                                    deviceContactsCursor.getColumnIndexOrThrow(
                                        ContactsContract.Contacts._ID
                                    )
                                )
                            val name = deviceContactsCursor
                                .getString(
                                    deviceContactsCursor.getColumnIndexOrThrow(
                                        ContactsContract.Contacts.DISPLAY_NAME
                                    )
                                )

                            contactsListInPhone.add(
                                Contact(
                                    name,
                                    id.toInt()
                                )
                            )
                        }
                    }
                }
                deviceContactsCursor?.close()
            }
            queryAppDB.join()
            contactsListInPhone.forEach { contact  ->  contactsList.add(contact)}
        }
        return ContactsRecyclerAdapter(
            contactsList,
            inAppNumber
        )
    }

    fun getContacts(isMale: Boolean): ContactsRecyclerAdapter {
        val contactsList    =   if(isMale)
                                    dbHelper.getMaleContacts()
                                else
                                    dbHelper.getFemaleContacts()

        val inAppNumber     =   contactsList.size

        return ContactsRecyclerAdapter(
            contactsList,
            inAppNumber
        )
    }

    fun getFilteredContacts(filter: String): ContactsRecyclerAdapter {
        var contactsList        =   mutableListOf<Contact>()
        val contactsListInPhone =   mutableListOf<Contact>()
        var inAppNumber         =   0

        val queryAppDB          =   GlobalScope.launch {
            contactsList    =    dbHelper.filterContacts(filter)
            inAppNumber     =    contactsList.size
        }

        runBlocking {
            if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ctx.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
            )
            else {
                //Get device contacts if we have permission
                val cr: ContentResolver     =   ctx.contentResolver
                val selection               =   "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?"
                val selectionArgs           =   arrayOf("%$filter%")
                val sortOrder               =   "${ContactsContract.Contacts.DISPLAY_NAME} ASC"
                val deviceContactsCursor    =   cr.query(
                                                        ContactsContract.Contacts.CONTENT_URI,
                                                        null,
                                                        selection,
                                                        selectionArgs,
                                                        sortOrder
                )

                if (deviceContactsCursor != null) {
                    if (deviceContactsCursor.count > 0) {
                        while (deviceContactsCursor.moveToNext()) {
                            val id = deviceContactsCursor
                                .getString(
                                    deviceContactsCursor.getColumnIndexOrThrow(
                                        ContactsContract.Contacts._ID
                                    )
                                )
                            val name = deviceContactsCursor
                                .getString(
                                    deviceContactsCursor.getColumnIndexOrThrow(
                                        ContactsContract.Contacts.DISPLAY_NAME
                                    )
                                )

                            contactsListInPhone.add(
                                Contact(
                                    name,
                                    id.toInt()
                                )
                            )
                        }
                    }
                }
                deviceContactsCursor?.close()
            }
            queryAppDB.join()
            contactsListInPhone.forEach { contact  ->  contactsList.add(contact)}
        }
        return ContactsRecyclerAdapter(
            contactsList,
            inAppNumber
        )
    }

    fun getDeviceContact(contactID: Int): Contact {
        val cr                      :   ContentResolver = ctx.contentResolver
        val contact                 = Contact()
        var deviceContactsCursor    =   cr.query(
                                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                                    null,
                                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                                    arrayOf(contactID.toString()),
                                                    null
        )
        if (deviceContactsCursor != null) {
            while(deviceContactsCursor.moveToNext())
                contact.phone   =   deviceContactsCursor.getString(deviceContactsCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
        }

        deviceContactsCursor    =   cr.query(
                                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                                null,
                                                ContactsContract.CommonDataKinds.Email.CONTACT_ID +" = ?" ,
                                                arrayOf(contactID.toString()),
                                                null
        )
        if (deviceContactsCursor != null) {
            while(deviceContactsCursor.moveToNext())
                contact.email = deviceContactsCursor.getString(deviceContactsCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DATA))
        }

        deviceContactsCursor    =   cr.query(
                                                ContactsContract.Contacts.CONTENT_URI,
                                                null,
                                                ContactsContract.Contacts._ID +" = ?",
                                                arrayOf(contactID.toString()),
                                                null
        )
        if (deviceContactsCursor != null) {
            while((deviceContactsCursor.moveToNext()))
                contact.forename = deviceContactsCursor.getString(deviceContactsCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
        }

        deviceContactsCursor?.close()
        return contact
    }
}