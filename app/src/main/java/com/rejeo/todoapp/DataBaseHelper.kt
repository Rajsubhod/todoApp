package com.rejeo.todoapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.sql.Timestamp

class DataBaseHelper(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private var TABLE_NAME = "todo"
    private var COLUMN_id = "_id"
    private var COLUMN_des = "des"
    private var COLUMN_isCompeted = "isComplete"
    private var COLUMN_time_of_creation = "timeCreation"

    companion object {
        private const val DATABASE_NAME = "todo.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE $TABLE_NAME(" +
                "$COLUMN_id INTEGER PRIMARY KEY," +
                "$COLUMN_des VARCHAR(255)," +
                "$COLUMN_isCompeted INTEGER," +
                "$COLUMN_time_of_creation TIMESTAMP" +
                ");"
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val query = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(query)
    }

    fun addTodo(des : String, isCompleted : Boolean, timeCreation : Timestamp) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_des,des)
            put(COLUMN_isCompeted, if (isCompleted) 1 else 0)
            put(COLUMN_time_of_creation, timeCreation.toString())
        }

        val result : Long = db.insert(TABLE_NAME,null,values)
        (context as AppCompatActivity).runOnUiThread {

            if (result == -1L) {
                Toast.makeText(context, "Adding new Todo Failed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "New Todo Added Successfully", Toast.LENGTH_SHORT).show()
            }

        }
        db.close()
    }

    fun getTodos() : ArrayList<Todo> {
        val todoList = arrayListOf<Todo>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME;"
        val cursor = db.rawQuery(query,null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_id))
                val todoDesc = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_des))
                val isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_isCompeted)) == 1
                val timeCreation = Timestamp.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_time_of_creation)))
                val todo = Todo(id,todoDesc, isCompleted, timeCreation)
                todoList.add(todo)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return todoList
    }

    fun updateTodo(todo : Todo) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_id,todo.id)
            put(COLUMN_des,todo.todoDesc)
            put(COLUMN_isCompeted,todo.isCompleted)
            put(COLUMN_time_of_creation,todo.timeCreation.toString())
        }
        val result = db.update(TABLE_NAME,values,"$COLUMN_id=?", arrayOf(todo.id.toString()))
        (context as AppCompatActivity).runOnUiThread {
            if (result == -1) {
                Toast.makeText(context, "Update Failed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Update Successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteTodo(todo : Todo) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME,"${todo.id}=?", arrayOf(todo.id.toString()))
    }

    fun deleteTodos() {
        val db = this.writableDatabase
        val query = "TRUNCATE TABLE $TABLE_NAME;"
        db.execSQL(query)
        db.close()
    }
}