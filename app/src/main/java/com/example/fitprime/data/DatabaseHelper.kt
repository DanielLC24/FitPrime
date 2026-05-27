package com.example.fitprime.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "FitPrime.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_METRICS = "user_metrics"
        private const val COLUMN_ID = "id"
        private const val COLUMN_HEIGHT = "height"
        private const val COLUMN_WEIGHT = "weight"
        private const val COLUMN_GENDER = "gender" // 1 for male, 0 for female
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_METRICS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY, " +
                "$COLUMN_HEIGHT REAL, " +
                "$COLUMN_WEIGHT REAL, " +
                "$COLUMN_GENDER INTEGER)")
        db?.execSQL(createTable)
        
        // Initialize with default values if empty
        val values = ContentValues().apply {
            put(COLUMN_ID, 1)
            put(COLUMN_HEIGHT, 0.0)
            put(COLUMN_WEIGHT, 0.0)
            put(COLUMN_GENDER, 1)
        }
        db?.insert(TABLE_METRICS, null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_METRICS")
        onCreate(db)
    }

    fun saveMetrics(height: Double, weight: Double, isMale: Boolean) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_HEIGHT, height)
            put(COLUMN_WEIGHT, weight)
            put(COLUMN_GENDER, if (isMale) 1 else 0)
        }
        db.update(TABLE_METRICS, values, "$COLUMN_ID = ?", arrayOf("1"))
        db.close()
    }

    fun getMetrics(): Triple<Double, Double, Boolean> {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_METRICS, null, "$COLUMN_ID = ?", arrayOf("1"), null, null, null)
        
        var height = 0.0
        var weight = 0.0
        var isMale = true
        
        if (cursor.moveToFirst()) {
            height = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT))
            weight = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT))
            isMale = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GENDER)) == 1
        }
        cursor.close()
        db.close()
        return Triple(height, weight, isMale)
    }
}
