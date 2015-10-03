package ch.unibe.msa.kotlintest

import android.content.Context
import java.io.FileInputStream
import java.io.FileNotFoundException

fun Context.readFile(fileName: String): String? {
    val inStream: FileInputStream

    try { inStream = this.openFileInput(fileName) }
    catch(e: FileNotFoundException) { return null }

    return inStream.bufferedReader().readText()
}

fun Context.writeToFile(fileName: String, data: String): Unit {
    val outStream = this.openFileOutput(fileName, Context.MODE_PRIVATE)
    outStream.write(data.toByteArray())
    outStream.close()
}