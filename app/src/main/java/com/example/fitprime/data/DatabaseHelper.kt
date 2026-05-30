package com.example.fitprime.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "FitPrime.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_METRICS = "user_metrics"
        private const val COLUMN_ID = "id"
        private const val COLUMN_HEIGHT = "height"
        private const val COLUMN_WEIGHT = "weight"
        private const val COLUMN_GENDER = "gender" // 1 for male, 0 for female

        // Reportes
        private const val TABLE_REPORTS = "workout_reports"
        private const val COLUMN_REPORT_ID = "report_id"
        private const val COLUMN_ROUTINE_NAME = "routine_name"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_SCHEDULED_MIN = "scheduled_min"
        private const val COLUMN_REAL_SEC = "real_sec"
        private const val COLUMN_IS_COMPLETED = "is_completed"
        private const val COLUMN_EXERCISES = "exercises" // Guardado como string separado por comas
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createMetricsTable = ("CREATE TABLE $TABLE_METRICS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY, " +
                "$COLUMN_HEIGHT REAL, " +
                "$COLUMN_WEIGHT REAL, " +
                "$COLUMN_GENDER INTEGER)")
        db?.execSQL(createMetricsTable)

        val createReportsTable = ("CREATE TABLE $TABLE_REPORTS (" +
                "$COLUMN_REPORT_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_ROUTINE_NAME TEXT, " +
                "$COLUMN_DATE INTEGER, " +
                "$COLUMN_SCHEDULED_MIN INTEGER, " +
                "$COLUMN_REAL_SEC INTEGER, " +
                "$COLUMN_IS_COMPLETED INTEGER, " +
                "$COLUMN_EXERCISES TEXT)")
        db?.execSQL(createReportsTable)
        
        // Initialize metrics with default values if empty
        val values = ContentValues().apply {
            put(COLUMN_ID, 1)
            put(COLUMN_HEIGHT, 0.0)
            put(COLUMN_WEIGHT, 0.0)
            put(COLUMN_GENDER, 1)
        }
        db?.insert(TABLE_METRICS, null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            val createReportsTable = ("CREATE TABLE $TABLE_REPORTS (" +
                    "$COLUMN_REPORT_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_ROUTINE_NAME TEXT, " +
                    "$COLUMN_DATE INTEGER, " +
                    "$COLUMN_SCHEDULED_MIN INTEGER, " +
                    "$COLUMN_REAL_SEC INTEGER, " +
                    "$COLUMN_IS_COMPLETED INTEGER, " +
                    "$COLUMN_EXERCISES TEXT)")
            db?.execSQL(createReportsTable)
        }
    }

    fun saveReport(routineName: String, scheduledMin: Int, realSec: Int, isCompleted: Boolean, exercises: List<String>) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ROUTINE_NAME, routineName)
            put(COLUMN_DATE, System.currentTimeMillis())
            put(COLUMN_SCHEDULED_MIN, scheduledMin)
            put(COLUMN_REAL_SEC, realSec)
            put(COLUMN_IS_COMPLETED, if (isCompleted) 1 else 0)
            put(COLUMN_EXERCISES, exercises.joinToString(","))
        }
        db.insert(TABLE_REPORTS, null, values)
        db.close()
    }

    fun getAllReports(): List<com.example.fitprime.ui.reports.WorkoutReport> {
        val reports = mutableListOf<com.example.fitprime.ui.reports.WorkoutReport>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_REPORTS, null, null, null, null, null, "$COLUMN_DATE DESC")

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_REPORT_ID)).toString()
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROUTINE_NAME))
                val date = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                val scheduled = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULED_MIN))
                val real = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REAL_SEC))
                val completed = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1
                val exercisesStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXERCISES))
                val exercises = exercisesStr.split(",").filter { it.isNotEmpty() }

                reports.add(com.example.fitprime.ui.reports.WorkoutReport(
                    id, name, java.util.Date(date), scheduled, real, completed, exercises
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return reports
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
