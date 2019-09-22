package com.example.memorygameandroid

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Communicator : ViewModel(){

    var message = MutableLiveData<Any>()

    fun setMsgCommunicator(msg: Any){
        message.value = msg
    }
}