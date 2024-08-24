package dev.undefinedteam.gensh1n.utils.task

import org.apache.logging.log4j.Logger

class Tasks {
    private var currentTask: Task? = null;

    private val tasks = mutableListOf<Task>()
    private val failTasks = mutableListOf<Task>()
    private val successTasks = mutableListOf<Task>()

    private var startTime: Long = System.currentTimeMillis();
    private var endTime: Long = System.currentTimeMillis();

    fun reset() {
        currentTask = null;

        tasks.clear()
        failTasks.clear()
        successTasks.clear()

        startTime = System.currentTimeMillis();
        endTime = System.currentTimeMillis();
    }

    fun start() {
        startTime = System.currentTimeMillis()
    }

    fun push(name: String): Tasks {
        val task = Task(name, this)
        if (currentTask != null) {
            task.parentTask = currentTask;
            task.parentTask!!.subTasks.add(task)
        }
        tasks.add(task)
        currentTask = task
        return this
    }

    fun pop(): Tasks {
        currentTask?.pop()
        successTasks.add(currentTask!!)

        currentTask = currentTask!!.parentTask
        return this
    }

    fun popPush(name: String): Tasks {
        return pop().push(name)
    }

    fun done() {
        endTime = System.currentTimeMillis()
    }

    fun show(title: String = "Tasks", log: Logger) {
        log.info("========${title}======== (size: ${successTasks.size}, total: ${endTime - startTime}ms)")
        var index = 0
        successTasks.forEach { it ->
            if (it.parentTask == null) {
                log.info("${index}. ${it.name} use ${it.usedTime()}ms.")
                it.subTasks.forEach { t ->
                    log.info("\t${t.name} use ${t.usedTime()}ms.")
                    t.subTasks.forEach { t1 ->
                        log.info("\t\t${t1.name} use ${t1.usedTime()}ms.")
                        t1.subTasks.forEach { t2 ->
                            log.info("\t\t\t${t2.name} use ${t.usedTime()}ms.")
                        }
                    }
                }

                index++
            }
        }
    }
}
