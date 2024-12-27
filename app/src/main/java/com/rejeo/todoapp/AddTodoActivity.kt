package com.rejeo.todoapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
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

        val clearBtn : Button = findViewById(R.id.clear_btn)
        val goBtn : Button = findViewById(R.id.go_btn)
        val todoField : TextInputEditText = findViewById(R.id.todo_field)

        clearBtn.setOnClickListener {
            todoField.text?.clear()
        }

        goBtn.setOnClickListener {
            if(todoField.text.toString() != "") {

                Log.d("TODO", "Todo adding started")
                lifecycleScope.launch(Dispatchers.IO) {
                    dbHelper.addTodo(
                        todoField.text.toString(),
                        false,
                        Timestamp(System.currentTimeMillis())
                    )
                    Log.d("TODO", "Todo added in coroutine")
                    Intent(this@AddTodoActivity, MainActivity::class.java).also {
                        setResult(RESULT_OK)
                        finish()
                    }
                }
                Log.d("TODO", "Todo activity completed")
            } else {
                // Focus the field and add an error glow mark
                todoField.requestFocus()
                todoField.error = "This field cannot be empty"
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(todoField, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }
}