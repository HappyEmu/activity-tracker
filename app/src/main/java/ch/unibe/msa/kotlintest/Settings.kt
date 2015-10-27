package ch.unibe.msa.kotlintest

import android.content.Context

object Settings {
    var endpoint: String = "localhost"
    var username: String = "defaultUser"
    var password: String = "defaultPass"
    var currentActivity: String = "UNKNOWN"

    fun save(context: Context) {
        val content = "$username;$password;$endpoint"
        context.writeToFile("settings", content)
    }

    fun load(context: Context) {
        val text = context.readFile("settings") ?: return
        val parts = text.split(';')

        username = parts[0]
        password = parts[1]
        endpoint = parts[2]
    }
}

