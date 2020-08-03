package com.example.icard_ex

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_CONTACTS)
        db.execSQL(SQL_CREATE_TABLE_COUNTRIES)

        // Insert Countries and their phone codes into the database
        for((index, value) in countries.withIndex()){
            db.execSQL("INSERT into ${CountriesTable.TABLE_NAME}("+
                                "${CountriesTable.COLUMN_COUNTRY}, " +
                                "${CountriesTable.COLUMN_CODE}) " +
                                "values('$value', " + countrycodes[index] + ")"
            )
        }

        db.execSQL(SQL_POPULATE_TEST)
        db.execSQL(SQL_POPULATE_TEST_1)
        db.execSQL(SQL_POPULATE_TEST)
        db.execSQL(SQL_POPULATE_TEST_1)
        db.execSQL(SQL_POPULATE_TEST)
        db.execSQL(SQL_POPULATE_TEST_1)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_TABLE_CONTACTS)
        db.execSQL(SQL_DELETE_TABLE_COUNTRY)
        onCreate(db)
    }
    fun getAllContacts(): MutableList<Contact> {
        val contactList =   mutableListOf<Contact>()
        val db          =   this.readableDatabase
        val projection  =   arrayOf(BaseColumns._ID, ContactsTable.COLUMN_FORENAME, ContactsTable.COLUMN_SURNAME)
        val sortOrder   =   "${ContactsTable.COLUMN_FORENAME} ASC, " + "${ContactsTable.COLUMN_SURNAME} ASC"
        val cursor      = db.query(
                                    ContactsTable.TABLE_NAME,
                                    projection,
                                    null,
                                    null,
                                    null,
                                    null,
                                    sortOrder
                                    )

        with(cursor) {
            while (moveToNext()) {
                val forename    =   getString(getColumnIndexOrThrow(ContactsTable.COLUMN_FORENAME))
                val surname     =   getString(getColumnIndexOrThrow(ContactsTable.COLUMN_SURNAME))
                val id          =   getInt(getColumnIndexOrThrow(BaseColumns._ID))

                contactList.add(Contact("$forename $surname", id))
            }
        }

        return contactList
    }

    fun getMaleContacts(): MutableList<Contact> {
        val contactList     =   mutableListOf<Contact>()
        val db              =    this.readableDatabase
        val projection      =    arrayOf(BaseColumns._ID, ContactsTable.COLUMN_FORENAME, ContactsTable.COLUMN_SURNAME)
        val sortOrder       =    "${ContactsTable.COLUMN_FORENAME} ASC, " + "${ContactsTable.COLUMN_SURNAME} ASC"
        val selection       =    "${ContactsTable.COLUMN_GENDER} = ?"
        val selectionArgs   =    arrayOf("Male")
        val cursor          = db.query(
                                        ContactsTable.TABLE_NAME,
                                        projection,
                                        selection,
                                        selectionArgs,
                                        null,
                                        null,
                                        sortOrder
                                        )

        with(cursor) {
            while (moveToNext()) {
                val forename    =   getString(getColumnIndexOrThrow(ContactsTable.COLUMN_FORENAME))
                val surname     =   getString(getColumnIndexOrThrow(ContactsTable.COLUMN_SURNAME))
                val id          =   getInt(getColumnIndexOrThrow(BaseColumns._ID))

                contactList.add(Contact("$forename $surname", id))
            }
        }

        return contactList
    }

    fun getFemaleContacts(): MutableList<Contact> {
        val contactList     =   mutableListOf<Contact>()
        val db              =   this.readableDatabase
        val projection      =   arrayOf(BaseColumns._ID, ContactsTable.COLUMN_FORENAME, ContactsTable.COLUMN_SURNAME)
        val sortOrder       =   "${ContactsTable.COLUMN_FORENAME} ASC, " + "${ContactsTable.COLUMN_SURNAME} ASC"
        val selection       =   "${ContactsTable.COLUMN_GENDER} = ?"
        val selectionArgs   =   arrayOf("Female")
        val cursor          = db.query(
                                        ContactsTable.TABLE_NAME,
                                        projection,
                                        selection,
                                        selectionArgs,
                                        null,
                                        null,
                                        sortOrder
                                    )

        with(cursor) {
            while (moveToNext()) {
                val forename    =   getString(getColumnIndexOrThrow(ContactsTable.COLUMN_FORENAME))
                val surname     =   getString(getColumnIndexOrThrow(ContactsTable.COLUMN_SURNAME))
                val id          =   getInt(getColumnIndexOrThrow(BaseColumns._ID))

                contactList.add(Contact("$forename $surname", id))
            }
        }

        return contactList
    }

    fun filterContacts(constraint: String): MutableList<Contact> {
        val contactList =   mutableListOf<Contact>()
        val db          =   this.readableDatabase
        val cursor      = db.rawQuery(
                                    "SELECT ${ContactsTable.COLUMN_FORENAME}, " +
                                        "${ContactsTable.COLUMN_SURNAME}, " +
                                        "${BaseColumns._ID} FROM ${ContactsTable.TABLE_NAME} " +
                                        "WHERE ${ContactsTable.COLUMN_COUNTRY} like '$constraint%'" +
                                        "OR ${ContactsTable.COLUMN_SURNAME} like '$constraint%'" +
                                        "OR ${ContactsTable.COLUMN_FORENAME} like '$constraint%'" +
                                        "OR ${ContactsTable.COLUMN_EMAIL} like '$constraint%'" +
                                        "OR CAST(${ContactsTable.COLUMN_NUMBER} as TEXT) like '%$constraint%'" +
                                        "ORDER BY ${ContactsTable.COLUMN_FORENAME} ASC, " +
                                        "${ContactsTable.COLUMN_SURNAME} ASC", null
        )

        with(cursor) {
            while (moveToNext()) {
                val forename    =   getString(getColumnIndexOrThrow(ContactsTable.COLUMN_FORENAME))
                val surname     =   getString(getColumnIndexOrThrow(ContactsTable.COLUMN_SURNAME))
                val id          =   getInt(getColumnIndexOrThrow(BaseColumns._ID))

                contactList.add(Contact("$forename $surname", id))
            }
        }

        return contactList
    }

    fun deleteContact(id: Int){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM ${ContactsTable.TABLE_NAME} WHERE ${BaseColumns._ID} is $id")
    }

    fun getContact(id: Int): Contact{
        val db              =   this.readableDatabase
        val selection       =   "${BaseColumns._ID} = ?"
        val selectionArgs   =   arrayOf("$id")
        val projection      =   arrayOf(
                                            ContactsTable.COLUMN_FORENAME,
                                            ContactsTable.COLUMN_SURNAME,
                                            ContactsTable.COLUMN_COUNTRY,
                                            ContactsTable.COLUMN_EMAIL,
                                            ContactsTable.COLUMN_GENDER,
                                            ContactsTable.COLUMN_NUMBER
        )
        val cursor          =   db.query(
                                            ContactsTable.TABLE_NAME,
                                            projection,
                                            selection,
                                            selectionArgs,
                                            null,
                                            null,
                                            null
        )

        cursor.moveToNext()
        val contact =   Contact(
                                cursor.getString(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_FORENAME)),
                                cursor.getString(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_SURNAME)),
                                cursor.getString(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_EMAIL)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_NUMBER)).toString(),
                                cursor.getString(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_GENDER)),
                                cursor.getString(cursor.getColumnIndexOrThrow(ContactsTable.COLUMN_COUNTRY))
        )
        cursor.close()
        return contact
    }
    fun getCountryCode(country: String): Int{
        val db              =   this.readableDatabase
        val projection      =   arrayOf(CountriesTable.COLUMN_CODE)
        val selection       =   "${CountriesTable.COLUMN_COUNTRY} = ?"
        val selectionArgs   =   arrayOf(country)
        var code            =   0

        val cursor          = db.query(
                                        CountriesTable.TABLE_NAME,
                                        projection,
                                        selection,
                                        selectionArgs,
                                        null,
                                        null,
                                        null
        )

        if(cursor.moveToNext())
            code = cursor.getInt(cursor.getColumnIndexOrThrow((CountriesTable.COLUMN_CODE)))

        cursor.close()
        return code
    }
    fun getAllCountries(): Array<String>{
        val countryList =   mutableListOf<String>()
        val db          =   this.readableDatabase
        val projection  =   arrayOf(CountriesTable.COLUMN_COUNTRY)
        val sortOrder   =   "${CountriesTable.COLUMN_COUNTRY} ASC"
        val cursor      =   db.query(
                                        CountriesTable.TABLE_NAME,
                                        projection,
                                        null,
                                        null,
                                        null,
                                        null,
                                        sortOrder
        )

        with(cursor){
            while (moveToNext()){
                val country = getString(getColumnIndexOrThrow(CountriesTable.COLUMN_COUNTRY))
                countryList.add(country)
            }
        }
        return countryList.toTypedArray()
    }
    fun countryInDB(country: String): Boolean{
        var code            =   0
        val db              =   this.readableDatabase
        val projection      =   arrayOf(CountriesTable.COLUMN_CODE)
        val selection       =   "${CountriesTable.COLUMN_COUNTRY} = ?"
        val selectionArgs   =   arrayOf(country)
        val cursor          =   db.query(
                                            CountriesTable.TABLE_NAME,
                                            projection,
                                            selection,
                                            selectionArgs,
                                            null,
                                            null,
                                            null
        )

        if(cursor.moveToNext()) {
            code = cursor.getInt(cursor.getColumnIndexOrThrow((CountriesTable.COLUMN_CODE)))
        }

        cursor.close()
        return code != 0
    }

    fun addContact(contact: Contact): Long {
        val db              =   this.writableDatabase
        val contentValues   =   ContentValues().apply{
                                                    put(ContactsTable.COLUMN_FORENAME, contact.forename)
                                                    put(ContactsTable.COLUMN_SURNAME, contact.surname)
                                                    put(ContactsTable.COLUMN_COUNTRY, contact.country)
                                                    put(ContactsTable.COLUMN_EMAIL, contact.email)
                                                    put(ContactsTable.COLUMN_NUMBER, contact.phone)
                                                    put(ContactsTable.COLUMN_GENDER, contact.gender)
        }

        return db.insert(ContactsTable.TABLE_NAME, null, contentValues)
    }

    fun editContact(contact: Contact, id: Int): Int{
        val db              =   this.writableDatabase
        val where           =   "${BaseColumns._ID} = ?"
        val whereArg        =   arrayOf("$id")
        val contentValues   =   ContentValues().apply{
                                                        put(ContactsTable.COLUMN_FORENAME, contact.forename)
                                                        put(ContactsTable.COLUMN_SURNAME, contact.surname)
                                                        put(ContactsTable.COLUMN_COUNTRY, contact.country)
                                                        put(ContactsTable.COLUMN_EMAIL, contact.email)
                                                        put(ContactsTable.COLUMN_NUMBER, contact.phone)
                                                        put(ContactsTable.COLUMN_GENDER, contact.gender)
        }

        return db.update(
                            ContactsTable.TABLE_NAME,
                            contentValues,
                            where,
                            whereArg
        )
    }
}
object ContactsTable : BaseColumns {
    const val TABLE_NAME        =   "Contacts"
    const val COLUMN_FORENAME   =   "forename"
    const val COLUMN_SURNAME    =   "surname"
    const val COLUMN_COUNTRY    =   "country"
    const val COLUMN_EMAIL      =   "email"
    const val COLUMN_NUMBER     =   "phonenumber"
    const val COLUMN_GENDER     =   "gender"
}
object CountriesTable : BaseColumns {
    const val TABLE_NAME        =   "Countries"
    const val COLUMN_COUNTRY    =   "country"
    const val COLUMN_CODE       =   "code"
}
private const val SQL_CREATE_TABLE_CONTACTS =
            "CREATE TABLE ${ContactsTable.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${ContactsTable.COLUMN_FORENAME} TEXT," +
            "${ContactsTable.COLUMN_SURNAME} TEXT," +
            "${ContactsTable.COLUMN_COUNTRY} TEXT," +
            "${ContactsTable.COLUMN_EMAIL} TEXT," +
            "${ContactsTable.COLUMN_NUMBER} ," +
            "${ContactsTable.COLUMN_GENDER} TEXT )"

