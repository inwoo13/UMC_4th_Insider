package com.umc.insider.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatRoomViewModel : ViewModel(){

    private var _currentScrollPosition = MutableLiveData<Int>()
    private var _isKeyboardVisible = MutableLiveData<Boolean>()

    val currentScrollPosition: LiveData<Int> get() = _currentScrollPosition
    val isKeyboardVisible: LiveData<Boolean> get() = _isKeyboardVisible

    fun setScrollPosition(position: Int) {
        _currentScrollPosition.value = position
    }

    fun setKeyboardVisibility(visible: Boolean) {
        _isKeyboardVisible.value = visible
    }
}