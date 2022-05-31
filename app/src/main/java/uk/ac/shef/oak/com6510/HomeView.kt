package uk.ac.shef.oak.com6510

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class HomeView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_view)

        val travelButton: Button = findViewById(R.id.travel_button)
        val Text: EditText = findViewById(R.id.edit_text)
        travelButton.setOnClickListener{
            val intent = Intent(this,MapsActivity::class.java);
            intent.putExtra("title",Text.text.toString())
            startActivity(intent)
        }

    }
}