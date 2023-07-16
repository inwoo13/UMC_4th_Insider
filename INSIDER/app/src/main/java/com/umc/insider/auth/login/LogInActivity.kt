package com.umc.insider.auth.login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.umc.insider.MainActivity
import com.umc.insider.R
import com.umc.insider.auth.signUp.SignUpActivity
import com.umc.insider.databinding.ActivityLogInBinding


class LogInActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLogInBinding

    // Google
    lateinit var mGoogleSignClient : GoogleSignInClient
    lateinit var resultLauncher : ActivityResultLauncher<Intent>

    // Facebook
    private lateinit var callbackManager : CallbackManager
    private lateinit var loginManager : LoginManager

    override fun onStart() {
        super.onStart()

        // Google
        val account = GoogleSignIn.getLastSignedInAccount(this)
        account?.let {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } ?: {}
        // Facebook
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        if(isLoggedIn){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }else { }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_log_in)


        initView()
        setResultSignUp()

        // Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignClient = GoogleSignIn.getClient(this, gso)

        // Facebook
        callbackManager = CallbackManager.Factory.create()
        loginManager = LoginManager.getInstance()

    }

    private fun initView(){
        with(binding){
            singUp.setOnClickListener {
                startActivity(Intent(this@LogInActivity, SignUpActivity::class.java))
            }
            googleBtn.setOnClickListener {
                signIn()
            }
            facebookBtn.setOnClickListener {
                loginManager.logInWithReadPermissions(this@LogInActivity, listOf("public_profile", "email"))
                loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult?>{
                    override fun onSuccess(loginResult: LoginResult?) {
                        val graphRequest = GraphRequest.newMeRequest(loginResult?.accessToken) { f_object, response ->
                            // {token: loginResult.accessToken / userObject: f_object}
                            //Toast.makeText(this@LogInActivity, "onSuccess: token: ${loginResult?.accessToken} \n\n userObject: ${f_object}}", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this@LogInActivity, MainActivity::class.java))
                            finish()
                        }
                        val parameters = Bundle()
                        parameters.putString("fields", "id,name,email,gender,birthday")
                        graphRequest.parameters = parameters
                        graphRequest.executeAsync()
                    }

                    override fun onCancel() {
                        //Toast.makeText(this@LogInActivity, "로그인 취소", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(error: FacebookException?) {
                        //Toast.makeText(this@LogInActivity, "로그인 에러", Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun setResultSignUp() {
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(
        )) { result ->
            if( result.resultCode == Activity.RESULT_OK){
                val task : Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else{
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
//            val email = account?.email.toString()
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

}