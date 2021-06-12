# NewsApp for Kumparan Test
Hi, I created Android news app using <a href="https://jsonplaceholder.typicode.com/">JSON Placeholder API</a> and Jetpack Component.

[Download APK v1.01 from Repo](https://drive.google.com/file/d/1UNcE4V5cWeGj-xLjHlhcTxbqbaxpZwqX/view?usp=sharing)

[Download APK from PlayStore](https://play.google.com/store/apps/details?id=me.aprilian.kumparantest)

<img src="https://github.com/aprealian/KumparanTest/blob/main/documentation/NewsApp-Preview.gif" alt="NewsApp Preview" height="480"/>

## Used Libraries

<ul>
<li><a href="https://developer.android.com/guide/navigation">Navigation</a>(Fragment transitions)</li>
<li><a href="https://developer.android.com/topic/libraries/data-binding">Data binding</a>  (Bind views and data)</li>
<li><a href="https://developer.android.com/topic/libraries/architecture/viewmodel">ViewModel</a> (Store and manage UI-related data)</li>
<li><a href="https://developer.android.com/topic/libraries/architecture/livedata">LiveData</a> (Observable data)</li>
<li><a href="https://github.com/Kotlin/kotlinx.coroutines">Kotlin Coroutine</a> (Light-weight threads)</li>
<li><a href="https://dagger.dev/hilt/">Hilt</a> (Dependency Injection)</li>
<li><a href="https://github.com/square/retrofit">Retrofit</a> (HTTP client)</li>
<li><a href="https://github.com/mockito/mockito">Mockito</a> (Unit testing)</li>
</ul>

## Architecture

This app uses MVVM architecture where the guideline you can read by <a href="https://developer.android.com/jetpack/docs/guide">here</a>.

Beside MVVM, this application is also use a single-activity application architecture. All screen transitions are done by <a href="https://developer.android.com/guide/navigation?hl=ja">Navigation</a>.

That because the design and the requirement dosen't need a complex thing. 

A single-activity can make better on 
<ul>
<li>Screen transition</li>
<li>Sharing-data using ViewModel</li>
<li>Passing argument safely with Safe Args Gradle Plugin</li>
</ul>





