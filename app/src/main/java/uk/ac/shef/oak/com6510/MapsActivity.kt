package uk.ac.shef.oak.com6510

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest

import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import uk.ac.shef.oak.com6510.databinding.ActivityMapsBinding

import java.text.DateFormat
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val mapView: MapView? = null
    private var mButtonStart: Button? = null
    private var mButtonEnd: Button? = null
    private lateinit var mySensorViewModel: SensorViewModel
    private var IntentLongitude: String?=null
    private var IntentLatitude:String?=null
    private var IntentPressure:String?=null
    private var IntentTemperature:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        val title: String? = intent.getStringExtra("title")
        super.onCreate(savedInstanceState)
        val binding=ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val travelButton: FloatingActionButton = binding.fabGallery2
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        mButtonStart = binding.buttonStart
        mButtonStart!!.setOnClickListener {
            startLocationUpdates()
            mySensorViewModel.startSensing()
            if (mButtonEnd != null) mButtonEnd!!.isEnabled = true
            mButtonStart!!.isEnabled = false
        }
        mButtonStart!!.isEnabled = true
        mButtonEnd = binding.buttonEnd
        mButtonEnd!!.setOnClickListener {
            stopLocationUpdates()
            mySensorViewModel.stopSensing()
            if (mButtonStart != null) mButtonStart!!.isEnabled = true
            mButtonEnd!!.isEnabled = false
        }
        mButtonEnd!!.isEnabled = false


        travelButton.setOnClickListener{
            val intent = Intent(this,ShowActivity::class.java);
            //intent.putExtra("title",Text.text)
            intent.putExtra("title",title)
            //put sensor data into the intent
            //eg. intent.putExtra("longtitude",longtitude)
            intent.putExtra("longitude",IntentLongitude)
            intent.putExtra("latitude",IntentLatitude)
            intent.putExtra("temperature",IntentTemperature)
            intent.putExtra("pressure",IntentPressure)
            startActivity(intent)
        }
        /** Here is the sensors Log test */
        this.mySensorViewModel=ViewModelProvider(this)[SensorViewModel::class.java]
        this.mySensorViewModel!!.retrievePressureData().observe(this,
            {
                newValue->
                    Log.i("MapsActivity","The current Pressure is${newValue}")
                    binding.pressure.text="The current Pressure is${newValue}"
                    IntentPressure=newValue.toString()
            }

        )
        this.mySensorViewModel!!.retrieveTemperatureData().observe(this,
            {
                newValue->
                    Log.i("MapsActivity","The Temperature is ${newValue}")
                    binding.temperature.text="The Temperature is ${newValue}"
                    IntentTemperature=newValue.toString()
            }
        )



    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    ACCESS_FINE_LOCATION
                )

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return
        }
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            null /* Looper */
        )

    }

    /**
     * it stops the location updates
     */
    private fun stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
    }

    override fun onResume() {
        super.onResume()
        mLocationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationUpdates()
    }

    private var mCurrentLocation: Location? = null
    private var mLastUpdateTime: String? = null
    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            mCurrentLocation = locationResult.getLastLocation()
            mLastUpdateTime = DateFormat.getTimeInstance().format(Date())
            Log.i("MAP", "new location " + mCurrentLocation.toString())
            IntentLongitude=mCurrentLocation!!.longitude.toString()
            IntentLatitude=mCurrentLocation!!.latitude.toString()
            if (mMap != null) mMap.addMarker(
                MarkerOptions().position(
                    LatLng(
                        mCurrentLocation!!.latitude,
                        mCurrentLocation!!.longitude
                    )
                ).title("Time: $mLastUpdateTime, Location: ${mCurrentLocation.toString()}")
            )
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        mCurrentLocation!!.latitude,
                        mCurrentLocation!!.longitude
                    ), 14.0f
                )
            )
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    mFusedLocationClient.requestLocationUpdates(
                        mLocationRequest,
                        mLocationCallback, null /* Looper */
                    )
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(53.3814, 1.4816)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sheffield"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14.0f))
    }

    companion object {
        private const val ACCESS_FINE_LOCATION = 321
    }
}