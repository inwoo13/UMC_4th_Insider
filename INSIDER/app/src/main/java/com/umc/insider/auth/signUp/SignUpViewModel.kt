package com.umc.insider.auth.signUp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class SignUpViewModel : ViewModel() {

    private val _userId = MutableLiveData("")
    val userId : LiveData<String> = _userId

    private val _userNickname = MutableLiveData("")
    val userNickname : LiveData<String> = _userNickname

    private val _userPWD = MutableLiveData("")
    val userPWD : LiveData<String> = _userPWD

    private val _checkPWD = MutableLiveData("")
    val checkPWD : LiveData<String> = _checkPWD

    private val _userEmail = MutableLiveData("")
    val userEmail : LiveData<String> = _userEmail

    private val _registerNum = MutableLiveData("")
    val registerNum : LiveData<String> = _registerNum

    private val _registerNumCheckResult = MutableLiveData(false)
    private val registerNumCheckResult : LiveData<Boolean> = _registerNumCheckResult

    val idState: LiveData<EditState> = _userId.map { id ->
        when {
            id.isNullOrBlank() -> EditState.EMPTY
            REGEX_ID.toRegex().matches(id) -> EditState.CHECK
            else -> EditState.CLOSE
        }
    }

    val nicknameState : LiveData<EditState> = _userNickname.map { nickname ->
        when{
            nickname.isNullOrBlank() -> EditState.EMPTY
            REGEX_NICKNAME.toRegex().matches(nickname) -> EditState.CHECK
            else -> EditState.CLOSE
        }
    }

    val securityState: LiveData<SecurityState> = _userPWD.map { password ->
        checkPWD(password)
    }

    val checkState : LiveData<EditState> = _checkPWD.map { checkPassword ->
        when {
            checkPassword.isNullOrBlank() -> EditState.EMPTY
            checkPassword == _userPWD.value -> EditState.CHECK
            else -> EditState.CLOSE
        }
    }

    val emailState: LiveData<EditState> = _userEmail.map { email ->
        when {
            email.isNullOrBlank() -> EditState.EMPTY
            REGEX_EMAIL.toRegex().matches(email) -> EditState.CHECK
            else -> EditState.CLOSE
        }
    }

    val registerNumState: LiveData<EditState> = _registerNum.map { registerNum ->
        when{
            registerNum.isNullOrBlank() -> EditState.EMPTY
            REGEX_REGISTER.toRegex().matches(registerNum) -> EditState.CHECK
            else -> EditState.CLOSE
        }
    }

    private fun checkPWD(password : String) : SecurityState {

        if (password.isNullOrBlank()) return SecurityState.EMPTY

        val hasSpecialChars = Regex(REGEX_SPECIALCHAR).findAll(password).count() >= 1
        val hasUppercase = password.any { it.isUpperCase() }
        val hasConsecutiveChars = (0 until password.length - 2).any { password[it + 2].toInt() == password[it + 1].toInt() + 1 && password[it + 1].toInt() == password[it].toInt() + 1 }

        val conditionCount = listOf(hasSpecialChars, hasUppercase, !hasConsecutiveChars).count { it }

        return when (conditionCount) {
            3 -> SecurityState.SAFE
            2 -> SecurityState.NORMAL
            else -> SecurityState.DANGER
        }

    }

    fun setUserId(id : String){
        _userId.value = id
    }

    fun setUserNickname(nickname : String){
        _userNickname.value = nickname
    }

    fun setUserPwd(pwd : String){
        _userPWD.value = pwd
    }

    fun setCheckPwd(checkPWD : String){
        _checkPWD.value = checkPWD
    }


    fun setUserEmail(email : String){
        _userEmail.value = email
    }

    fun setResgisterNum(registerNum : String){
        _registerNum.value = registerNum
    }

    companion object{
        //최소 6자 이상, 10자 이하
        //알파벳 소문자와 대문자 포함
        //특수 문자 사용 불가
        private const val REGEX_ID = "^(?=.*[a-zA-Z])[a-zA-Z0-9]{6,10}\$"

        //최소 2자, 최대 10자
        //알파벳 대소문자, 한글 허용
        //특수문자 밑줄(_)과 하이폰(-) 허용
        private const val REGEX_NICKNAME = "^[a-zA-Z가-힣_-]{2,10}\$"

        private const val REGEX_SPECIALCHAR = "[!@#\$%^&*()-=_+]"

        private const val REGEX_EMAIL = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+\$"

        private const val REGEX_REGISTER = "^[0-9]{10}\$"
    }
}

enum class EditState{
    CHECK,
    CLOSE,
    EMPTY
}

enum class SecurityState{
    SAFE,
    NORMAL,
    DANGER,
    EMPTY
}