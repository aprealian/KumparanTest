package me.aprilian.kumparantest.data
import com.google.gson.annotations.SerializedName

class PhotoResponse : ArrayList<PhotoResponse.PhotoItem>(){
    data class PhotoItem(
        @SerializedName("albumId")
        val albumId: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("thumbnailUrl")
        val thumbnailUrl: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("url")
        val url: String
    ) {
        companion object{
            fun getSamples(): ArrayList<PhotoItem>{
                return arrayListOf(
                    PhotoItem (albumId = 1, id  = 1, thumbnailUrl = "https://blue.kumparan.com/image/upload/fl_progressive,fl_lossy,c_fill,q_auto:best,w_640/v1594695581/batman_zw96yk.jpg", title = "Batman", url = "https://blue.kumparan.com/image/upload/fl_progressive,fl_lossy,c_fill,q_auto:best,w_640/v1594695581/batman_zw96yk.jpg"),
                    PhotoItem (albumId = 1, id  = 2, thumbnailUrl = "https://blue.kumparan.com/image/upload/fl_progressive,fl_lossy,c_fill,q_auto:best,w_640/v1594695581/batman_zw96yk.jpg", title = "Batman", url = "https://blue.kumparan.com/image/upload/fl_progressive,fl_lossy,c_fill,q_auto:best,w_640/v1594695581/batman_zw96yk.jpg"),
                    PhotoItem (albumId = 1, id  = 3, thumbnailUrl = "https://blue.kumparan.com/image/upload/fl_progressive,fl_lossy,c_fill,q_auto:best,w_640/v1594695581/batman_zw96yk.jpg", title = "Batman", url = "https://blue.kumparan.com/image/upload/fl_progressive,fl_lossy,c_fill,q_auto:best,w_640/v1594695581/batman_zw96yk.jpg"),
                    PhotoItem (albumId = 1, id  = 4, thumbnailUrl = "https://blue.kumparan.com/image/upload/fl_progressive,fl_lossy,c_fill,q_auto:best,w_640/v1594695581/batman_zw96yk.jpg", title = "Batman", url = "https://blue.kumparan.com/image/upload/fl_progressive,fl_lossy,c_fill,q_auto:best,w_640/v1594695581/batman_zw96yk.jpg"),
                    PhotoItem (albumId = 1, id  = 5, thumbnailUrl = "https://blue.kumparan.com/image/upload/fl_progressive,fl_lossy,c_fill,q_auto:best,w_640/v1594695581/batman_zw96yk.jpg", title = "Batman", url = "https://blue.kumparan.com/image/upload/fl_progressive,fl_lossy,c_fill,q_auto:best,w_640/v1594695581/batman_zw96yk.jpg"),
                )
            }
        }
    }
}