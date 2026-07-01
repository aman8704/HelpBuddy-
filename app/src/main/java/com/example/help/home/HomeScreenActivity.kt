package com.example.help.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.help.R
import com.example.help.databinding.ActivityHomeScreenBinding
import com.example.help.home.navigation_drawer.components.EditProfileActivity
import com.example.help.home.notification.NotificationActivity
import com.example.help.log_reg.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import androidx.fragment.app.Fragment
import com.example.help.home.fragments.HomeFragment
import com.example.help.home.fragments.NeedHelpFragment
import com.example.help.home.fragments.SettingFragment
import com.example.help.home.navigation_drawer.components.AboutUsActivity
import androidx.activity.OnBackPressedCallback

class HomeScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeScreenBinding
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firestore = FirebaseFirestore.getInstance()
        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open,
            R.string.close
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.drawerArrowDrawable.color = getColor(android.R.color.white)

        loadDrawerData()

        binding.navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.edit_profile-> {
                    startActivity(Intent(this, EditProfileActivity::class.java))
                }

                R.id.logout -> {
                    AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("✓ Yes") { dialog, _ ->
                            FirebaseAuth.getInstance().signOut()

                            startActivity(Intent(this, HomeActivity::class.java))
                            finishAffinity()
                            dialog.dismiss()
                        }
                        .setNegativeButton("✗ No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setCancelable(false)
                        .show()
                }

                R.id.about_me-> {
                    startActivity(Intent(this, AboutUsActivity::class.java))
                }
            }
            binding.drawerLayout.closeDrawers()
            true
        }

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
            binding.bottomNavigation.selectedItemId = R.id.home
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.help_icon -> {
                    replaceFragment(NeedHelpFragment())
                    true
                }
                R.id.settings -> {
                    replaceFragment(SettingFragment())
                    true
                }
                else -> false
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.container)

                if (currentFragment !is HomeFragment) {

                    replaceFragment(HomeFragment())
                    binding.bottomNavigation.selectedItemId = R.id.home

                } else {

                    AlertDialog.Builder(this@HomeScreenActivity)
                        .setTitle("Exit")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("✔ Yes") { _, _ ->
                            finish()
                        }
                        .setNegativeButton("X No", null)
                        .show()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        loadDrawerData()
    }

    private fun loadDrawerData() {

        val headerView = binding.navigationView.getHeaderView(0)

        val tvName = headerView.findViewById<TextView>(R.id.userName)
        val tvBranch = headerView.findViewById<TextView>(R.id.branch)
        val tvMobile = headerView.findViewById<TextView>(R.id.mobile)
        val tvEmail = headerView.findViewById<TextView>(R.id.email)

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        firestore.collection("Users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    tvName.text = document.getString("name") ?: ""
                    tvBranch.text = document.getString("branch") ?: ""
                    tvMobile.text = document.getString("mobile") ?: ""
                    tvEmail.text = document.getString("email") ?: ""
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.notification -> {
                startActivity(Intent(this, NotificationActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
