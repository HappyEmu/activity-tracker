package ch.unibe.msa.kotlintest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.StrictMode
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import org.jetbrains.anko.*
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.scheduleAtFixedRate

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        find<Button>(R.id.btn_start_service).onClick { startService(intentFor<SensorService>("updateRate" to 1000)) }
        find<Button>(R.id.btn_stop_service).onClick { stopService(intentFor<SensorService>()) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_settings) { return true }

        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }
}
