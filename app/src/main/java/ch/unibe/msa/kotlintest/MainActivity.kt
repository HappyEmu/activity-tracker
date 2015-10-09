package ch.unibe.msa.kotlintest

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.*
import com.google.android.gms.location.ActivityRecognition
import org.jetbrains.anko.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : Activity(), ConnectionCallbacks, OnConnectionFailedListener, AnkoLogger {
    var txtHistory: TextView? = null
    var gApiClient: GoogleApiClient? = null
    var receiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Settings.load(ctx)

        txtHistory = find<TextView>(R.id.txt_history)

        val txtEndPoint = find<EditText>(R.id.txt_url)
        txtEndPoint.setText(Settings.endpoint)

        // Set up UI
        find<Button>(R.id.btn_start).onClick {
            val endPointAddress = txtEndPoint.text.toString()
            Settings.endpoint = endPointAddress
            Settings.save(ctx)
            startService(intentFor<SensorService>())
        }

        find<Button>(R.id.btn_stop).onClick {
            stopService(intentFor<SensorService>())
        }

        // Connect to play services
        if (isPlayServiceAvailable()) {
            gApiClient = GoogleApiClient.Builder(this)
                    .addApi(ActivityRecognition.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build()

            //Connect to Google API
            gApiClient?.connect()
        }

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val activity = intent?.getStringExtra("activity")
                val confidence = intent?.getIntExtra("confidence", -1)

                find<TextView>(R.id.txt_history).append("\n${Date().format("HH:mm:ss")}: $activity @ $confidence%")
            }
        }

        val filter = IntentFilter()
        filter.addAction("ImActive")
        registerReceiver(receiver, filter)
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

    override fun onConnected(bundle: Bundle?) {
        val intent = intentFor<ActivityRecognitionService>()
        val callbackIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(gApiClient, 0, callbackIntent)
        toast("Waiting for recognition")
    }

    override fun onConnectionSuspended(cause: Int) {
        info("Play Service connection was suspended. Cause: $cause")
    }

    override fun onConnectionFailed(result: ConnectionResult?) {
        info("Not connected to ActivityRecognition")
    }

    override fun onDestroy() {
        info("onDestroy")
        super.onDestroy()
        gApiClient?.disconnect()
        unregisterReceiver(receiver)
        info("receiver unregistered")
    }

    private fun isPlayServiceAvailable(): Boolean {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS
    }
}
