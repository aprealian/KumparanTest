package me.aprilian.kumparantest.repository

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import me.aprilian.kumparantest.data.Post
import me.aprilian.kumparantest.data.Resource
import me.aprilian.kumparantest.datasource.api.ApiService
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], manifest=Config.NONE)
class PostRepositoryTest {

    private lateinit var context: Context

    @Mock
    private lateinit var apiService: ApiService

    private lateinit var postRepository: PostRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        context = ApplicationProvider.getApplicationContext<Context>()
        postRepository = PostRepository(context, apiService)
    }

    @Test
    fun postLocalDb_save_nullData() {
        //check input params and check is saved items is same
        Assertions.assertDoesNotThrow { postRepository.savePosts(null) }
        Assertions.assertTrue(postRepository.getPostsFromDb().isNullOrEmpty())
    }

    @Test
    fun postLocalDb_save_emptyData() {
        Assertions.assertDoesNotThrow { postRepository.savePosts(arrayListOf()) }
        Assertions.assertTrue(postRepository.getPostsFromDb().isNullOrEmpty())
    }

    @Test
    fun postLocalDb_saveData() {
        Assertions.assertDoesNotThrow { postRepository.savePosts(Post.getSample()) }
        Assertions.assertEquals(Post.getSample(), postRepository.getPostsFromDb())
    }

    @Test
    fun testPosRepository(){
        runBlocking {
            `when`(postRepository.getPosts()
            ).thenReturn(Resource(Resource.Status.ERROR, null, "Something wrong"))
        }
    }

}