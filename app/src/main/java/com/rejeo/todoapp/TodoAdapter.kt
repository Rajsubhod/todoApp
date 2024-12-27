package com.rejeo.todoapp


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class TodoAdapter(private val todoList : List<Todo>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    inner class TodoViewHolder( item : View) : RecyclerView.ViewHolder(item) {
        val todoDesc : TextView = item.findViewById(R.id.todo_desc)
        val isCompleted : CheckBox = item.findViewById(R.id.is_completed)
        val timeCreation : TextView = item.findViewById(R.id.creation_time)
        val card : MaterialCardView = item.findViewById(R.id.todo_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_card,parent,false)
        return TodoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentTodo = todoList[position]
//        val todo = Todo(currentTodo.id, currentTodo.todoDesc, currentTodo.isCompleted, currentTodo.timeCreation)

        holder.apply {
            todoDesc.text = currentTodo.todoDesc
            isCompleted.isChecked = currentTodo.isCompleted
            timeCreation.text = currentTodo.timeCreation.toString()
            if(currentTodo.isCompleted) {
                holder.card.isEnabled = false
            } else{
                holder.card.isEnabled = true
            }
        }

        holder.card.setOnLongClickListener { view ->
            val fragment = (view.context as? MainActivity)
                ?.supportFragmentManager
                ?.fragments
                ?.find { it is TodoViewFragment } as? TodoViewFragment
            fragment?.showUpdateOverlay(currentTodo)
            true
        }

        holder.isCompleted.setOnCheckedChangeListener { _, isChecked ->
            currentTodo.isCompleted = isChecked
            if(currentTodo.isCompleted) {
                holder.card.isEnabled = false
            } else{
                holder.card.isEnabled = true
            }
            DataBaseHelper(holder.itemView.context).updateTodo(currentTodo)
        }
    }
}