package com.rejeo.todoapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    private lateinit var todoList : List<Todo>
    private lateinit var homeFragment: HomeFragment
    private lateinit var todoViewFragment: TodoViewFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        setSupportActionBar(findViewById(R.id.app_bar))

        val addBtn = findViewById<FloatingActionButton>(R.id.add_btn)

        val todoAddActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            result : ActivityResult ->
            run {
                if (result.resultCode == Activity.RESULT_OK) {
                    todoList = DataBaseHelper(this).getTodos()

                    val fragment = supportFragmentManager.findFragmentById(R.id.todoView)
                    if (fragment is TodoViewFragment) {
                        fragment.updateTodoList(todoList) // Dynamically update the list
                    } else {
                        // If not the active fragment, recreate it
                        todoViewFragment = TodoViewFragment.newInstance(todoList as ArrayList<Todo>)
                        fragmentInVIew()
                    }
                }
            }
        }

        addBtn.setOnClickListener {
            Intent(this@MainActivity, AddTodoActivity::class.java).also { intent ->
                todoAddActivityLauncher.launch(intent)
            }
        }

        todoList = DataBaseHelper(this).getTodos()

        homeFragment = HomeFragment()
        todoViewFragment = TodoViewFragment.newInstance(todoList as ArrayList<Todo>)

        fragmentInVIew()

    }


    private fun fragmentInVIew() {
        if (todoList.isEmpty()){
            setCurrentFragment(homeFragment)
        } else {
            setCurrentFragment(todoViewFragment)
        }
    }

    private fun setCurrentFragment(fragment : Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainContainer,fragment)
            commit()
        }
    }
}