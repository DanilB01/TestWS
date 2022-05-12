package ru.tsu.testws.bottomnav

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.tsu.testws.R
import ru.tsu.testws.databinding.ActivityBottomNavBinding

class BottomNavActivity : AppCompatActivity(R.layout.activity_bottom_nav) {
    private val binding: ActivityBottomNavBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.firstItem -> openFragment(FirstFragment())
                R.id.secondItem -> openFragment(SecondFragment())
                R.id.thirdItem -> openFragment(ThirdFragment())
            }
            true
        }
        binding.bottomNav.selectedItemId = R.id.firstItem
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment, fragment.tag).commit()
    }
}