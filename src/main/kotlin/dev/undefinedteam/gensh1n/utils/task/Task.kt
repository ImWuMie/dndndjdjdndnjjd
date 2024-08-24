package dev.undefinedteam.gensh1n.utils.task

import lombok.Getter

class Task(val name: String, val main: Tasks) {
    val createTime: Long = System.currentTimeMillis()
    private var endTime: Long = -1L
    var usedTime: Long = -1L
    var parentTask: Task? = null
    val subTasks = mutableListOf<Task>()

    fun pop() {
        endTime = System.currentTimeMillis()
        usedTime = endTime - createTime
    }

    fun usedTime(): Long {
        return usedTime;
    }
}
