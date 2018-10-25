package xerus2000.github.com.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main
import retrofit2.Call
import xerus2000.github.com.weather.api.CityWeather
import xerus2000.github.com.weather.api.WeatherAPI
import java.io.PrintWriter
import java.io.StringWriter

val taskDispatcher = AsyncTask.THREAD_POOL_EXECUTOR.asCoroutineDispatcher()
val API = WeatherAPI.get()

const val REQUEST_LOCATION_CODE = 1

class WeatherSearchActivity : AppCompatActivity() {
	
	private lateinit var fusedLocationClient: FusedLocationProviderClient
	private val locationCallback = object : LocationCallback() {
		override fun onLocationResult(locations: LocationResult) {
			updateLocation(locations.lastLocation, 1)
		}
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
		requestLastLocation()
		
		configureSearchBar(searchBar)
	}
	
	override fun onResume() {
		super.onResume()
		registerLocationListener()
	}
	
	override fun onPause() {
		super.onPause()
		stopLocationListener()
	}
	
	private fun hasPermission(permission: String) =
			ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
	
	private fun requestPermissions(code: Int, vararg permissions: String) {
		ActivityCompat.requestPermissions(this, permissions, code)
	}
	
	@SuppressLint("SetTextI18n")
	private fun checkLocationPermission() {
		if (!hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
			ActivityCompat.requestPermissions(this,
					arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
					REQUEST_LOCATION_CODE)
		}
	}
	
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (resultCode == PackageManager.PERMISSION_GRANTED)
			when (requestCode) {
				REQUEST_LOCATION_CODE -> {
					requestLastLocation()
					registerLocationListener()
				}
			}
	}
	
	@SuppressLint("MissingPermission")
	private fun requestLastLocation() =
			fusedLocationClient.lastLocation.addOnSuccessListener {
				if (it != null)
					updateLocation(it, 0)
			}
	
	@SuppressLint("MissingPermission")
	private fun registerLocationListener() =
			fusedLocationClient.requestLocationUpdates(LocationRequest.create().setPriority(LocationRequest.PRIORITY_NO_POWER), locationCallback, null)
	
	
	private fun stopLocationListener() {
		Log.d("weatherapi", "stopLocationListener")
		fusedLocationClient.removeLocationUpdates(locationCallback)
	}
	
	private fun configureSearchBar(searchBar: EditText) {
		searchBar.requestFocusFromTouch()
		searchBar.setOnEditorActionListener { _, actionId, keyEvent: KeyEvent? ->
			if (actionId == EditorInfo.IME_ACTION_SEARCH || keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER) {
				val search = searchBar.text.toString()
				showCityWeather(API.cityWeather(search))
				return@setOnEditorActionListener true
			}
			false
		}
	}
	
	var timer = 0L
	var recency = 0
	private fun updateLocation(location: Location, recency: Int) {
		Log.d("weatherapi", "location: " + location.toString())
		showCityWeather(API.cityWeather(location.latitude, location.longitude))
		if (timer + 10_000 < System.currentTimeMillis() || this.recency < recency) {
			this.recency = recency
			timer = System.currentTimeMillis()
			showCityWeather(API.cityWeather(location.latitude, location.longitude))
		}
	}
	
	private fun showCityWeather(weatherCall: Call<CityWeather>) {
		Log.d("weatherapi", "requesting " + weatherCall.request().url())
		GlobalScope.launch(taskDispatcher) {
			showText(try {
				weatherCall.execute().body()?.run {
					name + ": " + weather.first().description
				} ?: "City not found!"
			} catch (t: Throwable) {
				Log.d("weatherapi", StringWriter().let {
					t.printStackTrace(PrintWriter(it, true))
					it.buffer.toString()
				})
				t.toString()
			})
		}
	}
	
	private suspend fun showText(text: String?) = withContext(Dispatchers.Main) {
	
	}
	
}
