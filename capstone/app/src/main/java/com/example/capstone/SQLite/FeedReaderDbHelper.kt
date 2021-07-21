package com.example.capstone.SQLite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.example.capstone.TimeTableActivity

private const val SQL_CREATE_ENTRIES =
    "CREATE TABLE if not exists ${TimeTableActivity.FeedEntry.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
            "${TimeTableActivity.FeedEntry.COLUMN_NAME_DAYTIME} CHAR(5), " +
            "${TimeTableActivity.FeedEntry.COLUMN_NAME_DEPT} VARCHAR(10))"
private const val SQL_INIT_TABLE =
    "INSERT INTO ${TimeTableActivity.FeedEntry.TABLE_NAME}(${TimeTableActivity.FeedEntry.COLUMN_NAME_DAYTIME}) VALUES" +
            "('Mon1'), ('Mon2'), ('Mon3'), ('Mon4'), ('Mon5'), ('Mon6'), ('Mon7'), " +
            "('Tue1'), ('Tue2'), ('Tue3'), ('Tue4'), ('Tue5'), ('Tue6'), ('Tue7'), " +
            "('Wed1'), ('Wed2'), ('Wed3'), ('Wed4'), ('Wed5'), ('Wed6'), ('Wed7'), " +
            "('Thu1'), ('Thu2'), ('Thu3'), ('Thu4'), ('Thu5'), ('Thu6'), ('Thu7'), " +
            "('Fri1'), ('Fri2'), ('Fri3'), ('Fri4'), ('Fri5'), ('Fri6'), ('Fri7'), " +
            "('Sat1'), ('Sat2'), ('Sat3'), ('Sat4'), ('Sat5'), ('Sat6'), ('Sat7')"
private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${TimeTableActivity.FeedEntry.TABLE_NAME}"

class FeedReaderDbHelper(context: Context) : SQLiteOpenHelper(context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
        db.execSQL(SQL_INIT_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "timetable.db"
    }
}