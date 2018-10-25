package xerus2000.github.com.weather

import android.arch.lifecycle.MutableLiveData
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import retrofit2.Call
import xerus2000.github.com.weather.api.CityWeather
import java.io.PrintWriter
import java.io.StringWriter

class WeatherViewModel(activity: FragmentActivity) {
	private val locationManager = LocationManager(LocationServices.getFusedLocationProviderClient(activity))
	var showingCurrentLocationWeather = true
	
	val cityWeather = MutableLiveData<CityWeather>()
	val error = MutableLiveData<String>()
	
	init {
		location.observe({ activity.lifecycle }, { location ->
			Log.d("weatherapi", "location: $location")
			if (showingCurrentLocationWeather)
				showCityWeather(API.cityWeather(location!!.latitude, location.longitude))
		})
	}
	
	private fun showCityWeather(weatherCall: Call<CityWeather>) {
		Log.d("weatherapi", "requesting " + weatherCall.request().url())
		GlobalScope.launch(taskDispatcher) {
			try {
				weatherCall.execute().body()?.let {
					error.postValue(null)
					cityWeather.postValue(it)
				} ?: error.postValue("City not found!")
			} catch (t: Throwable) {
				Log.d("weatherapi", StringWriter().let {
					t.printStackTrace(PrintWriter(it, true))
					it.buffer.toString()
				})
				error.postValue(t.toString())
			}
		}
	}
	
	fun requestCityWeather(query: String) = showCityWeather(API.cityWeather(query))
	
	fun requestLocation() {
		Log.d("weatherapi", "requestLocation")
		if (cityWeather.value == null)
			locationManager.requestLastLocation()
		locationManager.registerLocationListener()
	}
	
	fun removeLocationListener() = locationManager.removeLocationListener()
}
