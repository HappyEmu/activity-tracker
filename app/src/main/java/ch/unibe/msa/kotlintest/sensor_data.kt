package ch.unibe.msa.kotlintest

import android.hardware.SensorEvent
import android.location.Location
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
                   val loc: Location?, val activity: String) : IJsonable {
    override fun toJson(): String {
        return jsonObject("timestamp" to timestamp.toJsonDate(), "type" to "location",
                "lat" to loc?.latitude, "long" to loc?.longitude, "activity" to activity).toString()
    }
}


data class ActivityData(val timestamp: Date, val text: String) : IJsonable {
    override fun toJson(): String {
        return jsonObject("timestamp" to timestamp.toJsonDate(), "type" to "activity", "text" to text).toString()
    }
}