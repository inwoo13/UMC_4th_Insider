package com.umc.insider.saleregistraion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SaleRegistrationViewModel : ViewModel() {

    private val _saleTitle = MutableLiveData("")
    val saleTitle : LiveData<String> = _saleTitle

    private val _productName = MutableLiveData("")
    val productName : LiveData<String> = _productName

    private val _productWeight = MutableLiveData("")
    val productWeight : LiveData<String> = _productWeight

    private val _productPrice = MutableLiveData("")
    val productPrice : LiveData<String> = _productPrice

    private val _ExpirationDate = MutableLiveData("")
    val ExpirationDate : LiveData<String> = _ExpirationDate

    // 판매위치는 제일 마지막에 하는걸로..(주소)



    private var selectedCategory: String? = null

    fun setSelectedCategory(category: String) {
        selectedCategory = category
    }

}