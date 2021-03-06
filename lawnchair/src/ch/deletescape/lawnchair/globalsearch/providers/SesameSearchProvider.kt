package ch.deletescape.lawnchair.globalsearch.providers

import android.content.*
import android.graphics.drawable.Drawable
import ch.deletescape.lawnchair.colors.ColorEngine
import ch.deletescape.lawnchair.globalsearch.SearchProvider
import ch.deletescape.lawnchair.sesame.Sesame
import com.android.launcher3.R
import com.android.launcher3.util.PackageManagerHelper
import ninja.sesame.lib.bridge.v1_1.LookFeelKeys

class SesameSearchProvider(context: Context) : SearchProvider(context) {

    override val name = context.getString(R.string.sesame)!!
    override val supportsVoiceSearch: Boolean
        get() = true
    override val supportsAssistant: Boolean
        get() = true
    override val supportsFeed = false
    override val settingsIntent get () = Intent(Sesame.ACTION_OPEN_SETTINGS).setPackage(Sesame.PACKAGE)

    override val isAvailable: Boolean
        get() = PackageManagerHelper.isAppEnabled(context.packageManager, Sesame.PACKAGE, 0)

    override fun startSearch(callback: (intent: Intent) -> Unit) = callback(Intent("ninja.sesame.app.action.OPEN_SEARCH").setPackage(Sesame.PACKAGE))

    override fun startVoiceSearch(callback: (intent: Intent) -> Unit) = startAssistant(callback)

    override fun startAssistant(callback: (intent: Intent) -> Unit) = callback(Intent(Intent.ACTION_VOICE_COMMAND).setPackage(GoogleSearchProvider.PACKAGE))

    override fun getIcon(): Drawable = context.getDrawable(R.drawable.ic_sesame_large)!!.mutate().apply { setTint(getTint(context)) }

    override fun getVoiceIcon(): Drawable? = getAssistantIcon()

    override fun getAssistantIcon(): Drawable? = context.getDrawable(R.drawable.opa_assistant_logo)!!.mutate().apply { setTint(getTint(context)) }

    private fun getTint(context: Context): Int {
        if (Sesame.isAvailable(context)) {
            (Sesame.LookAndFeel[LookFeelKeys.SEARCH_ICON_COLOR] as? Int)?.let {
                return it
            }
        }
        return ColorEngine.getInstance(context).accent
    }

}
