package me.aprilian.kumparantest.data
import com.google.gson.annotations.SerializedName


class AlbumsResponse : ArrayList<AlbumsResponse.AlbumsResponseItem>(){
    data class AlbumsResponseItem(
        @SerializedName("id")
        val id: Int,
        @SerializedName("title")
        val title: String,
        @SerializedName("userId")
        val userId: Int
    )
}