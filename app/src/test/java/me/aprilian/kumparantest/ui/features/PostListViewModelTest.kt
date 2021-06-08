package me.aprilian.kumparantest.ui.features

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.lifecycle.Observer
import me.aprilian.kumparantest.data.Post
import me.aprilian.kumparantest.data.Resource
import me.aprilian.kumparantest.repository.PostRepository
import me.aprilian.kumparantest.repository.UserRepository
import me.aprilian.kumparantest.ui.features.post.PostViewModel
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], manifest= Config.NONE)
class PostListViewModelTest {

    @Mock
    private lateinit var postRepository: PostRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var context: Application
    private lateinit var viewModel: PostViewModel

    private val postId = 1

    private val posts = mock<Observer<Resource<Post>>>()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        setupContext()
        viewModel = PostViewModel(postRepository, userRepository)
    }

    private fun setupContext() {
        `when`<Context>(context.applicationContext).thenReturn(context)
    }
}