package ch.unibe.msa.kotlintest

import android.hardware.SensorEvent
import com.github.salomonbrys.kotson.jsonObject
import java.text.SimpleDateFormat
import java.util.*

interface IJsonable {
    fun toJson(): String
}

data class AccelerometerData(val timestamp: Date,
                             val ax: Float, val ay: Float, val az: Float) : IJsonable {
    override fun toJson(): String {
        return jsonObject("timestamp" to timestamp.toJsonDate(), "type" to "acceleration",
                "ax" to ax, "ay" to ay, "az" to az).toString()
    }
}

data class GpsData(val timestamp: Date,
                   val lat: String, val long: String) : IJsonable {
    override fun toJson(): String {
        return jsonObject("timestamp" to timestamp.toJsonDate(), "type" to "location",
                "lat" to lat, "long" to long).toString()
    }
}

data class ActivityData(val timestamp: Date, val text: String) : IJsonable {
    override fun toJson(): String {
        return jsonObject("timestamp" to timestamp.toJsonDate(), "type" to "activity", "text" to text).toString()
    }
}