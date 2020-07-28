package com.example.icard_ex

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.ContactsContract
import kotlinx.android.synthetic.main.activity_details.*

class ContactsHelper(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val ctx = context
    
    fun initAllContacts(): ContactsRecyclerAdapter {
        val contactsList    =   mutableListOf<String>()
        val idsList         =   mutableListOf<Int>()
        val contactsCursor  =   dbHelper.getAllContacts()

        with(contactsCursor){
            while (moveToNext()){
                val forename    =   getString(getColumnIndexOrThrow(ContactsTable.COLUMN_FORENAME))
                val surname     =   getString(getColumnIndexOrThrow(ContactsTable.COLUMN_SURNAME))
                val id          =   getInt(getColumnIndexOrThrow(android.provider.BaseColumns._ID))

                contactsList.add("$forename $surname")
                idsList.add(id)
            }
        }
        val inAppNumber = contactsList.size

        //Get device contacts if we have permission
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M                                                  &&
            ctx.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
        )
        else {
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
                                    .getString(deviceContactsCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                        val name = deviceContactsCursor
                                    .getString(deviceContactsCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                        
                        contactsList.add(name)
                        idsList.add(id.toInt())
                    }
                }
            }
            deviceContactsCursor?.close()
        }
        return ContactsRecyclerAdapter(contactsList, idsList, inAppNumber)
    }

    fun getMaleContacts(): ContactsRecyclerAdapter{
        val contactsList    =   mutableListOf<String>()
        val idsList         =   mutableListOf<Int>()
        val contactsCursor  =   dbHelper.getMaleContacts()

        with(contactsCursor){
            while (moveToNext()){
                val forename    =   getString(getColumnIndexOrThrow(ContactsTable.COLUMN_FORENAME))
                val surname     =   getString(getColumnIndexOrThrow(ContactsTable.COLUMN_SURNAME))
                val id          =   getInt(getColumnIndexOrThrow(android.provider.BaseColumns._ID))

                contactsList.add("$forename $surname")
                idsList.add(id)
            }
        }
        val inAppNumber = contactsList.size

        return ContactsRecyclerAdapter(contactsList, idsList, inAppNumber)
    }
    fun getFemaleContacts(): ContactsRecyclerAdapter{
        val contactsList    =   mutableListOf<String>()
        val idsList         =   mutableListOf<Int>()
        val contactsCursor  =   dbHelper.getFemaleContacts()

        with(contactsCursor){
            while (moveToNext()){
                val forename    =   getString(getColumnIndexOrThrow(ContactsTable.COLUMN_FORENAME))
                val surname     =   getString(getColumnIndexOrThrow(ContactsTable.COLUMN_SURNAME))
                val id          =   getInt(getColumnIndexOrThrow(android.provider.BaseColumns._ID))

                contactsList.add("$forename $surname")
                idsList.add(id)
            }
        }
        val inAppNumber = contactsList.size

        return ContactsRecyclerAdapter(contactsList, idsList, inAppNumber)
    }

    fun getFilteredContacts(filter: String): ContactsRecyclerAdapter {
        val contactsList = mutableListOf<String>()
        val idsList = mutableListOf<Int>()
        val contactsCursor = dbHelper.filterContacts(filter)

        with(contactsCursor) {
            while (moveToNext()) {
                val forename = getString(getColumnIndexOrThrow(ContactsTable.COLUMN_FORENAME))
                val surname = getString(getColumnIndexOrThrow(ContactsTable.COLUMN_SURNAME))
                val id = getInt(getColumnIndexOrThrow(android.provider.BaseColumns._ID))

                contactsList.add("$forename $surname")
                idsList.add(id)
            }
        }
        val inAppNumber = contactsList.size
        //Get device contacts if we have permission
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M                                                  &&
            ctx.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
        )
        else {
            //TODO - make device contacts filterable
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
                            .getString(deviceContactsCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                        val name = deviceContactsCursor
                            .getString(deviceContactsCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))

                        contactsList.add(name)
                        idsList.add(id.toInt())
                    }
                }
            }
            deviceContactsCursor?.close()
        }
        return ContactsRecyclerAdapter(contactsList, idsList, inAppNumber)
    }

    fun getDeviceContact(contactID: Int): Contact {
        val cr: ContentResolver = ctx.contentResolver
        val contact = Contact()
        var deviceCursor = cr.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
            arrayOf(contactID.toString()), null
        )
        if (deviceCursor != null) {
            while(deviceCursor.moveToNext())
                contact.phone = deviceCursor.getString(
                    deviceCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
        }
        deviceCursor = cr.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            null, ContactsContract.CommonDataKinds.Email.CONTACT_ID +" = ?"
            , arrayOf(contactID.toString()), null
        )
        if (deviceCursor != null) {
            while(deviceCursor.moveToNext())
                contact.email = deviceCursor.getString(
                    deviceCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DATA))
        }
        deviceCursor = cr.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, ContactsContract.Contacts._ID +" = ?",
            arrayOf(contactID.toString()), null
        )
        if (deviceCursor != null) {
            while((deviceCursor.moveToNext()))
                contact.forename = deviceCursor.getString(
                    deviceCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
        }
        deviceCursor?.close()
        return contact
    }
}