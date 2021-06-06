package me.aprilian.kumparantest.api

import android.content.Context
import me.aprilian.kumparantest.R

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(message: String, data: T? = null): Resource<T> {
            return Resource(Status.ERROR, data, message)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }

        fun isNoInternetConnection(error: String?): Boolean{
            return error?.contains("No address associated with hostname") ?: false
        }

        fun getErrorMessageToUser(context: Context, message: String?): String {
            return if (isNoInternetConnection(message)) context.getString(R.string.no_internet_connection) else context.getString(R.string.request_failed)
        }
    }
}