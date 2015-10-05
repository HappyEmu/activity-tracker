package ch.unibe.msa.kotlintest

import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.IBinder
import org.jetbrains.anko.sensorManager
import org.jetbrains.anko.toast

class SensorService : Service() {

    var accelerometer: Sensor? = null
    val batchSender = BatchSender()
    val listener = EnqueuingSensorListener(batchSender.queue)

    var running = false

    override fun onCreate() {
        super.onCreate()
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        toast("Endpoint: ${Settings.endpoint}")

        if (running) {
            toast("Service is already running")
        } else {
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            batchSender.endpoint = Settings.endpoint
            batchSender.start()

            running = true
            toast("Service Started")
        }

        return Service.START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        running = false

        toast("Service Stopped")

        sensorManager.unregisterListener(listener)
        batchSender.stop()
    }
}
