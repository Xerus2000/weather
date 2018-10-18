package xerus2000.github.com.weather.api

data class CityWeather(var cod: Int, var id: Int, var name: String, var weather: WeatherList)

class WeatherList : ArrayList<Weather>()

data class Weather(var id: Int, var main: String, var description: String, var icon: String)