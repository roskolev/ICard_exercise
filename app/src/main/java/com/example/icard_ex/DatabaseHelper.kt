package com.example.icard_ex

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
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
                                "values('$value', " + countrycodes[index] + ")")
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
    fun getAllContacts(): Cursor {
        val db = this.readableDatabase
        val projection = arrayOf(BaseColumns._ID, ContactsTable.COLUMN_FORENAME, ContactsTable.COLUMN_SURNAME)
        val sortOrder = "${ContactsTable.COLUMN_FORENAME} ASC, " +
                "${ContactsTable.COLUMN_SURNAME} ASC"
        return db.query(
            ContactsTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            sortOrder
        )
    }
    fun getMaleContacts(): Cursor {
        val db = this.readableDatabase
        val projection = arrayOf(BaseColumns._ID, ContactsTable.COLUMN_FORENAME, ContactsTable.COLUMN_SURNAME)
        val sortOrder = "${ContactsTable.COLUMN_FORENAME} ASC, " +
                "${ContactsTable.COLUMN_SURNAME} ASC"
        val selection = "${ContactsTable.COLUMN_GENDER} = ?"
        val selectionArgs = arrayOf("Male")
        return db.query(
            ContactsTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )
    }
    fun getFemaleContacts(): Cursor {
        val db = this.readableDatabase
        val projection = arrayOf(BaseColumns._ID, ContactsTable.COLUMN_FORENAME, ContactsTable.COLUMN_SURNAME)
        val sortOrder = "${ContactsTable.COLUMN_FORENAME} ASC, " +
                "${ContactsTable.COLUMN_SURNAME} ASC"
        val selection = "${ContactsTable.COLUMN_GENDER} = ?"
        val selectionArgs = arrayOf("Female")
        return db.query(
            ContactsTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )
    }
    fun filterContacts(constraint: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT ${ContactsTable.COLUMN_FORENAME}, " +
                                        "${ContactsTable.COLUMN_SURNAME}, " +
                                        "${BaseColumns._ID} FROM ${ContactsTable.TABLE_NAME} " +
                                        "WHERE ${ContactsTable.COLUMN_COUNTRY} like '$constraint%'" +
                                        "OR ${ContactsTable.COLUMN_SURNAME} like '$constraint%'" +
                                        "OR ${ContactsTable.COLUMN_FORENAME} like '$constraint%'" +
                                        "OR ${ContactsTable.COLUMN_EMAIL} like '$constraint%'" +
                                        "OR CAST(${ContactsTable.COLUMN_NUMBER} as TEXT) like '%$constraint%'" +
                                        "ORDER BY ${ContactsTable.COLUMN_FORENAME} ASC, " +
                                        "${ContactsTable.COLUMN_SURNAME} ASC", null)
    }

    fun deleteContact(id: Int){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM ${ContactsTable.TABLE_NAME} WHERE ${BaseColumns._ID} is $id")
    }
    fun getContact(id: Int): Cursor{
        val db = this.readableDatabase
        val projection = arrayOf(ContactsTable.COLUMN_FORENAME, ContactsTable.COLUMN_SURNAME,
                                    ContactsTable.COLUMN_COUNTRY, ContactsTable.COLUMN_EMAIL,
                                    ContactsTable.COLUMN_GENDER, ContactsTable.COLUMN_NUMBER)
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf("$id")
        return db.query(
            ContactsTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
    }
    fun getCountryCode(country: String): Int{
        val db = this.readableDatabase
        val projection = arrayOf(CountriesTable.COLUMN_CODE)
        val selection = "${CountriesTable.COLUMN_COUNTRY} = ?"
        val selectionArgs = arrayOf(country)
        val cursor = db.query(
            CountriesTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        var code = 0
        if(cursor.moveToNext()) {
            code = cursor.getInt(cursor.getColumnIndexOrThrow((CountriesTable.COLUMN_CODE)))
        }
        cursor.close()
        return code
    }
    fun getAllCountries(): Array<String>{
        val db = this.readableDatabase
        val projection = arrayOf(CountriesTable.COLUMN_COUNTRY)
        val sortOrder = "${CountriesTable.COLUMN_COUNTRY} ASC"
        val cursor = db.query(
            CountriesTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            sortOrder
        )
        val countryList = mutableListOf<String>()
        with(cursor){
            while (moveToNext()){
                val country = getString(getColumnIndexOrThrow(CountriesTable.COLUMN_COUNTRY))
                countryList.add(country)
            }
        }
        return countryList.toTypedArray()
    }
    fun countryInDB(country: String): Boolean{
        val db = this.readableDatabase
        val projection = arrayOf(CountriesTable.COLUMN_CODE)
        val selection = "${CountriesTable.COLUMN_COUNTRY} = ?"
        val selectionArgs = arrayOf(country)
        val cursor = db.query(
            CountriesTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        var code = 0
        if(cursor.moveToNext()) {
            code = cursor.getInt(cursor.getColumnIndexOrThrow((CountriesTable.COLUMN_CODE)))
        }
        cursor.close()
        return code != 0
    }

    fun addContact(forename: String, surname: String, email: String, phone: String, gender: String, country: String)
            : Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply{
            put(ContactsTable.COLUMN_FORENAME, forename)
            put(ContactsTable.COLUMN_SURNAME, surname)
            put(ContactsTable.COLUMN_COUNTRY, country)
            put(ContactsTable.COLUMN_EMAIL, email)
            put(ContactsTable.COLUMN_NUMBER, phone)
            put(ContactsTable.COLUMN_GENDER, gender)

        }
        return db.insert(ContactsTable.TABLE_NAME, null, contentValues)
    }

    fun editContact(id: Int, forename: String, surname: String, email: String, phone: String, gender: String, country: String)
            : Int{
        val db = this.writableDatabase
        val contentValues = ContentValues().apply{
            put(ContactsTable.COLUMN_FORENAME, forename)
            put(ContactsTable.COLUMN_SURNAME, surname)
            put(ContactsTable.COLUMN_COUNTRY, country)
            put(ContactsTable.COLUMN_EMAIL, email)
            put(ContactsTable.COLUMN_NUMBER, phone)
            put(ContactsTable.COLUMN_GENDER, gender)

        }
        val where = "${BaseColumns._ID} = ?"
        val whereArg = arrayOf("$id")
        return db.update(
            ContactsTable.TABLE_NAME,
            contentValues,
            where,
            whereArg)
    }
}
object ContactsTable : BaseColumns {
    const val TABLE_NAME = "Contacts"
    const val COLUMN_FORENAME = "forename"
    const val COLUMN_SURNAME = "surname"
    const val COLUMN_COUNTRY = "country"
    const val COLUMN_EMAIL = "email"
    const val COLUMN_NUMBER = "phonenumber"
    const val COLUMN_GENDER = "gender"
}
object CountriesTable : BaseColumns {
    const val TABLE_NAME = "Countries"
    const val COLUMN_COUNTRY = "country"
    const val COLUMN_CODE = "code"
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

private const val SQL_DELETE_TABLE_CONTACTS = "DROP TABLE IF EXISTS ${ContactsTable.TABLE_NAME}"

private const val SQL_DELETE_TABLE_COUNTRY = "DROP TABLE IF EXISTS ${ContactsTable.TABLE_NAME}"

private const val DATABASE_NAME = "ICard.db"

private const val DATABASE_VERSION = 2

val countries = arrayOf("Austria", "Albania", "Andorra", "Armenia", "Belarus", "Belgium",
                        "Bulgaria", "Croatia","Cyprus", "Czech Republic", "Denmark", "Estonia",
                        "Finland", "France", "Germany", "Greece", "Hungary", "Ireland", "Italy",
                        "Kosovo", "Latvia", "Netherlands", "Norway", "Poland", "Portugal", "Romania",
                        "Russia", "Serbia", "Spain", "Switzerland", "Turkey", "United Kingdom")
val countrycodes = arrayOf(43, 355, 376, 374, 375, 32, 395, 385, 357, 420, 45, 372, 358, 33, 49, 30,
                            36, 353, 39, 383, 371, 31, 47, 48, 351, 40, 7, 381, 34, 41, 90, 44)