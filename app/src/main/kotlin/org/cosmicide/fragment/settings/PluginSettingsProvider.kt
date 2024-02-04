/*
 * This file is part of Cosmic IDE.
 * Cosmic IDE is a free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * Cosmic IDE is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Cosmic IDE. If not, see <https://www.gnu.org/licenses/>.
 */

package org.cosmicide.fragment.settings

import androidx.core.content.res.ResourcesCompat
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.helpers.categoryHeader
import de.Maxr1998.modernpreferences.helpers.editText
import de.Maxr1998.modernpreferences.helpers.onClick
import de.Maxr1998.modernpreferences.helpers.pref
import org.cosmicide.MainActivity
import org.cosmicide.R
import org.cosmicide.fragment.PluginListFragment
import org.cosmicide.fragment.PluginsFragment
import org.cosmicide.fragment.SettingsFragmentDirections
import org.cosmicide.common.Prefs
import org.cosmicide.rewrite.plugin.api.PluginLoader
import org.cosmicide.util.PreferenceKeys

class PluginSettingsProvider(private val activity: MainActivity) : SettingsProvider {

    override fun provideSettings(builder: PreferenceScreen.Builder) {
        builder.apply {
            icon = ResourcesCompat.getDrawable(
                activity.resources,
                R.drawable.outline_extension_24,
                activity.theme
            )
            pref(PreferenceKeys.AVAILABLE_PLUGINS) {
                title = "Available plugins"
                summary = "View available plugins"
                onClick {
                    activity.navUtil.navigateFragment(
                        SettingsFragmentDirections.actionSettingsFragmentToPluginListFragment()
                    )
                    true
                }
            }

            pref(PreferenceKeys.INSTALLED_PLUGINS) {
                title = "Installed plugins"
                summary = "View installed plugins"
                onClick {
                    activity.navUtil.navigateFragment(
                        SettingsFragmentDirections.actionSettingsFragmentToPluginsFragment()
                    )
                    true
                }
            }

            editText(PreferenceKeys.PLUGIN_REPOSITORY) {
                title = "Repository"
                summary = "Add a custom plugin repository"
                defaultValue = Prefs.pluginRepository
            }

            categoryHeader(PreferenceKeys.PLUGIN_SETTINGS) {
                title = "Plugin settings"
            }
            PluginLoader.prefsMethods.forEach {
                it.invoke(this)
            }
        }
    }
}
