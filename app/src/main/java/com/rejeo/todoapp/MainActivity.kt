package com.rejeo.todoapp

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var todoList : List<Todo>
    private lateinit var homeFragment: HomeFragment
    private lateinit var todoViewFragment: TodoViewFragment
    private var selectAll : Int = 0
    private var newList : ArrayList<Todo> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(findViewById(R.id.app_bar))

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.todo_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.select_all -> {
                todoList = DataBaseHelper(this).getTodos()
                if(selectAll == 0) {
                    selectAll = 1
                    for (t in todoList){
                        if(!t.isCompleted){
                            t.isCompleted=true
                            lifecycleScope.launch(Dispatchers.IO) {
                                DataBaseHelper(this@MainActivity).updateTodo(t)
                            }
                        }
                    }
                    todoViewFragment.updateListCompletion(todoList)
                } else {
                    selectAll = 0
                    for (t in todoList) {
                        t.isCompleted = false
                        lifecycleScope.launch(Dispatchers.IO) {
                            DataBaseHelper(this@MainActivity).updateTodo(t)
                        }
                    }
                    todoViewFragment.updateListCompletion(todoList)
                }
                true
            }
            R.id.delete_selected -> {
                confirmDialog(todoList)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    private fun confirmDialog(list: List<Todo>) {
        AlertDialog.Builder(this)
            .setTitle("Delete Todo")
            .setMessage("Do you want to delete the selected todos ?")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                for (t in list){
                    if(t.isCompleted) {
                        newList.add(t)
                        DataBaseHelper(this).deleteTodos(t)
                    }
                }
                Intent(this@MainActivity,MainActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            })
            .setNegativeButton("No") { dialog, which ->
                dialog.cancel()

            }.create().show()
    }
}