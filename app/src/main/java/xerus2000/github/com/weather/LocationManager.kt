package xerus2000.github.com.weather

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

val location: MutableLiveData<Location> = MutableLiveData()

class LocationManager(val locationProvider: FusedLocationProviderClient) {
	
	private val locationCallback = object : LocationCallback() {
		override fun onLocationResult(locations: LocationResult) {
			updateLocation(locations.lastLocation, 1)
		}
	}
	
	@SuppressLint("MissingPermission")
	fun requestLastLocation() =
			locationProvider.lastLocation.addOnSuccessListener {
				Log.d("weatherapi", "lastLocation: $it")
				if (it != null)
					updateLocation(it, 0)
			}
	
	@SuppressLint("MissingPermission")
	fun registerLocationListener() =
			locationProvider.requestLocationUpdates(LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER), locationCallback, null)
	
	fun removeLocationListener() =
			locationProvider.removeLocationUpdates(locationCallback)
	
	var recency = 0
	private fun updateLocation(loc: Location, recency: Int) {
		if (this.recency < recency) {
			this.recency = recency
			location.value = loc
		}
	}
}