private const val SQL_POPULATE_TEST =
            "INSERT into ${ContactsTable.TABLE_NAME}(" +
            "${ContactsTable.COLUMN_FORENAME}," +
            "${ContactsTable.COLUMN_SURNAME}," +
            "${ContactsTable.COLUMN_COUNTRY}," +
            "${ContactsTable.COLUMN_EMAIL}," +
            "${ContactsTable.COLUMN_NUMBER}," +
            "${ContactsTable.COLUMN_GENDER}) " +
            "values('Rostislav', 'Kolev', " +
            "'Bulgaria', 'roskokolev@gmail.com', "+
            "896631746, 'Male')"
private const val SQL_POPULATE_TEST_1 =
            "INSERT into ${ContactsTable.TABLE_NAME}(" +
            "${ContactsTable.COLUMN_FORENAME}," +
            "${ContactsTable.COLUMN_SURNAME}," +
            "${ContactsTable.COLUMN_COUNTRY}," +
            "${ContactsTable.COLUMN_EMAIL}," +
            "${ContactsTable.COLUMN_NUMBER}," +
            "${ContactsTable.COLUMN_GENDER}) " +
            "values('Svetoslava', 'Lazarova', " +
            "'Greece', 'svetl@gmail.com', "+
            "896631745, 'Female')"

