package ch.unibe.msa.kotlintest

import android.app.IntentService
import android.content.Intent
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import org.jetbrains.anko.toast

class ActivityRecognitionService : IntentService("activity-rec-service") {

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

            toast("Activity detected: $activityText ($confidence)")

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
            else -> "NOT_RECOGNIZED"
        }
    }
}