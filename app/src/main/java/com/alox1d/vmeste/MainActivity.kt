package com.alox1d.vmeste

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.ui.AppBarConfiguration
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.alox1d.vmeste.databinding.ActivityMainBinding
import com.alox1d.vmeste.ui.friends.FriendsFragment
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import com.vk.api.sdk.exceptions.VKAuthException

class MainActivity : AppCompatActivity() {

    private val TAG: String = this::class.java.name
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        if (!VK.isLoggedIn()) {
            VK.login(this, arrayListOf(VKScope.FRIENDS))
        } else {
            replaceFragment(FriendsFragment.newInstance())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {

            override fun onLogin(token: VKAccessToken) {
                // User passed authorization
                Log.i(TAG, "onLogin: success")
                replaceFragment(FriendsFragment.newInstance())
            }

            override fun onLoginFailed(authException: VKAuthException) {
                Log.i(TAG, "onLogin: error")
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
        }
    }
}