package com.promisebooks.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.promisebooks.app.auth.AuthActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val splashScreenStarter: Thread = object : Thread() {
            override fun run() {
                val i = 0
                try {
                    var waitingTime = 0
                    while (waitingTime < 1500) {
                        sleep(150)
                        waitingTime += 100
                    }
                    val intent = Intent(this@MainActivity, AuthActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    finish()
                }
            }
        }
        splashScreenStarter.start()
    }
}