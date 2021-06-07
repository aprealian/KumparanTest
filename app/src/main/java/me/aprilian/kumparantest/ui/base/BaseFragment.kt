package me.aprilian.kumparantest.ui.base

import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

open class BaseFragment: Fragment() {
    fun navigateBack(){
        view?.findNavController()?.navigateUp()
    }
}