package me.aprilian.kumparantest.data
import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Post(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("body")
    val body: String,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("user")
    var user: User? = null
) : Parcelable {
    companion object{
        fun getSample(): ArrayList<Post> {
            return arrayListOf(
                Post(id = 1, title = "Lorem ipsum 1", body = "This is the body", userId = 1),
                Post(id = 2, title = "Lorem ipsum 2", body = "This is the body", userId = 1),
                Post(id = 3, title = "Lorem ipsum 3", body = "This is the body", userId = 1),
                Post(id = 4, title = "Lorem ipsum 4", body = "This is the body", userId = 1),
            )
        }
    }
}