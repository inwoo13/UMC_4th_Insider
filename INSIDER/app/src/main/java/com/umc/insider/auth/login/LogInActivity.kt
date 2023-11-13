package com.umc.insider.auth.login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.umc.insider.MainActivity
import com.umc.insider.R
import com.umc.insider.auth.signUp.SellerBuyerActivity
import com.umc.insider.auth.AutoLoginManager
import com.umc.insider.auth.TokenManager
import com.umc.insider.auth.UserManager
import com.umc.insider.databinding.ActivityLogInBinding
import com.umc.insider.retrofit.RetrofitInstance
import com.umc.insider.retrofit.api.KakaoInterface
import com.umc.insider.retrofit.api.UserInterface
import com.umc.insider.retrofit.model.KakaoPostReq
import com.umc.insider.retrofit.model.LoginPostReq
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LogInActivity : AppCompatActivity() {


    private lateinit var binding : ActivityLogInBinding
    private val userAPI = RetrofitInstance.getInstance().create(UserInterface::class.java)
    private val kakaoAPI = RetrofitInstance.getInstance().create(KakaoInterface::class.java)

    private lateinit var autoLoginManager: AutoLoginManager

    // Google
    lateinit var mGoogleSignClient : GoogleSignInClient
    lateinit var resultLauncher : ActivityResultLauncher<Intent>


    override fun onStart() {
        super.onStart()

        // Google
//        val account = GoogleSignIn.getLastSignedInAccount(this)
//        account?.let {
////            startActivity(Intent(this, MainActivity::class.java))
////            finish()
//            goMainActivity()
//        } ?: {}


        if(!TokenManager.getToken(applicationContext).isNullOrBlank() &&
            !UserManager.getUserIdx(applicationContext).isNullOrBlank() &&
                AutoLoginManager(applicationContext).isAutoLogin()){
            goMainActivity()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_log_in)
        //Log.d("login", "keyhash : ${Utility.getKeyHash(this)}")

        autoLoginManager = AutoLoginManager(this)

        binding.autoLoginSwitch.isChecked = autoLoginManager.isAutoLogin()

        initView()
        setResultSignUp()

        // Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignClient = GoogleSignIn.getClient(this, gso)
        val account = GoogleSignIn.getLastSignedInAccount(this)

        var keyHash = Utility.getKeyHash(this)
        //Log.d("keyhash", keyHash)

    }

    private fun initView(){
        with(binding){

            logIn.setOnClickListener {

                if (idEdit.text.isNullOrBlank()) {
                    Toast.makeText(this@LogInActivity, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (pwdEdit.text.isNullOrBlank()) {
                    Toast.makeText(this@LogInActivity, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val id = idEdit.text.toString()

                val pwd = pwdEdit.text.toString()
                val loginPostReq = LoginPostReq(id,pwd)

                lifecycleScope.launch {
                    val response = withContext(Dispatchers.IO) {
                        userAPI.logIn(loginPostReq)
                    }
                    if(response.isSuccessful){

                        val baseResponse = response.body()

                        if(baseResponse?.isSuccess == true){

                            val loginPostRes = baseResponse.result
                            TokenManager.saveToken(this@LogInActivity, loginPostRes?.jwt)
                            UserManager.saveUserIdx(this@LogInActivity, loginPostRes?.id)
                            UserManager.setUserSellerOrBuyer(this@LogInActivity, loginPostRes?.sellerOrBuyer)
                            if (autoLoginSwitch.isChecked){
                                autoLoginManager.setAutoLogin(true)
                            }else{
                                autoLoginManager.setAutoLogin(false)
                            }

                            goMainActivity()
                        }else{
                            // baseResponse가 실패한 경우의 처리
                            //Log.d("loginerror",baseResponse!!.message)
                        }

                    }else{
                        // 네트워크 에러 처리
                    }
                }
            }

            singUp.setOnClickListener {
                startActivity(Intent(this@LogInActivity, SellerBuyerActivity::class.java))
            }
            googleBtn.setOnClickListener {
                Toast.makeText(this@LogInActivity, "로그인은 되지만 미구현입니다.",Toast.LENGTH_SHORT).show()
                signIn()
            }

            kakaoBtn.setOnClickListener {
                //Toast.makeText(this@LogInActivity, "누름", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LogInActivity, KakaoWebViewActivity::class.java)
                resultLauncher.launch(intent)

            }
        }
    }

    private fun setResultSignUp() {
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(
        )) { result ->
            if( result.resultCode == Activity.RESULT_OK){
                val task : Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
                //startActivity(Intent(this, MainActivity::class.java))
                //finish()
                Toast.makeText(this@LogInActivity, "로그인 성공! - 연결 x.",Toast.LENGTH_SHORT).show()
            }else{
                //Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
            if ( result.resultCode == 101){
                val code = result.data?.getStringExtra("code")
                if (code != null) {
                    //Toast.makeText(this@LogInActivity, code,Toast.LENGTH_SHORT).show()
                    lifecycleScope.launch {

                        try {
                            val response = withContext(Dispatchers.IO){
                                kakaoAPI.kakaoCallback(code)
                            }
                            if (response.isSuccess){
                                Log.d("카카오", response.result.toString())
                                val result = response.result
                                val id = result!!.userId
                                val kakaoPostReq = KakaoPostReq(id)

                                lifecycleScope.launch {
                                    val response = withContext(Dispatchers.IO) {
                                        kakaoAPI.logIn(kakaoPostReq)
                                    }
                                    if(response.isSuccess){

                                        val baseResponse = response.result

                                        TokenManager.saveToken(this@LogInActivity, "empty")
                                        UserManager.saveUserIdx(this@LogInActivity, baseResponse?.id)
                                        UserManager.setUserSellerOrBuyer(this@LogInActivity, baseResponse?.sellerOrBuyer)
                                        //Toast.makeText(this@LogInActivity, loginPostRes?.id.toString(),Toast.LENGTH_SHORT).show()
                                        if (binding.autoLoginSwitch.isChecked){
                                            autoLoginManager.setAutoLogin(true)
                                        }else{
                                            autoLoginManager.setAutoLogin(false)
                                        }
                                        goMainActivity()
                                    }else{
                                        // 네트워크 에러 처리
                                    }
                                }
                            }else{
                                Log.d("카카오", "다른 에러")
                            }
                        }catch (e : Exception){
                            Log.d("카카오", "네트워크 에러")
                        }
                    }
                } else {
                    // code 값이 없는 경우의 처리
                    Toast.makeText(this@LogInActivity, "code가 없습니다.",Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val email = account?.email.toString()
//            Toast.makeText(this, email, Toast.LENGTH_SHORT).show()
//            val familyName = account?.familyName.toString()
//            val givenName = account?.givenName.toString()
//            val displayName = account?.displayName.toString()
//            val photoUrl = account?.photoUrl.toString()
        }catch (e : ApiException){

        }
    }

    private fun signIn() {
        val signInIntent : Intent = mGoogleSignClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private fun goMainActivity(){
        startActivity(Intent(this@LogInActivity, MainActivity::class.java))
        finish()
    }

}