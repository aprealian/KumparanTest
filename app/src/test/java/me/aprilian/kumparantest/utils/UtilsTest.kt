package me.aprilian.kumparantest.utils

import android.content.Context
import android.os.Build
import android.widget.ImageView
import androidx.test.core.app.ApplicationProvider
import me.aprilian.kumparantest.R
import me.aprilian.kumparantest.utils.Utils.toast
import me.aprilian.kumparantest.utils.extension.load
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], manifest=Config.NONE)
class UtilsTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
    }

    @Test
    fun toastMessage_notThrowException() {
        assertDoesNotThrow { context.toast(message =  "Hello world") }
        assertDoesNotThrow { context.toast(message = null) }
    }

    @Test
    fun dpToPx_notNull() {
        assertNotEquals(null, Utils.dpToPx(context = context, dp = 16f))
    }

    @Test
    fun pxToDp_notNull() {
        assertNotEquals(null, Utils.pxToDp(context = context, px = 16f))
    }

    @Test
    fun spacesItemDecorationTest() {
        assertDoesNotThrow { SpacesItemDecoration(16) }
        assertDoesNotThrow { SpacesItemDecoration(0) }
        assertDoesNotThrow { SpacesItemDecoration(-16) }
        assertDoesNotThrow { SpacesItemDecoration(1000) }
    }

    @Test
    fun imageViewExtension_loadImageSourceTest() {
        val imageView = ImageView(context)
        assertDoesNotThrow { imageView.load("") }
        assertDoesNotThrow { imageView.load("https://") }
        assertDoesNotThrow { imageView.load(1) }
        assertDoesNotThrow { imageView.load(-1) }
        assertDoesNotThrow { imageView.load(R.drawable.ic_launcher_foreground) }
        assertDoesNotThrow { imageView.load("https://www.apple.com/ac/structured-data/images/knowledge_graph_logo.png") }
    }

}