package com.umc.insider.exchange

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.umc.insider.R
import com.umc.insider.auth.UserManager
import com.umc.insider.databinding.ActivityExchangeModifyBinding
import com.umc.insider.retrofit.RetrofitInstance
import com.umc.insider.retrofit.api.ExchangesInterface
import com.umc.insider.revise.SaleReviseRegistrationActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExchangeModifyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExchangeModifyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_exchange_modify)

        val user_id = UserManager.getUserIdx(this)!!.toLong()
        val goods_id = intent.getStringExtra("goods_id")!!.toLong()

        val exchangesAPI = RetrofitInstance.getInstance().create(ExchangesInterface::class.java)
        //Toast.makeText(this, goods_id.toString(), Toast.LENGTH_SHORT).show()
        lifecycleScope.launch {

            try {

                val response = withContext(Dispatchers.IO) {
                    exchangesAPI.getGoodsById(goods_id)
                }


                withContext(Dispatchers.Main) {

                    binding.purchaseLocation.text = response.detail
                    binding.sellerInfo.text = response.user.nickname
                    binding.productNameTitle.text = response.title

                    binding.productName.text = response.name

                    Glide.with(binding.productImage.context)
                        .load(response.imageUrl)
                        .placeholder(null)
                        .transform(RoundedCorners(30))
                        .into(binding.productImage)

                    binding.PurchaseExpirationDate.text = response.shelfLife

                    if (response.weight.isNullOrBlank()) {
                        binding.productAmount.text = "(${response.count}개)"
                        binding.PurchaseTotalamountTv.text = "${response.count}개"
                    } else {
                        val weightText = if (response.weight.toLong() >= 1000) {
                            String.format("(%.1fkg)", response.weight.toLong() / 1000.0)
                        } else {
                            "(${response.weight}g)"
                        }
                        binding.productAmount.text = "$weightText"
                        binding.PurchaseTotalamountTv.text = "${response.count}개"
                    }

                }


            } catch (e: Exception) {

            }
        }


        binding.sellRegistorBtn.setOnClickListener {

            UserManager.getUserIdx(this@ExchangeModifyActivity)

            val intent =
                Intent(this@ExchangeModifyActivity, SaleReviseRegistrationActivity::class.java)
            intent.putExtra("goods_id", goods_id.toString())
            intent.putExtra("sellOrExchange", "exchange")
            startActivity(intent)
            finish()

        }

        binding.deleteBtn.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val response = withContext(Dispatchers.IO){exchangesAPI.deleteGoods(goods_id)}
                    withContext(Dispatchers.Main){
                        Toast.makeText(applicationContext, "상품이 성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("APIII", "Failed to delete goods", e)
                    Toast.makeText(applicationContext, "상품 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            finish()
        }
    }
}