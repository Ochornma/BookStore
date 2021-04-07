package com.promisebooks.app.customer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.promisebooks.app.R
import com.promisebooks.app.model.User
import kotlinx.android.synthetic.main.activity_customer.*
import kotlinx.android.synthetic.main.nav_header.*

class CustomerActivity : AppCompatActivity() {
    private lateinit var mAppBarConfiguration: AppBarConfiguration
    private lateinit var drawer: DrawerLayout
    private var user = FirebaseAuth.getInstance().currentUser
    private var db = FirebaseFirestore.getInstance()
    private var collectionUser = db.collection("Users")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)

        drawer = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.inflateMenu(R.menu.merchant_drawer)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_nav_host) as NavHostFragment?
        NavigationUI.setupWithNavController(navigationView,
            navHostFragment!!.navController
        )

        mAppBarConfiguration = AppBarConfiguration.Builder(
            R.id.myBookFragment,
            R.id.marketFragment,
            R.id.deleteFragment)
            .setOpenableLayout(drawer_layout)
            .build()

        val navController = Navigation.findNavController(this, R.id.main_nav_host)
        NavigationUI.setupWithNavController(navigationView, navController)
        val uiid = user?.uid
        if (uiid != null) {
            collectionUser.document(uiid).get().addOnSuccessListener { it1 ->
                val user = it1.toObject<User>(User::class.java)
                if (user != null) {
                    header_subtitle.text = user.name

                }
            }
        }
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





    override fun onStart() {
        super.onStart()


    }

    override fun onStop() {
        super.onStop()


    }
}