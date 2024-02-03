/*
 * This file is part of Cosmic IDE.
 * Cosmic IDE is a free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * Cosmic IDE is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Cosmic IDE. If not, see <https://www.gnu.org/licenses/>.
 */

package org.cosmicide

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.launch
import org.cosmicide.common.Prefs
import org.cosmicide.databinding.ActivityMainBinding
import org.cosmicide.fragment.InstallResourcesFragment
import org.cosmicide.fragment.ProjectFragment
import org.cosmicide.util.CommonUtils
import org.cosmicide.util.ResourceUtil
import org.cosmicide.util.awaitBinderReceived
import org.cosmicide.util.isShizukuInstalled
import rikka.shizuku.Shizuku
import rikka.shizuku.Shizuku.OnRequestPermissionResultListener
import rikka.shizuku.ShizukuProvider

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val REQUEST_CODE_SHIZUKU = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        DynamicColors.applyToActivityIfAvailable(this)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(applyBottomInset(binding.root))

        ResourceUtil.checkForMissingResources(
            fragmentManager = supportFragmentManager,
            containerId = binding.fragmentContainerId
        )

        Shizuku.addRequestPermissionResultListener(listener)

        if (!Shizuku.pingBinder()) {
            Shizuku.addBinderReceivedListener {
                if (checkShizukuPermission()) {
                    ShizukuUtil.isReady = true
                }
            }
        } else {
            if (checkShizukuPermission()) {
                ShizukuUtil.isReady = true
            }
        }
    }

    private fun applyBottomInset(view: View): View {
        return view.also {
            ViewCompat.setOnApplyWindowInsetsListener(it) { view, insets ->
                val bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.ime()).bottom
                view.updatePadding(bottom = bottomInset)

                insets
            }
        }
    }

    private val listener = OnRequestPermissionResultListener { requestCode, grantResult ->
        if (requestCode != REQUEST_CODE_SHIZUKU) {
            return@OnRequestPermissionResultListener
        }
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            ShizukuUtil.isReady = true
        }
    }

    private fun checkShizukuPermission(): Boolean {
        if (Shizuku.isPreV11()) {
            return false
        } else if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            return true
        } else if (Shizuku.shouldShowRequestPermissionRationale()) {
            return false
        } else {
            Shizuku.requestPermission(REQUEST_CODE_SHIZUKU)
            return false;
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(listener)
    }
}
