package ch.unibe.msa.kotlintest

import android.content.Context
import android.hardware.SensorEvent
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*

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

fun SensorEvent.toAccelData(): AccelerometerData {
    val date = Date()
    return AccelerometerData(date, this.values[0], this.values[1], this.values[2])
}

fun Date.format(format: String = "yyyy-MM-dd HH:mm:ss"): String {
    val formatter = SimpleDateFormat(format)
    return formatter.format(this)
}
fun Date.toJsonDate(): String = format("yyyy-MM-dd'T'HH:mm:ss.SSS")