package ch.unibe.msa.kotlintest

import android.app.IntentService
import android.content.Intent
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.async
import org.jetbrains.anko.info
import java.util.*

class ActivityRecognitionService : IntentService("activity-rec-service"), AnkoLogger {

    override fun onCreate() {
        super.onCreate()
        println("onCreate")
    }

    override fun onHandleIntent(intent: Intent?) {
        println("onHandleIntent")
        if (ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            val mostProbableActivity = result.mostProbableActivity

            val activityText = mostProbableActivity.getActivityString()
            val confidence = mostProbableActivity.confidence

            info("Activity detected: $activityText ($confidence)")

            // Send activity data to server
            async {
                val sendData = listOf<IJsonable>(ActivityData(Date(), activityText))
                println("Sending data in thread")
                DataSender(Settings.endpoint).send(sendData.map { it.toJson() }.joinToString(",","[","]"))
            }

            // Notify MainActivity about new activity via broadcast
            val bcIntent = Intent("ImActive")
            bcIntent.putExtra("activity", activityText)
            bcIntent.putExtra("confidence", confidence)

            sendBroadcast(bcIntent)
        }
    }

    fun DetectedActivity.getActivityString(): String {
        return when (this.type) {
            DetectedActivity.ON_BICYCLE -> "ON_BICYCLE"
            DetectedActivity.ON_FOOT -> "ON_FOOT"
            DetectedActivity.RUNNING -> "RUNNING"
            DetectedActivity.STILL -> "STILL"
            DetectedActivity.WALKING -> "WALKING"
            DetectedActivity.TILTING -> "TILTING"
            DetectedActivity.IN_VEHICLE -> "IN_VEHICLE"
            DetectedActivity.UNKNOWN -> "UNKNOWN"
            else -> "NOT_RECOGNIZED"
        }
    }
}