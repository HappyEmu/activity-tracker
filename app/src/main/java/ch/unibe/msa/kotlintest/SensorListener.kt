package ch.unibe.msa.kotlintest

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import java.util.concurrent.BlockingQueue

class EnqueuingSensorListener(val queue: BlockingQueue<IJsonable>) : SensorEventListener {

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        println("Accuracy Changed")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        queue += event?.toAccelData()
    }
}