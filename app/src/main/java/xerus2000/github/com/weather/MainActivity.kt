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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main
import xerus2000.github.com.weather.api.WeatherAPI
import java.io.PrintWriter
import java.io.StringWriter

val taskDispatcher = AsyncTask.THREAD_POOL_EXECUTOR.asCoroutineDispatcher()
val API = WeatherAPI.get()

class MainActivity : AppCompatActivity() {
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		
		configureSearchBar(searchBar)
	}
	
	private fun configureSearchBar(searchBar: EditText) {
		searchBar.requestFocusFromTouch()
		searchBar.setOnEditorActionListener { _, actionId, keyEvent: KeyEvent? ->
			if (actionId == EditorInfo.IME_ACTION_SEARCH || keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER) {
				val search = searchBar.text.toString()
				Log.d("searchText", search)
				GlobalScope.launch(taskDispatcher) {
					val text = try {
						API.cityWeather(search).execute().body()?.run {
							name + ": " + weather.first().description
						}
					} catch (t: Throwable) {
						Log.d("weatherapi", StringWriter().let {
							t.printStackTrace(PrintWriter(it, true))
							it.buffer.toString()
						})
						t.toString()
					}
					withContext(Dispatchers.Main) {
						textView.text = text
					}
				}
				return@setOnEditorActionListener true
			}
			false
		}
	}
	
	
}
