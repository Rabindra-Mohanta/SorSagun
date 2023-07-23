package alkusi.mahato.sarsogune.AllActivity

import alkusi.mahato.sarsogune.R
import alkusi.mahato.sarsogune.databinding.ActivityMapsBinding
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.io.IOException
import java.util.*


class MapsActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    lateinit var locationManager: LocationManager;
    val REQ_GPS_ON = 12;
    val REQ_SEARCH_LOCATION = 13;
    var isAddLocation = false;
    var isViewLocation = false;
    var isViewAll = false;
    var lat:Double? = null;
    var long:Double?=null;


    lateinit var fusedLocationProviderClient: FusedLocationProviderClient;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            val intent = intent
        if(intent.hasExtra("isAddLocation"))
        {

            isAddLocation = intent.getBooleanExtra("isAddLocation",false)
        }
        if(intent.hasExtra("isViewAll"))
        {

            isViewAll = intent.getBooleanExtra("isViewAll",false)
        }
        if(intent.hasExtra("isViewLocation"))
        {
            lat = intent.getDoubleExtra("latitude",0.0)
            long = intent.getDoubleExtra("longitude",0.0)
            isViewLocation = intent.getBooleanExtra("isViewLocation",false)
        }
            init()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(alkusi.mahato.sarsogune.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }




