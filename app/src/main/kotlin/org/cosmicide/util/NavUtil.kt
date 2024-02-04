package org.cosmicide.util

import android.content.SharedPreferences
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavController.OnDestinationChangedListener
import androidx.navigation.NavDirections
import androidx.navigation.NavGraph
import androidx.navigation.NavInflater
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.NavHostFragment
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import org.cosmicide.MainActivity
import org.cosmicide.R
import org.cosmicide.util.ResourceUtil

class NavUtil(
    val mainActivity: MainActivity,
    val destinationChangedListener: OnDestinationChangedListener,
    val sharedPrefs: SharedPreferences,
    val tag: String
) {
    private var navController: NavController? = null
    private lateinit var fragmentManager: FragmentManager

    init {
        fragmentManager = mainActivity.supportFragmentManager
        val navHostFragment: NavHostFragment = fragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment
        navController = navHostFragment.navController
        navController?.addOnDestinationChangedListener(destinationChangedListener)
    }

    fun updateStartDestination() {
        navController?.let {
            val navInflater = it.navInflater
            val graph = navInflater.inflate(R.navigation.navigation_main)
            if (ResourceUtil.hasMissingResources()) {
                graph.setStartDestination(R.id.installResourcesFragment)
            } else {
                graph.setStartDestination(R.id.projectsFragment)
            }
            it.graph = graph
        }
    }

    fun navigate(directions: NavDirections?) {
        if (navController == null || directions == null) {
            Log.e(tag, "navigate: controller or direction is null")
            return
        }
        try {
            navController!!.navigate(directions)
        } catch (e: IllegalArgumentException) {
            Log.e(tag, "navigate: " + directions, e)
        }
    }

    fun navigateFragment(directions: NavDirections?) {
        if (navController == null || directions == null) {
            Log.e(tag, "navigateFragment: controller or direction is null")
            return
        }
        try {
            navController!!.navigate(directions, getNavOptionsBuilderFragmentFadeOrSlide().build())
        } catch (e: IllegalArgumentException) {
            Log.e(tag, "navigateFragment: " + directions, e)
        }
    }

    fun navigateUp() {
        if (navController == null) {
            val navHostFragment: NavHostFragment = fragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment
            navController = navHostFragment.navController
        }
        navController!!.navigateUp()
    }

    fun getNavOptionsBuilderFragmentFadeOrSlide(): NavOptions.Builder {
        return NavOptions.Builder()
            .setEnterAnim(R.anim.open_enter_slide)
            .setExitAnim(R.anim.open_exit_slide)
            .setPopEnterAnim(R.anim.close_enter_slide)
            .setPopExitAnim(R.anim.close_exit_slide)
    }
}
