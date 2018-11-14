package org.adams.opencms.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class CustomTask extends DefaultTask {

    @TaskAction
    void executeTask() {
        println('My custom task execution')
    }
}
