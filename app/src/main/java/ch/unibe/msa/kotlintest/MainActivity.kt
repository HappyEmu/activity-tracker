package ch.unibe.msa.kotlintest

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.github.salomonbrys.kotson.jsonObject
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.*
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import org.jetbrains.anko.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : Activity(), ConnectionCallbacks, OnConnectionFailedListener, AnkoLogger, LocationListener {

    fun handleUpdate(loc: Location?) {
        find<TextView>(R.id.txt_history).append("\n${Date().format("HH:mm:ss")}: Loc: ${loc?.latitude}, ${loc?.longitude}, Activity: ${Settings.currentActivity}")
        async {
            val sendData = listOf<IJsonable>(ActivityData(Date(), Settings.currentActivity))
            println("Sending data in thread")
            var sendText = GpsData(Date(),loc,Settings.currentActivity)
            DataSender(Settings.endpoint).send(sendText.toJson());
        }
    }

    override fun onLocationChanged(loc: Location?) {
        handleUpdate(loc)
    }

    var txtHistory: TextView? = null
    var gApiClient: GoogleApiClient? = null
    var receiver: BroadcastReceiver? = null
    var locRequest: LocationRequest? = null
    var tracking: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Settings.load(ctx)

        txtHistory = find<TextView>(R.id.txt_history)

        val txtEndPoint = find<EditText>(R.id.txt_url)
        txtEndPoint.setText(Settings.endpoint)

        // Set up UI
        find<Button>(R.id.btn_toggle).onClick {
            if(tracking){
                find<TextView>(R.id.txt_history).append("\nTracking stopped")
                tracking = false
                find<Button>(R.id.btn_toggle).text = "Start Tracking"

                //Stop GPS updates
                LocationServices.FusedLocationApi.removeLocationUpdates(gApiClient, this)

                //Stop Activity tracking
                val intent = intentFor<ActivityRecognitionService>()
                val callbackIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(gApiClient,callbackIntent)
                unregisterReceiver(receiver)
            }
            else {
                find<TextView>(R.id.txt_history).append("\nTracking started")
                tracking = true
                find<Button>(R.id.btn_toggle).text = "Stop Tracking"

                //Initialize activity tracking
                val intent = intentFor<ActivityRecognitionService>()
                val callbackIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(gApiClient, 0, callbackIntent)
                toast("Waiting for recognition")
                val filter = IntentFilter()
                filter.addAction("ImActive")
                registerReceiver(receiver, filter)

                //Initialize GPS Updates
                val endPointAddress = txtEndPoint.text.toString()
                Settings.endpoint = endPointAddress
                Settings.save(ctx)
                LocationServices.FusedLocationApi.requestLocationUpdates(gApiClient, locRequest, this)
            }

        }



        // Connect to play services
        if (isPlayServiceAvailable()) {
            gApiClient = GoogleApiClient.Builder(this)
                    .addApi(ActivityRecognition.API)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build()

            //Connect to Google API
            gApiClient?.connect()

            locRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                    .setFastestInterval(1 * 1000) // 1 second, in milliseconds
        }

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val activity = intent?.getStringExtra("activity")
                val confidence = intent?.getIntExtra("confidence", -1)

                find<TextView>(R.id.txt_history).append("\n${Date().format("HH:mm:ss")}: $activity @ $confidence%")
            }
        }



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


        val location = LocationServices.FusedLocationApi.getLastLocation(gApiClient)
        /*if (location != null) {
            handleUpdate(location)
        }*/


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

        info("receiver unregistered")
    }

    private fun isPlayServiceAvailable(): Boolean {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS
    }
}
