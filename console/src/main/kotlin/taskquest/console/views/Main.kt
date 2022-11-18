package taskquest.console.views

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import taskquest.console.controllers.CommandFactory
import taskquest.console.controllers.ShowCommand
import taskquest.utilities.controllers.CloudUtils
import taskquest.utilities.controllers.SaveUtils
import taskquest.utilities.models.Store
import taskquest.utilities.models.User
import java.io.File
import java.lang.Exception
import java.net.ConnectException
import kotlinx.coroutines.*

var currentUser = User()
var currentList = -1
var store = Store()

fun main(args: Array<String>) {
    val mapper = jacksonObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
//    currentUser = try {
//        val res = CloudUtils.getUsers()
//        mapper.readValue<User>(res)
//    } catch (_: ConnectException) {
//        SaveUtils.saveUserData(currentUser)
//        SaveUtils.restoreUserData()
//    }
    currentUser = SaveUtils.restoreUserData()
    currentList = currentUser.lastUsedList

    try {
        val res = CloudUtils.getStores()
        store = mapper.readValue<Store>(res)
    } catch (_: ConnectException) {}

    val taskCommands = listOf<String>("add", "del", "show", "edit", "sort", "complete")
    val userCommands = listOf<String>("addtags", "deltag", "showtags", "wallet", "help")

    println("Welcome to TaskQuest Console.")
    println("Enter 'help' for a detailed description of each supported command.")
    println("")

    if (currentList == -1) {
        println("You have no currently active list.")
    } else {
        println("Your currently active list is the ${currentUser.lists[currentList].title} list.")
        val command = ShowCommand(listOf("show"))
        command.execute(currentUser.lists[currentList])
    }

    if (args.isEmpty()) {

        var curInstr : List<String>?

        // process commands
        while (true) {
            print(">> ")
            curInstr = readLine()?.trim()?.split("\\s+".toRegex())
            if (curInstr == null || curInstr[0].trim().lowercase() == "quit"
                || curInstr[0].trim().lowercase() == "q" || curInstr[0].trim().lowercase() == "exit") {
                break
            } else if (taskCommands.contains(curInstr[0])) {
                val taskCommand = CommandFactory.createTaskComFromArgs(curInstr)
                if (currentList == -1) {
                    println("You have no currently active list. Please select a list.")
                } else {
                    try {
                        taskCommand.execute(currentUser.lists[currentList])
                    } catch (e: Exception) {
                        println("An error occurred.")
                    }
                }
            } else if (userCommands.contains(curInstr[0])) {
                val userCommand = CommandFactory.createUserComFromArgs(curInstr)
                try {
                    userCommand.execute()
                } catch (e: Exception) {
                    println("An error occurred.")
                }
            } else {
                val taskListCommand = CommandFactory.createTaskListComFromArgs(curInstr)
                try {
                    taskListCommand.execute(currentUser.lists)
                } catch (e: Exception) {
                    println("An error occurred.")
                }
            }

            currentUser.lastUsedList = currentList
            SaveUtils.saveUserData(currentUser)
        }
    } else {
        val instructions : List<String> = args.toMutableList()
        val length = instructions.size
        var i = 0

        while (true) {
            i = CommandFactory.findFirstCommand(instructions, i, length)
            if (i == length) break
            val curInstr = instructions.slice(i until length)

            if (taskCommands.contains(curInstr[0])) {
                val taskCommand = CommandFactory.createTaskComFromArgs(curInstr)
                if (currentList == -1) {
                    println("You have no currently active list. Please select a list.")
                } else {
                    try {
                        taskCommand.execute(currentUser.lists[currentList])
                    } catch (e: Exception) {
                        println("An error occurred.")
                    }
                }
            } else if (userCommands.contains(curInstr[0])) {
                val userCommand = CommandFactory.createUserComFromArgs(curInstr)
                try {
                    userCommand.execute()
                } catch (e: Exception) {
                    println("An error occurred.")
                }
            } else {
                val taskListCommand = CommandFactory.createTaskListComFromArgs(curInstr)
                try {
                    taskListCommand.execute(currentUser.lists)
                } catch (e: Exception) {
                    println("An error occurred.")
                }
            }
            i++

            currentUser.lastUsedList = currentList
            SaveUtils.saveUserData(currentUser)
        }

    }

    // save to-do list (json)
    currentUser.lastUsedList = currentList
//    runBlocking {
//        delay(2000L)
//        try {
//            CloudUtils.postUsers(currentUser)
//        } catch (_: ConnectException) {
//            val filename = "data.json"
//            SaveUtils.saveUserData(currentUser)
//        }
//    }
}