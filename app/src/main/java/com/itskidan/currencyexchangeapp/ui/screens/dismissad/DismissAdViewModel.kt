package com.itskidan.currencyexchangeapp.ui.screens.dismissad

import androidx.lifecycle.ViewModel
import com.itskidan.currencyexchangeapp.domain.Interactor
import javax.inject.Inject

class DismissAdViewModel: ViewModel()  {

    @Inject
    lateinit var interactor: Interactor

    fun getDismissAdPrice():String{
        return "4,99 USD"
    }
}