private fun init()
{

if(isAddLocation)
{
    binding.btnAdd.visibility = View.VISIBLE;
}
    binding.btnAdd.setOnClickListener(object :View.OnClickListener
    {
        override fun onClick(p0: View?) {

           val lat =  mMap.cameraPosition.target.latitude
            val long = mMap.cameraPosition.target.longitude
            val resultIntent = Intent()
            resultIntent.putExtra("latitude",lat)
            resultIntent.putExtra("longitude",long)
            setResult(RESULT_OK,resultIntent)
            finish();

        }

    })
    binding.iconCurrentLocation.setOnClickListener(object :View.OnClickListener
    {
        override fun onClick(v: View?) {
            if(isGPSProviderEnable())
            {
                getGpsLocation()
            }
            else
            {
                onGps();
            }


        }

    })

    binding.carSearchLocation.setOnClickListener(object :View.OnClickListener
    {
        override fun onClick(v: View?) {
            searchLocation()
        }
    })

}




    private fun searchLocation()
    {

        Places.initialize(applicationContext,resources.getString(alkusi.mahato.sarsogune.R.string.map_key))
        val fields = Arrays.asList(Place.Field.ID,Place.Field.NAME,Place.Field.LAT_LNG);

        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,fields).setCountry("IN").build(this)
        try {
            startActivityForResult(intent,REQ_SEARCH_LOCATION)
        }
        catch (e:Exception)
        {}

    }

    private fun onGps() {


        val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
           interval = 300;
           priority = Priority.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        val task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        task.addOnSuccessListener {
            //if already gps in on on this device
        }
        task.addOnFailureListener {

            val statusCodes = (it as ResolvableApiException).statusCode
            if(statusCodes == LocationSettingsStatusCodes.RESOLUTION_REQUIRED)
            {
                try {
                    it.startResolutionForResult(this@MapsActivity,REQ_GPS_ON);
                }
                catch (e:IOException)
                {}

            }

        }
    }

    private fun isGPSProviderEnable():Boolean
    {
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
    }

    private fun getGpsLocation() {
        val priority = LocationRequest.QUALITY_BALANCED_POWER_ACCURACY;
        val cancellationTokenSource = CancellationTokenSource();
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationProviderClient.getCurrentLocation(priority,cancellationTokenSource.token).addOnSuccessListener {

            if (mMap != null && it != null) {
                var latLong = LatLng(it.latitude, it.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, 15f));

            }
        }
            .addOnFailureListener {

            }


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
         mMap.clear();
     //default jamshedpur lat ong
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(22.8046,86.2029),5f))
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mMap.isMyLocationEnabled = true;
        mMap.uiSettings.isMyLocationButtonEnabled = false

        if(isViewLocation)
        {
            showMarker(LatLng(lat!!,long!!),"")
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat!!,long!!),15f))
        }

          mMap.setOnCameraIdleListener(object:GoogleMap.OnCameraIdleListener
          {
              override fun onCameraIdle() {
                if(mMap!=null && mMap.myLocation!=null)
                {

                    val cameraPosition = mMap.cameraPosition;
                    val target = cameraPosition.target;
                    val lat = target.latitude;
                    val long = target.longitude;
                      var addr = ""
                    val geocoder = Geocoder(this@MapsActivity);
                    try {
                        val arrAddress = geocoder.getFromLocation(lat,long,1);
                        addr = arrAddress!!.get(0).getAddressLine(0)
                    }
                    catch (e:Exception)
                    {

                    }
                    binding.textSearchLocation.setText(addr)
                }
              }

          })



          if(isViewAll)
          {


              mMap.setOnMarkerClickListener(object :GoogleMap.OnMarkerClickListener
              {
                  override fun onMarkerClick(marker: Marker): Boolean {
                      val intent = Intent(this@MapsActivity,ActivityNotificationClick::class.java)
                      intent.putExtra("email",marker.title)
                      startActivity(intent)

                      return true
                  }

              })
              getAllUser()
          }

        if(!isViewLocation && !isViewAll)
        {
            if(isGPSProviderEnable())
            {
                getGpsLocation()
            }
            else
            {
                onGps();
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode)
        {
            REQ_GPS_ON ->
            {
                 getGpsLocation()
            }
            REQ_SEARCH_LOCATION->
            {
                if(resultCode== RESULT_OK)
                {
                    var places = Autocomplete.getPlaceFromIntent(data)
                    if(places==null)
                    {
                        return
                    }
                    var lat = places.latLng.latitude
                    var long = places.latLng.longitude
                    if(mMap!=null)
                    {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(places.latLng,15f))
                        var address = ""
                        val geocoder = Geocoder(this);
                        try {
                            val addressArr = geocoder.getFromLocation(lat,long,1)
                            address = addressArr!!.get(0).getAddressLine(0);
                        }
                        catch (e:Exception)
                        {

                        }
                        binding.textSearchLocation.setText(address)
                    }
                }

            }
        }
    }
    private fun showMarker(latLong:LatLng,title:String)
    {

        mMap.addMarker(MarkerOptions().position(latLong).title(title))
    }
    private fun getAllUser()
    {
        binding.progressbar.visibility = View.VISIBLE
        val db = FirebaseFirestore.getInstance();
        db.collection(resources.getString(alkusi.mahato.sarsogune.R.string.fir_locations)).get().addOnCompleteListener(object :OnCompleteListener<QuerySnapshot>
        {
            override fun onComplete(task: Task<QuerySnapshot>) {
                val result = task.result;
                for(i in result.documents.indices)
                {
                    val item = result.documents.get(i)
                    if(item.getDouble(resources.getString(R.string.fir_latitude)) !=null)
                    {
                        lat = item.getDouble(resources.getString(R.string.fir_latitude))
                    }

                    if(item.getDouble(resources.getString(R.string.fir_longitude))!=null)
                    {
                        long = item.getDouble(resources.getString(R.string.fir_longitude))
                    }
                    if(lat!=null && long!=null)
                    {
                        showMarker(LatLng(lat!!,long!!),item.id)
                    }
                }
                binding.progressbar.visibility = View.GONE
            }

        })
            .addOnFailureListener(object :OnFailureListener
            {
                override fun onFailure(p0: java.lang.Exception) {
                    binding.progressbar.visibility = View.GONE;
                    Toast.makeText(this@MapsActivity,resources.getString(alkusi.mahato.sarsogune.R.string.msg_something_went),Toast.LENGTH_SHORT).show()
                }

            })
    }
}