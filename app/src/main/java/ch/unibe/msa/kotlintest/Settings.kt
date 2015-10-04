package ch.unibe.msa.kotlintest

import android.content.Context

data class Settings(val username: String, val password: String, val endpoint: String) {

    fun save(context: Context) {
        val content = "$username;$password;$endpoint"
        context.writeToFile("settings", content)
    }

    companion object {
        fun retrieve(context: Context) : Settings? {
            val text = context.readFile("settings") ?: return null
            val parts = text.split(';')

            return Settings(parts[0], parts[1], parts[2])
        }
    }
}