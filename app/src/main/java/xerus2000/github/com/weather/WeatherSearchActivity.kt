package xerus2000.github.com.weather

import android.Manifest
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.asCoroutineDispatcher
import xerus2000.github.com.weather.api.CityWeather
import xerus2000.github.com.weather.api.WeatherAPI

val taskDispatcher = AsyncTask.THREAD_POOL_EXECUTOR.asCoroutineDispatcher()
val API = WeatherAPI.get()

const val REQUEST_LOCATION_CODE = 1

class WeatherSearchActivity : AppCompatActivity() {
	private lateinit var weatherViewModel: WeatherViewModel
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		weatherViewModel = WeatherViewModel(this)
		
		configureSearchBar(searchBar)
		
		if (hasLocationPermission()) {
			location_wrapper.visibility = View.GONE
			weatherViewModel.requestLocation()
		}
		
		val weathersAdapater = WeathersAdapter()
		weathers.layoutManager = LinearLayoutManager(this)
		weathers.adapter = weathersAdapater
		weatherViewModel.cityWeather.observe({ lifecycle }, {
			if (it != null)
				weathersAdapater.addItem(it)
		})
		weatherViewModel.error.observe({ lifecycle }, {
			text_errors.text = it
		})
	}
	
	private fun configureSearchBar(searchBar: EditText) {
		searchBar.requestFocusFromTouch()
		searchBar.setOnEditorActionListener { _, actionId, keyEvent: KeyEvent? ->
			if (actionId == EditorInfo.IME_ACTION_SEARCH || keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER) {
				val search = searchBar.text.toString()
				weatherViewModel.requestCityWeather(search)
				return@setOnEditorActionListener true
			}
			false
		}
	}
	
	override fun onResume() {
		super.onResume()
		if (hasLocationPermission())
			weatherViewModel.requestLocation()
	}
	
	override fun onPause() {
		super.onPause()
		weatherViewModel.removeLocationListener()
	}
	
	private fun hasLocationPermission() = hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
	
	private fun hasPermission(permission: String) =
			ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
	
	private fun requestPermissions(code: Int, vararg permissions: String) =
			ActivityCompat.requestPermissions(this, permissions, code)
	
	fun requestLocationPermission(view: View) {
		if (!hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION))
			requestPermissions(REQUEST_LOCATION_CODE, Manifest.permission.ACCESS_COARSE_LOCATION)
	}
	
	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, results: IntArray) {
		when (requestCode) {
			REQUEST_LOCATION_CODE -> {
				if (results[0] == PackageManager.PERMISSION_GRANTED) {
					location_wrapper.visibility = View.GONE
					weatherViewModel.requestLocation()
				}
			}
		}
	}

}

class WeathersAdapter : RecyclerView.Adapter<CityWeatherHolder>() {
	private val data = ArrayList<CityWeather>()
	override fun getItemCount() = data.size
	
	fun addItem(item: CityWeather) {
		data.add(item)
		notifyItemInserted(0)
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityWeatherHolder {
		val textView: TextView = parent.inflate(R.layout.weather_city)
		return CityWeatherHolder(textView)
	}
	
	override fun onBindViewHolder(holder: CityWeatherHolder, position: Int) {
		val text = data[data.lastIndex - position].run { name + ": " + weather.first().description }
		holder.view.text = text
	}
}

class CityWeatherHolder(val view: TextView) : RecyclerView.ViewHolder(view)

fun <T : View> ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false) =
		LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot) as T