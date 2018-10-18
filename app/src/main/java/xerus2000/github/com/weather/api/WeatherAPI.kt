package xerus2000.github.com.weather.api

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val APPID = "c816b28d04ec4959cfe9a439c34f3bc5"

interface WeatherAPI {
	
	@GET("weather")
	fun cityWeather(@Query("q") city: String): Call<CityWeather>
	
	@GET("weather")
	fun cityWeather(@Query("lat") lat: String, @Query("lon") lon: String): Call<CityWeather>
	
	companion object {
		fun get(): WeatherAPI {
			val retrofit = Retrofit.Builder()
					.addConverterFactory(GsonConverterFactory.create())
					.baseUrl("http://api.openweathermap.org/data/2.5/")
					.client(OkHttpClient().newBuilder().addInterceptor { chain ->
						chain.request().run {
							chain.proceed(newBuilder().url(url().newBuilder().addQueryParameter("appid", APPID).build()).build())
						}
					}.build())
					.build()
			return retrofit.create(WeatherAPI::class.java)
		}
	}
}