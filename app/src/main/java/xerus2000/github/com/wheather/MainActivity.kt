package xerus2000.github.com.wheather

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		
		configureSearchBar(searchBar)
		configureWeatherList()
	}
	
	private fun configureSearchBar(searchBar: EditText) {
		searchBar.requestFocusFromTouch()
		searchBar.setOnEditorActionListener { _, actionId, keyEvent: KeyEvent? ->
			if(actionId == EditorInfo.IME_ACTION_SEARCH || keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER) {
				Log.d("searchText", searchBar.text.toString())
				textView.text = searchBar.text.toString()
				return@setOnEditorActionListener true
			}
			false
		}
	}
	
	private fun configureWeatherList() {
	
	}
	
}
