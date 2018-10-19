package xerus2000.github.com.weather

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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

class MainActivity : AppCompatActivity() {
	private lateinit var fusedLocationClient: FusedLocationProviderClient
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
		
		configureSearchBar(searchBar)
		configureLocationListener()
	}
	
	private fun configureLocationListener() {
		// Acquire a reference to the system Location Manager
		val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
		
		var timer = 0L
		
		// Define a listener that responds to location updates
		val locationListener = object : LocationListener {
			
			override fun onLocationChanged(location: Location) {
				Log.d("weatherapi", "location: " + location.toString())
				if(timer + 10000 < System.currentTimeMillis()) {
					timer = System.currentTimeMillis()
					showCityWeather(API.cityWeather(location.latitude, location.longitude))
				}
			}
			
			override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
			}
			
			override fun onProviderEnabled(provider: String) {
			}
			
			override fun onProviderDisabled(provider: String) {
			}
		}
		
		// Register the listener with the Location Manager to receive location updates
		Log.d("weatherapi", "requestLocationUpdates")
		try {
			fusedLocationClient.lastLocation.addOnSuccessListener { showCityWeather(API.cityWeather(it.latitude, it.longitude)) }
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)
		} catch(e: SecurityException) {
			textView.text = "I need the Location permission!"
		}
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
	
	private fun showCityWeather(weatherCall: Call<CityWeather>) {
		Log.d("weatherapi", "requesting " + weatherCall.request().url())
		GlobalScope.launch(taskDispatcher) {
			showText(try {
				weatherCall.execute().body()?.run {
					name + ": " + weather.first().description
				}
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
		textView.text = text
	}
	
}
