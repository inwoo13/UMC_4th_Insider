package com.umc.insider.auth.signUp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.umc.insider.R
import com.umc.insider.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySignUpBinding
    private lateinit var viewModel : SignUpViewModel
    private lateinit var getSearchResult : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
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

            addressFindBtn.setOnClickListener {
                val intent = Intent(this@SignUpActivity, AddressActivity::class.java)
                getSearchResult.launch(intent)
            }
        }

    }
}