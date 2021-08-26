package com.ang.kotlinang.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.ang.kotlinang.R
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.nav_header.view.*
import maes.tech.intentanim.CustomIntent


class MainActivity : AppCompatActivity() {
    lateinit var drawer: DrawerLayout
    private val db = FirebaseFirestore.getInstance()
    val fAuth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Home"

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawer, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        updateNav()
        onMenuClick()
        LoadImage()
        createNotificationChannels()

    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun updateNav() {
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val hView = navigationView.getHeaderView(0)
        db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
            .get()
            .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful && task.result != null) {
                    hView.name.text = task.result!!.getString("Name")
                    hView.email.text = task.result!!.getString("Email")
                }
            }
    }

    fun onMenuClick() {
        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.logout -> {
                    fAuth.signOut()
                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                }
                R.id.profile -> {
                    startActivity(Intent(applicationContext, UserActivity::class.java))
                    CustomIntent.customType(this, "fadein-to-fadeout")
                }

                R.id.vocabulary -> {
                    startActivity(Intent(applicationContext, VocabularyListActivity::class.java))
                }
            }
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }
    }

    fun LoadImage() {
        val userId: String = fAuth.currentUser!!.uid
        db.collection("users").document(userId).get().addOnSuccessListener {
            if (it.exists()) {
                if (!it.getString("ProfileImg").equals("")) {
                    Picasso.get().load(it.getString("ProfileImg")).into(profile)
                }
            }
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("Mychannel", "Reminder", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Channel for Reminder"
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(channel)
        }
    }

//    override fun onStop() {
//        var intent = Intent(this, NotificationReceiver::class.java)
//        var pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
//        var alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
//        val currtime = System.currentTimeMillis()
//        val timeforNotfication = 1000*10
//        alarmManager.set(AlarmManager.RTC_WAKEUP, currtime + timeforNotfication, pendingIntent)
//        super.onStop()
//    }
}
