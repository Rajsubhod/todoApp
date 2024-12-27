package com.rejeo.todoapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import java.sql.Timestamp

class TodoViewFragment : Fragment(R.layout.fragment_todo_view) {

    private lateinit var todoList: List<Todo>
    private lateinit var adapter: TodoAdapter
    private lateinit var todoRecyclerView: RecyclerView
    private lateinit var updateOverlay: FrameLayout
    private lateinit var editTodoText: TextInputEditText
    private var selectedTodo: Todo? = null

    companion object {
        private const val ARG_LIST = "list"

        fun newInstance(list: ArrayList<Todo>): TodoViewFragment {
            val fragment = TodoViewFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_LIST, list) // Pass the list
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        todoList =
            arguments?.getParcelableArrayList(ARG_LIST,Todo::class.java)!! // Retrieve the list

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_todo_view, container, false)
        todoRecyclerView = view.findViewById(R.id.todoView)

        // Inflate the update_todo_hover layout and add it to the fragment's view hierarchy
        val updateOverView = inflater.inflate(R.layout.update_todo_hover,container,false)
        updateOverlay = updateOverView.findViewById(R.id.update_overlay)
        editTodoText = updateOverlay.findViewById(R.id.edit_todo_text)
        (view as ViewGroup).addView(updateOverView)

        // Set the updateOverlay visibility to GONE initially
        updateOverView.visibility = View.GONE

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TodoAdapter(todoList)
        todoRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        todoRecyclerView.adapter = adapter

        updateOverlay.findViewById<MaterialButton>(R.id.update_button).setOnClickListener {
            selectedTodo?.let {
                it.todoDesc = editTodoText.text.toString()
                it.timeCreation = Timestamp(System.currentTimeMillis())
                // Update the todo in the database
                DataBaseHelper(requireContext()).updateTodo(it)
                // Refresh the list
               adapter.notifyDataSetChanged()

                hideUpdateOverlay()
            }
        }

        updateOverlay.findViewById<MaterialButton>(R.id.cancel_button).setOnClickListener {
            hideUpdateOverlay()
        }

        updateOverlay.findViewById<View>(R.id.dim_background).setOnClickListener {
            hideUpdateOverlay()
        }

        updateOverlay.findViewById<MaterialCardView>(R.id.box).setOnClickListener {

        }
    }

    fun showUpdateOverlay(todo: Todo) {
        selectedTodo = todo
        editTodoText.setText(todo.todoDesc)
        updateOverlay.visibility = View.VISIBLE
    }


    fun updateTodoList(newList: List<Todo>) {
        todoList = newList
        adapter.notifyItemInserted(todoList.size-1) // Notify the adapter of the data change
    }

    private fun hideUpdateOverlay() {
        updateOverlay.visibility = View.GONE
        selectedTodo = null
        editTodoText.text = null
    }

}