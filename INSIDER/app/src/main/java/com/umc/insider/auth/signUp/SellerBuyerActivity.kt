package com.umc.insider.auth.signUp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.umc.insider.R
import com.umc.insider.databinding.ActivitySellerBuyerBinding

class SellerBuyerActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySellerBuyerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_seller_buyer)

        initview()
    }

    private fun initview(){
        with(binding){
            // 판매자 클릭시
            sellerLayout.setOnClickListener {
                val intent = Intent(this@SellerBuyerActivity, SellerSignUpActivity::class.java)
                startActivity(intent)
                finish()
            }
            // 구매자 클릭시
            buyerLayout.setOnClickListener {
                val intent = Intent(this@SellerBuyerActivity, SignUpActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
    }
}