package me.aprilian.kumparantest.repository

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import me.aprilian.kumparantest.data.Post
import me.aprilian.kumparantest.datasource.api.ApiService
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.junit.runner.RunWith
import org.mockito.Mock
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
        MockitoAnnotations.initMocks(this);
        context = ApplicationProvider.getApplicationContext<Context>()
        postRepository = PostRepository(context, apiService)
    }

    @Test
    fun postLocalDb_checkParamNotThrowException() {
        //check input params and check is saved items is same
        Assertions.assertDoesNotThrow { postRepository.savePosts(null) }
        Assertions.assertTrue(postRepository.getPostsFromDb().isNullOrEmpty())

        Assertions.assertDoesNotThrow { postRepository.savePosts(arrayListOf()) }
        Assertions.assertTrue(postRepository.getPostsFromDb().isNullOrEmpty())

        Assertions.assertDoesNotThrow { postRepository.savePosts(Post.getSample()) }
        Assertions.assertEquals(Post.getSample(), postRepository.getPostsFromDb())
    }

}