package me.aprilian.kumparantest.data
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Album(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("photos")
    var photos: ArrayList<Photo> = arrayListOf()
) : Parcelable