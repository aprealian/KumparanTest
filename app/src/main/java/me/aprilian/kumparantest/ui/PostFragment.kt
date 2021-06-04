package me.aprilian.kumparantest.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import me.aprilian.kumparantest.R

@AndroidEntryPoint
class PostFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_post, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(){}
    }
}