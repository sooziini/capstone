package com.example.capstone.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

private const val SQL_CREATE_ENTRIES =
    "CREATE TABLE IF NOT EXISTS ${TodoEntry.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
            "${TodoEntry.COLUMN_NAME_DATE} TEXT, " +
            "${TodoEntry.COLUMN_NAME_TODOLIST} TEXT, " +
            "${TodoEntry.COLUMN_NAME_CHECK} TEXT)"

private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${TodoEntry.TABLE_NAME}"

class TodoListDBHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        const val DATABASE_NAME = "todoList.db"
        const val DATABASE_VERSION = 1
    }
}

object TodoEntry : BaseColumns {
    const val TABLE_NAME = "todolist"
    const val COLUMN_NAME_DATE = "daydate"
    const val COLUMN_NAME_TODOLIST = "body"
    const val COLUMN_NAME_CHECK = "ck"
}