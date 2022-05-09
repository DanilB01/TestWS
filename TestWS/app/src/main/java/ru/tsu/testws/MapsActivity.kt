package ru.tsu.testws

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.directions.route.*
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import ru.tsu.testws.databinding.ActivityMapsBinding


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, RoutingListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val endLocation = LatLng(
        56.46953973955253,
        84.94751051442465
    )

    private val permissions = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
    )

    private val permissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        for (permission in permissions) {
            if (permission.value) {
                showLocation(permission.key)
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Разрешения не даны")
                    .setPositiveButton("Ясно") { _, _ -> }
                    .create()
                    .show()
                return@registerForActivityResult
            }
        }
    }

    private fun showLocation(key: String) {
        Toast.makeText(this, "$key granted", Toast.LENGTH_SHORT).show()
    }

    private fun checkPermission(permission: String) =
        ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    fun setCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationRequest = LocationRequest.create().apply {
            interval = 100000
            fastestInterval = 100000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val locationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                mMap.clear()
                mMap.addMarker(
                    MarkerOptions()
                        .position(endLocation)
                        .title("TSU")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
                )
                for (location in p0.locations) {
                    val curLoc = LatLng(location.latitude, location.longitude)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(curLoc)
                            .title("Current location")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
                    )

                    val routing = Routing.Builder()
                        .travelMode(AbstractRouting.TravelMode.WALKING)
                        .withListener(this@MapsActivity)
                        .alternativeRoutes(true)
                        .waypoints(curLoc, endLocation)
                        .key("AIzaSyCab0wW1JAUQs1bXq2F-zv4sJtbIchK8cQ")
                        .build()
                    routing.execute()
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
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

        var ifAllGranted = true
        for (permission in permissions) {
            if (checkPermission(permission)) {
                Toast.makeText(this, "$permission granted", Toast.LENGTH_SHORT).show()
            } else {
                ifAllGranted = false
            }
        }
        if (!ifAllGranted) {
            permissionRequest.launch(permissions)
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(endLocation, 12f))
            setCurrentLocation()
            mMap.isMyLocationEnabled = true
        }
    }

    override fun onRoutingFailure(p0: RouteException?) {
        //TODO("Not yet implemented")
        val a = p0
    }

    override fun onRoutingStart() {
        //TODO("Not yet implemented")
        println()
        print ("start")
        println()
    }

    override fun onRoutingSuccess(p0: ArrayList<Route>?, p1: Int) {
        val line = PolylineOptions()
        line.width(4f).color(R.color.purple_500)
        p0?.get(p1)?.let {
            line.addAll(it.points)
            mMap.addPolyline(line)
        }
    }

    override fun onRoutingCancelled() {
        //TODO("Not yet implemented")
        println()
        print ("cancel")
        println()
    }
}