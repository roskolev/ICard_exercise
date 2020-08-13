package com.example.icard_ex.helpers

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.ContactsContract
import com.example.icard_ex.models.Contact
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@Suppress("ControlFlowWithEmptyBody")
class ContactsHelper(context: Context) {
    private val dbHelper    =
        DatabaseHelper(context)
    private val ctx         =   context

    fun initAllContacts(): MutableList<Contact> {
        var contactsList        =   mutableListOf<Contact>()
        val contactsListInPhone =   mutableListOf<Contact>()

        val queryAppDB          =   GlobalScope.launch {
            contactsList    =    dbHelper.getAllContacts()
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
                            val id      =   deviceContactsCursor.getString(deviceContactsCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                            val name    =   deviceContactsCursor.getString(deviceContactsCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))

                            contactsListInPhone.add(Contact(name, id.toInt(), false))
                        }
                    }
                }
                deviceContactsCursor?.close()
            }

            queryAppDB.join()
            contactsListInPhone.forEach { contact  ->  contactsList.add(contact)}
        }

        return contactsList
    }

    fun getContactsByGender(isMale: Boolean): MutableList<Contact> {
        return if(isMale)
                    dbHelper.getMaleContacts()
                else
                    dbHelper.getFemaleContacts()
    }

    fun getFilteredContacts(filter: String): MutableList<Contact> {
        var contactsList        =   mutableListOf<Contact>()
        val contactsListInPhone =   mutableListOf<Contact>()

        val queryAppDB          =   GlobalScope.launch {
            contactsList    =    dbHelper.filterContacts(filter)
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
                            val id      =   deviceContactsCursor.getString(deviceContactsCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                            val name    =   deviceContactsCursor.getString(deviceContactsCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))

                            contactsListInPhone.add(Contact(name, id.toInt(), false))
                        }
                    }
                }
                deviceContactsCursor?.close()
            }

            queryAppDB.join()
            contactsListInPhone.forEach { contact  ->  contactsList.add(contact)}
        }

        return contactsList

    }

    fun getDeviceContact(contactID: Int): Contact {
        val cr                      :   ContentResolver = ctx.contentResolver
        val contact                 =   Contact()
        val deviceCursor = cr.query(
                                    ContactsContract.Data.CONTENT_URI,
                                    arrayOf(ContactsContract.Data.DISPLAY_NAME, ContactsContract.Data.MIMETYPE, ContactsContract.Data.DATA1),
                                    ContactsContract.Data.CONTACT_ID +" = ?",
                                    arrayOf(contactID.toString()),
                                    null
        )

        if (deviceCursor != null) {
            while(deviceCursor.moveToNext()){

                if(deviceCursor.getString(deviceCursor.getColumnIndexOrThrow(ContactsContract.Data.MIMETYPE)) == ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    contact.phone       =   deviceCursor.getString(deviceCursor.getColumnIndexOrThrow(ContactsContract.Data.DATA1))

                if(deviceCursor.getString(deviceCursor.getColumnIndexOrThrow(ContactsContract.Data.MIMETYPE)) == ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    contact.email       =   deviceCursor.getString(deviceCursor.getColumnIndexOrThrow(ContactsContract.Data.DATA1))

                contact.fullname    =   deviceCursor.getString(deviceCursor.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME))
            }
        }
        deviceCursor?.close()
        return contact
    }
}