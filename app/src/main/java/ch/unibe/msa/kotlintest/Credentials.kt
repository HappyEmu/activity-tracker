package ch.unibe.msa.kotlintest

import android.content.Context

data class Credentials(val username: String, val password: String) {

    fun save(context: Context) {
        val content = "$username;$password"
        context.writeToFile("credentials", content)
    }

    companion object {
        fun retrieve(context: Context) : Credentials? {
            val text = context.readFile("credentials") ?: return null
            val parts = text.split(';')

            return Credentials(parts[0], parts[1])
        }
    }
}