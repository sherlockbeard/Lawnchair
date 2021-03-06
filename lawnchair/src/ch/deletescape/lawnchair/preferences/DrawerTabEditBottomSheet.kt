/*
 *     This file is part of Lawnchair Launcher.
 *
 *     Lawnchair Launcher is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Lawnchair Launcher is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Lawnchair Launcher.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.preferences

import android.content.Context
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import ch.deletescape.lawnchair.applyColor
import ch.deletescape.lawnchair.colors.ColorEngine
import ch.deletescape.lawnchair.groups.AppGroups
import ch.deletescape.lawnchair.groups.DrawerTabs
import ch.deletescape.lawnchair.lawnchairPrefs
import ch.deletescape.lawnchair.settings.ui.SettingsBottomSheet
import ch.deletescape.lawnchair.views.BaseBottomSheet
import com.android.launcher3.Launcher
import com.android.launcher3.R

class DrawerTabEditBottomSheet(context: Context, config: AppGroups.Group.CustomizationMap,
                               private val callback: (Boolean) -> Unit) : FrameLayout(context), View.OnClickListener {

    init {
        View.inflate(context, R.layout.drawer_tab_edit_bottom_sheet, this)

        val accent = ColorEngine.getInstance(context).accent
        val container = findViewById<ViewGroup>(R.id.customization_container)
        config.sortedEntries.reversed().forEach { entry ->
            entry.createRow(context, container, accent)?.let { container.addView(it, 0) }
        }

        findViewById<Button>(R.id.save).apply {
            applyColor(accent)
            setTextColor(accent)
            setOnClickListener(this@DrawerTabEditBottomSheet)
        }
        findViewById<TextView>(R.id.cancel).apply {
            setOnClickListener(this@DrawerTabEditBottomSheet)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.save -> callback(true)
            R.id.cancel -> callback(false)
        }
    }

    companion object {

        fun show(context: Context, config: AppGroups.Group.CustomizationMap, callback: () -> Unit) {
            val sheet = SettingsBottomSheet.inflate(context)
            sheet.show(DrawerTabEditBottomSheet(context, config) {
                if (it) {
                    callback()
                }
                sheet.close(true)
            }, true)
        }

        fun show(launcher: Launcher, config: AppGroups.Group.CustomizationMap, callback: () -> Unit, animate: Boolean = true) {
            val sheet = BaseBottomSheet.inflate(launcher)
            sheet.show(DrawerTabEditBottomSheet(launcher, config) {
                if (it) {
                    callback()
                }
                sheet.close(true)
            }, animate)
        }

        fun newTab(context: Context, callback: (AppGroups.Group.CustomizationMap) -> Unit) {
            val config = DrawerTabs.CustomTab(context).customizations
            show(context, config) {
                callback(config)
            }
        }

        fun edit(context: Context, group: AppGroups.Group, callback: () -> Unit) {
            val config = AppGroups.Group.CustomizationMap(group.customizations)
        show(context, config) {
                group.customizations.applyFrom(config)
                context.lawnchairPrefs.drawerTabs.saveToJson()
                callback()
            }
        }

        fun edit(launcher: Launcher, group: AppGroups.Group) {
            val config = AppGroups.Group.CustomizationMap(group.customizations)
            edit(launcher, config, group)
        }

        fun edit(launcher: Launcher, config: AppGroups.Group.CustomizationMap,
                 group: AppGroups.Group, animate: Boolean = true) {
            show(launcher, config, {
                group.customizations.applyFrom(config)
                launcher.lawnchairPrefs.drawerTabs.saveToJson()
            }, animate)
        }
    }
}
