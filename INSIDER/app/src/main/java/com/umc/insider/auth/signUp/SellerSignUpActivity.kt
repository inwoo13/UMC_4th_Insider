package com.umc.insider.auth.signUp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.umc.insider.R
import com.umc.insider.auth.login.LogInActivity
import com.umc.insider.databinding.ActivitySellerSignUpBinding
import com.umc.insider.retrofit.RegisterNumCheckInstance
import com.umc.insider.retrofit.RetrofitInstance
import com.umc.insider.retrofit.api.RegisterNumCheckInterface
import com.umc.insider.retrofit.api.UserInterface
import com.umc.insider.retrofit.model.SignUpPostReq
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.create

class SellerSignUpActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySellerSignUpBinding
    private lateinit var viewModel : SignUpViewModel
    private lateinit var getSearchResult : ActivityResultLauncher<Intent>
    private val userAPI = RetrofitInstance.getInstance().create(UserInterface::class.java)
    private val registerNumCheckAPI = RegisterNumCheckInstance.getInstance().create(RegisterNumCheckInterface::class.java)
    private var registerNumCheckResult = false
    private var registerNum : Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_seller_sign_up)

        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]

        binding.vm = viewModel
        binding.lifecycleOwner = this

        getSearchResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            val fulladdress = it.data?.getStringExtra("data")
            if (!fulladdress.isNullOrBlank()){
                val addressSplit = fulladdress.split(",")
                binding.addressText.text = addressSplit[0]
                binding.addressNum.text = addressSplit[1]
            }
        }

        initView()
    }

    private fun initView(){
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        with(binding){

            idEdit.addTextChangedListener { viewModel.setUserId(it.toString()) }
            nicknameEdit.addTextChangedListener { viewModel.setUserNickname(it.toString()) }
            pwdEdit.addTextChangedListener { viewModel.setUserPwd(it.toString()) }
            pwdCheckEdit.addTextChangedListener { viewModel.setCheckPwd(it.toString()) }
            emailEdit.addTextChangedListener { viewModel.setUserEmail(it.toString()) }
            registerNumEdit.addTextChangedListener {
                viewModel.setResgisterNum((it.toString()))
                if (registerNumCheckResult) { registerNumCheckResult = false }
            }

            addressFindBtn.setOnClickListener {
                val intent = Intent(this@SellerSignUpActivity, AddressActivity::class.java)
                getSearchResult.launch(intent)
            }

            registerNumCheck.setOnClickListener {
                if (binding.registerNumEdit.text.length != 10){
                    Toast.makeText(this@SellerSignUpActivity, "사업자 번호 형식을 입력해 주세요",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                lifecycleScope.launch {

                    try {

                        val response = withContext(Dispatchers.IO) {
                            registerNumCheckAPI.fetchData(
                                key = getString(R.string.register_num_check_key),
                                query = binding.registerNumEdit.text.toString()
                            )
                        }

                        if (response.isSuccessful) {
                            Log.d("REGISTERRR", "여기는 왜")
                            val result = response.body()!!.items[0]
                            Log.d("REGISTERRR", "됨?")
                            registerNum = binding.registerNumEdit.text.toString().toLong()
                            if (result.bstt == "계속사업자"){
                                registerNumCheckResult = true
                                Toast.makeText(this@SellerSignUpActivity, "사업자 인증되었습니다.", Toast.LENGTH_SHORT).show()
                                binding.registerNumEdit.setCompoundDrawablesWithIntrinsicBounds(
                                    null, null,
                                    AppCompatResources.getDrawable(it.context, R.drawable.baseline_check_24), null
                                )
                            }

                        }


                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Log.d("에러", e.message.toString())
                        }
                    }
                }
            }

            signUpBtn.setOnClickListener {

                if (
                    idEdit.text.isNullOrBlank() || nicknameEdit.text.isNullOrBlank() || pwdEdit.text.isNullOrBlank() ||
                    pwdCheckEdit.text.isNullOrBlank() || emailEdit.text.isNullOrBlank() || addressNum.text.isNullOrBlank() ||
                    addressText.text.isNullOrBlank() || registerNumEdit.text.isNullOrBlank()
                ){
                    Toast.makeText(this@SellerSignUpActivity, "빈 항목을 채워주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (!registerNumCheckResult) {
                    Toast.makeText(this@SellerSignUpActivity, "사업자 인증을 해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                lifecycleScope.launch {

                    val userId = idEdit.text.toString()
                    val nickname = nicknameEdit.text.toString()
                    val pwd = pwdEdit.text.toString()
                    val email = emailEdit.text.toString()
                    val zipCode = addressNum.text.toString().toInt()
                    val detailAddress = addressText.text.toString()
                    //val registerNum = registerNumEdit.text.toString().toLong()
                    val sellerOrBuyer: Int = 1

                    // 구매자 회원가입
                    val signUpPostReq = SignUpPostReq(userId,nickname,pwd,email, zipCode, detailAddress, sellerOrBuyer, registerNum!!)

                    val response = withContext(Dispatchers.IO){
                        userAPI.createUser(signUpPostReq)
                    }

                    if (response.isSuccessful){
                        val baseResponse = response.body()
                        if(baseResponse?.isSuccess == true){
                            val loginPostRes = baseResponse.result
                            Toast.makeText(this@SellerSignUpActivity, "회원가입 성공하셨습니다.", Toast.LENGTH_SHORT).show()
                            finish()
                        }else{
                            // baseResponse가 실패
                            //Log.d("signuperror",baseResponse!!.message)
                        }
                    }else{
                        // 네트워크 에러 처리
                    }
                }
            }
        }

    }
}