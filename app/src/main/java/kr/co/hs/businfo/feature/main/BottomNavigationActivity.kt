package kr.co.hs.businfo.feature.main

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kr.co.hs.businfo.R

class BottomNavigationActivity : AppCompatActivity() {
    private lateinit var favoriteFragment: Fragment
    private lateinit var MapFragment: Fragment
    private lateinit var DirectionFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        favoriteFragment = FavoriteFragment()
        MapFragment = MainActivity()
        DirectionFragment = DirectionFragment()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, favoriteFragment)
            commit()
        }

        val navigationFavorite = findViewById<ImageButton>(R.id.favorite)
        val navigationMap = findViewById<ImageButton>(R.id.map)
        val navigationDirection = findViewById<ImageButton>(R.id.direction)

        navigationFavorite.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, favoriteFragment)
                commit()
            }
        }

        navigationMap.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, MapFragment)
                commit()
            }
        }

        navigationDirection.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, DirectionFragment)
                commit()
            }
        }
    }
}