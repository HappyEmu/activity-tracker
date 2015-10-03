package ch.unibe.msa.kotlintest

import android.hardware.SensorEvent
import com.github.salomonbrys.kotson.jsonObject
import java.util.*

interface IJsonable {
    fun toJson(): String
}

data class AccelerometerData(val timestamp: Date,
                             val ax: Float, val ay: Float, val az: Float) : IJsonable {
    override fun toJson(): String {
        return jsonObject("timestamp" to timestamp.time, "ax" to ax, "ay" to ay, "az" to az).toString()
    }
}

data class GpsData(val timestamp: Date,
                   val lat: String, val long: String) : IJsonable {
    override fun toJson(): String {
        throw UnsupportedOperationException()
    }
}

fun SensorEvent.toAccelData(): AccelerometerData {
    val date = Date(this.timestamp / 1000000)
    return AccelerometerData(date, this.values[0], this.values[1], this.values[2])
}