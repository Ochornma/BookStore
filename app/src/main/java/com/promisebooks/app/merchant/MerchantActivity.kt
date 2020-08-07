package com.promisebooks.app.merchant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.promisebooks.app.R
import kotlinx.android.synthetic.main.activity_customer.*

class MerchantActivity : AppCompatActivity() {
    private lateinit var mAppBarConfiguration: AppBarConfiguration
    private lateinit var drawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_merchant)

        drawer = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_nav_host) as NavHostFragment?
        NavigationUI.setupWithNavController(navigationView,
            navHostFragment!!.navController
        )

        mAppBarConfiguration = AppBarConfiguration.Builder(
            R.id.transactionFragment,
            R.id.refundFragment,
            R.id.uploadFragment,
            R.id.myBookFragment,
            R.id.marketFragment,
            R.id.deleteFragment)
            .setOpenableLayout(drawer_layout)
            .build()

        val navController = Navigation.findNavController(this, R.id.main_nav_host)
        NavigationUI.setupWithNavController(navigationView, navController)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        }
        super.onBackPressed()
    }



    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.main_nav_host)
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
    }
}