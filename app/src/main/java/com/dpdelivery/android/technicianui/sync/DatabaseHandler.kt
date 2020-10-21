package com.dpdelivery.android.technicianui.sync

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.lang.String
import java.util.*

class DatabaseHandler(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    var cursor: Cursor? = null

    // Creating Tables
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE IF NOT EXISTS " + TABLE_COMMANDS + " ( "
                + KEY_ID + " int NOT NULL UNIQUE," + KEY_CMD + " varchar(255),"
                + KEY_STATUS + " varchar(10)" + " )")
        db.execSQL(CREATE_CONTACTS_TABLE)
        val CREATE_PINGS_TABLE = ("CREATE TABLE IF NOT EXISTS " + TABLE_PINGS + " ( "
                + KEY_FLOWLIMIT + " int, " + KEY_CURRENT + " int NOT NULL UNIQUE, "
                + KEY_PUMP + " int, " + KEY_LCS + " int, " + KEY_STATE + " int, " + KEY_TS + " DATETIME" + " )")
        db.execSQL(CREATE_PINGS_TABLE)
        val CREATE_ACK_TABLE = ("CREATE TABLE IF NOT EXISTS " + TABLE_ACK + " ( "
                + KEY_ID + " int NOT NULL UNIQUE," + KEY_CMD + " varchar(255),"
                + KEY_STATUS + " varchar(10)" + " )")
        db.execSQL(CREATE_ACK_TABLE)
    }

    // Upgrading database
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COMMANDS")

        // Create tables again
        onCreate(db)
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
    fun addCommand(command: Command) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_ID, command.id) // Contact Name
        values.put(KEY_CMD, command.cmd) // Contact Name
        values.put(KEY_STATUS, command.status) // Contact Phone

        // Inserting Row
        db.insertWithOnConflict(TABLE_COMMANDS, null, values, SQLiteDatabase.CONFLICT_IGNORE)
        db.close() // Closing database connection
    }

    fun addAck(command: Command) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_ID, command.id) // Contact Name
        values.put(KEY_CMD, command.cmd) // Contact Name
        values.put(KEY_STATUS, command.status) // Contact Phone

        // Inserting Row
        db.insertWithOnConflict(TABLE_ACK, null, values, SQLiteDatabase.CONFLICT_IGNORE)
        db.close() // Closing database connection
    }

    // Getting single contact
    fun changeState(cmd: Command): Int {
        return updateCommand(cmd)
    }

    fun getCmd(id: Int): Command? {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_COMMANDS, arrayOf(KEY_ID,
                KEY_CMD, KEY_STATUS), "$KEY_ID=?", arrayOf(id.toString()), null, null, null, null)
        if (cursor != null) if (!cursor.moveToFirst()) return null
        // return contact
        return Command(cursor!!.getString(0).toInt(),
                cursor.getString(1), cursor.getString(2))
    }

    fun resetCursor() {
        cursor = null
    }

    fun getnextCmd(): Command? {
        if (cursor == null) {
            val db = this.readableDatabase
            cursor = db.query(TABLE_COMMANDS, arrayOf(KEY_ID,
                    KEY_CMD, KEY_STATUS), null, null, null, null, KEY_ID)
            if (!cursor!!.moveToFirst()) return null
        } else if (!cursor!!.moveToNext()) {
            return null
        }
        return Command(cursor!!.getString(0).toInt(),
                cursor!!.getString(1), cursor!!.getString(2))
    }

    // Select All Query
    val allAcks: List<Command>
        get() {
            val commandList: MutableList<Command> = ArrayList()
            // Select All Query
            val selectQuery = "SELECT  * FROM $TABLE_ACK ORDER BY  $KEY_ID ASC "
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    val command = Command()
                    command.id = cursor.getString(0).toInt()
                    command.cmd = cursor.getString(1)
                    command.status = cursor.getString(2)
                    // Adding contact to list
                    commandList.add(command)
                } while (cursor.moveToNext())
            }

            // return contact list
            return commandList
        }// Adding contact to list

    // Updating single contact
    fun updateCommand(command: Command): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_STATUS, command.status)
        Log.i("WaterWalaprime", "status " + command.id + " " + command.status)
        // updating row
        return db.update(TABLE_COMMANDS, values, "$KEY_ID=?", arrayOf(String.valueOf(command.id)))
    }

    fun clearCmds(pc: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_COMMANDS, "$KEY_ID < ?", arrayOf(pc.toString()))
        db.close()
    }

    fun clearPings() {
        val db = this.writableDatabase
        db.execSQL("delete from $TABLE_PINGS")
        db.close()
    }

    // Deleting single contact
    fun deleteCommand(command: Command) {
        val db = this.writableDatabase
        db.delete(TABLE_COMMANDS, "$KEY_ID = ?", arrayOf(String.valueOf(command.id)))
        db.close()
    }// return count

    // Getting contacts Count
    val maxID: Int
        get() {
            val countQuery = "SELECT  MAX($KEY_ID) AS MaxID  FROM $TABLE_COMMANDS"
            var count = 0
            val db = this.readableDatabase
            val cursor = db.rawQuery(countQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    count = cursor.getString(0).toInt()
                } while (cursor.moveToNext())
            }
            cursor.close()

            // return count
            return count
        }

    fun clearAcks() {
        val db = this.writableDatabase
        // db.delete(TABLE_NAME,null,null);
        //db.execSQL("delete * from"+ TABLE_NAME);
        db.execSQL("delete from $TABLE_ACK")
        db.close()
    }

    fun deleteAll() {
        val db = this.writableDatabase
        // db.delete(TABLE_NAME,null,null);
        //db.execSQL("delete * from"+ TABLE_NAME);
        db.execSQL("delete from $TABLE_COMMANDS")
        db.close()
    }

    companion object {
        // All Static variables
        // Database Version
        private const val DATABASE_VERSION = 1

        // Database Name
        private const val DATABASE_NAME = "TechBLEData"

        // Contacts table name
        private const val TABLE_COMMANDS = "commands"
        private const val TABLE_ACK = "commandack"
        private const val TABLE_PINGS = "pings"

        // Contacts Table Columns names
        private const val KEY_ID = "id"
        private const val KEY_CMD = "cmd"
        private const val KEY_STATUS = "status"

        // Contacts Table Columns names
        private const val KEY_FLOWLIMIT = "flowlimit"
        private const val KEY_CURRENT = "current"
        private const val KEY_TS = "timestamp"
        private const val KEY_PUMP = "pump"
        private const val KEY_LCS = "lcs"
        private const val KEY_STATE = "state"
    }

}