package com.example.weatherforecast.presentation.views


import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.ActivityMainBinding
import com.example.weatherforecast.utils.language.BaseActivity


class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        setupNav()
        // why can not use binding with navController


//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.homeFragment,
//                R.id.favoriteFragment,
//                R.id.alertsFragment,
//                R.id.settingsFragment
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)

    }

    //    override fun attachBaseContext(newBase: Context?) {
//
//        val sp = PreferenceManager.getDefaultSharedPreferences(newBase)
//        val lang = sp.getString("lang",Locale.getDefault().language)
//        val context = ContextUtils.updateLocale(newBase!!, Locale(lang!!))
//        super.attachBaseContext(newBase)
//    }
    private fun setupNav() {
        val navController = findNavController(R.id.fragment)
        binding.bottomNavView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> showBottomNav()
                R.id.favoriteFragment -> showBottomNav()
                R.id.alertsFragment -> showBottomNav()
                R.id.settingsFragment -> showBottomNav()
                else -> hideBottomNav()
            }
        }
    }

    private fun showBottomNav() {
        binding.bottomNavView.visibility = View.VISIBLE


    }

    private fun hideBottomNav() {
        binding.bottomNavView.visibility = View.GONE

    }
}