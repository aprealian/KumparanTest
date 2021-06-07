package me.aprilian.kumparantest.datasource.local

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.aprilian.kumparantest.data.Post


class LocalDataStorage private constructor(
    context: Context
) {
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val gson: Gson = Gson()

    fun addPosts(list: List<Post>?) {
        sharedPreferences.edit().putString("posts", gson.toJson(list)).apply()
    }

    val getPosts: List<Post>? get() {
        var contestList: List<Post>? = gson.fromJson<List<Post>>(sharedPreferences.getString("posts", ""), object : TypeToken<List<Post>?>() {}.type)
        if (contestList == null) contestList = ArrayList()
        return contestList
    }

    fun clearData() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        private var instance: LocalDataStorage? = null
        fun getInstance(context: Context): LocalDataStorage? {
            if (instance == null) {
                instance = LocalDataStorage(context)
            }
            return instance
        }
    }

}