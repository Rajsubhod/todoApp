package com.rejeo.todoapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Timestamp

class AddTodoActivity : AppCompatActivity() {

    private val dbHelper = DataBaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_todo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        setSupportActionBar(findViewById(R.id.app_bar))

        val clearBtn : Button = findViewById(R.id.clear_btn)
        val goBtn : Button = findViewById(R.id.go_btn)
        val todoField : TextInputEditText = findViewById(R.id.todo_field)

        clearBtn.setOnClickListener {
            todoField.text?.clear()
        }

        goBtn.setOnClickListener {
            Log.d("TODO", "Todo adding started")
            lifecycleScope.launch(Dispatchers.IO) {
                dbHelper.addTodo(todoField.text.toString(),false, Timestamp(System.currentTimeMillis()))
                Log.d("TODO", "Todo added in coroutine")
                Intent(this@AddTodoActivity, MainActivity::class.java).also {
                    setResult(RESULT_OK)
                    finish()
                }
            }
            Log.d("TODO", "Todo activity completed")
        }
    }
}