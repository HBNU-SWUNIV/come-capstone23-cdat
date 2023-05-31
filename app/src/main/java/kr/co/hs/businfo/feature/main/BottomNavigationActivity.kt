package kr.co.hs.businfo.feature.main

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.MapFragment
import kr.co.hs.businfo.R

class BottomNavigationActivity : AppCompatActivity() {
    private lateinit var favoriteFragment: Fragment
    private lateinit var directionFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        favoriteFragment = FavoriteFragment()
        directionFragment = DirectionFragment()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, favoriteFragment)
            commit()
        }

        val navigationFavorite = findViewById<ImageButton>(R.id.favorite)
        val navigationDirection = findViewById<ImageButton>(R.id.direction)

        navigationFavorite.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, favoriteFragment)
                commit()
            }
        }

        navigationDirection.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, directionFragment)
                commit()
            }
        }
    }
}