private const val SQL_CREATE_TABLE_COUNTRIES=
            "CREATE TABLE ${CountriesTable.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${CountriesTable.COLUMN_COUNTRY} TEXT," +
            "${CountriesTable.COLUMN_CODE})"

private const val SQL_DELETE_TABLE_CONTACTS =   "DROP TABLE IF EXISTS ${ContactsTable.TABLE_NAME}"

private const val SQL_DELETE_TABLE_COUNTRY  =   "DROP TABLE IF EXISTS ${ContactsTable.TABLE_NAME}"

private const val DATABASE_NAME             =   "ICard.db"

private const val DATABASE_VERSION          =   2

val countries       = arrayOf(
                            "Austria", "Albania", "Andorra", "Armenia", "Belarus", "Belgium",
                            "Bulgaria", "Croatia","Cyprus", "Czech Republic", "Denmark", "Estonia",
                            "Finland", "France", "Germany", "Greece", "Hungary", "Ireland", "Italy",
                            "Kosovo", "Latvia", "Netherlands", "Norway", "Poland", "Portugal", "Romania",
                            "Russia", "Serbia", "Spain", "Switzerland", "Turkey", "United Kingdom"
)
val countrycodes    = arrayOf(
                            43, 355, 376, 374, 375, 32, 359, 385, 357, 420, 45, 372, 358, 33, 49, 30,
                            36, 353, 39, 383, 371, 31, 47, 48, 351, 40, 7, 381, 34, 41, 90, 44
)