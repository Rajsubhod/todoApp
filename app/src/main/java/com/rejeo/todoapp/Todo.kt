package com.rejeo.todoapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

@Parcelize
data class Todo(
    val id:Int,
    var todoDesc:String,
    var isCompleted:Boolean,
    var timeCreation:Timestamp
) : Parcelable
