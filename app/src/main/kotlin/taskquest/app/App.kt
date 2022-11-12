/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package taskquest.app

import javafx.application.Application
import javafx.stage.Stage
import taskquest.app.javafx.MainBoardDisplay

class App: Application() {
    override fun start(stage: Stage?) {
        val display = MainBoardDisplay()
        display.start_display(stage)
    }

    override fun stop() {
        println("Stage is closing")
        // Save file
    }
}

fun main() {
    Application.launch(App::class.java)
}
