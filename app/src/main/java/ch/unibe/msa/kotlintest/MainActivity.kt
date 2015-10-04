package ch.unibe.msa.kotlintest

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.ActivityRecognition
import org.jetbrains.anko.*

class MainActivity : Activity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    var lblStatus: TextView? = null
    var gApiClient: GoogleApiClient? = null
    var receiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val settings = Settings.retrieve(ctx)

        lblStatus = find<TextView>(R.id.lbl_status)
        lblStatus?.text = "false"
        val txtEndPoint = find<EditText>(R.id.txt_api_endpoint)
        txtEndPoint.setText(settings?.endpoint ?: "")

        // Set up UI
        find<Button>(R.id.btn_start_service).onClick {
            val endPointAddress = txtEndPoint.text.toString()
            Settings("","", endPointAddress).save(ctx)
            //startService(intentFor<SensorService>("endpoint" to endPointAddress))
        }
        find<Button>(R.id.btn_stop_service).onClick { stopService(intentFor<SensorService>()) }

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

                find<TextView>(R.id.lbl_status).text = "New activity: $activity with $confidence"
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
        val pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(gApiClient, 0, pendingIntent)
        toast("Waiting for recognition")
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.d("BLABLA", "Suspended to ActivityRecognition")
    }

    override fun onConnectionFailed(result: ConnectionResult?) {
        Log.d("BLABLA", "Not connected to ActivityRecognition")
    }

    override fun onDestroy() {
        super.onDestroy()
        gApiClient?.disconnect()
        unregisterReceiver(receiver)
    }

    private fun isPlayServiceAvailable(): Boolean {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS
    }
}
