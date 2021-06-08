package me.aprilian.kumparantest.datasource

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import me.aprilian.kumparantest.data.Post
import me.aprilian.kumparantest.datasource.local.LocalDataStorage
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], manifest=Config.NONE)
class DataSourceTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
    }

    @Test
    fun localDataStorage_checkParamNotThrowException() {
        val localDataStorage = LocalDataStorage.getInstance(context)
        //check input params and check is saved items is same
        assertDoesNotThrow { localDataStorage?.addPosts(null) }
        assertTrue(localDataStorage?.getPosts.isNullOrEmpty())

        assertDoesNotThrow { localDataStorage?.addPosts(arrayListOf()) }
        assertTrue(localDataStorage?.getPosts.isNullOrEmpty())

        assertDoesNotThrow { localDataStorage?.addPosts(Post.getSample()) }
        assertEquals(Post.getSample(), localDataStorage?.getPosts)
    }

}