package com.umc.insider.revise

import Category
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.umc.insider.R
import com.umc.insider.adapter.CustomSpinnerAdapter
import com.umc.insider.auth.UserManager
import com.umc.insider.databinding.ActivitySaleReviseRegistrationBinding
import com.umc.insider.retrofit.model.PartialExchanges
import com.umc.insider.retrofit.RetrofitInstance
import com.umc.insider.retrofit.api.ExchangesInterface
import com.umc.insider.retrofit.api.GoodsInterface
import com.umc.insider.retrofit.model.PartialGoods
import com.umc.insider.saleregistraion.SaleRegistrationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class SaleReviseRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySaleReviseRegistrationBinding
    private lateinit var viewModel: SaleRegistrationViewModel
    private lateinit var getSearchResult: ActivityResultLauncher<Intent>

    private lateinit var categorySpinner: Spinner
    private lateinit var adapter: CustomSpinnerAdapter
    private var currentPrice = ""

    private var imgUri: Uri? = null

    private var sellOrExchange = ""
    private var imgUrl : String = ""

    private val selectImageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imgUri = result.data?.data
                Glide.with(this)
                    .load(imgUri)
                    .into(binding.sellImageView)
            }
        }

    private val goodsAPI = RetrofitInstance.getInstance().create(GoodsInterface::class.java)
    private val exchangesAPI = RetrofitInstance.getInstance().create(ExchangesInterface::class.java)
    private var isGeneralSaleSelected = true    // true면 일반 판매

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sale_revise_registration)

        getSearchResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val fulladdress = it.data?.getStringExtra("data")
                if (!fulladdress.isNullOrBlank()) {
                    val addressSplit = fulladdress.split(",")
                }
            }

        sellOrExchange = intent.getStringExtra("sellOrExchange").toString()

        val categories = listOf("카테고리", "과일", "정육/계란", "채소", "유제품", "수산/건어물", "기타")
        categorySpinner = findViewById(R.id.categorySpinner)
        adapter = CustomSpinnerAdapter(this, categories)
        categorySpinner.adapter = adapter

        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val goods_id = intent.getStringExtra("goods_id")!!.toLong()
        sellOrExchange = intent.getStringExtra("sellOrExchange").toString()

        if (sellOrExchange.isNotBlank()) {
            if (sellOrExchange == "sell") {
                isGeneralSaleSelected = true
            } else if (sellOrExchange == "exchange") {
                isGeneralSaleSelected = false
            }
            updateButtonUI()
        }

        // 가져오기
        lifecycleScope.launch {

            if (isGeneralSaleSelected) {

                try {
                    val response = withContext(Dispatchers.IO) {
                        goodsAPI.getGoodsById(goods_id)
                    }
                    val category = response.categoryId.id
                    currentPrice = response.price
                    imgUrl = response.img_url
                    withContext(Dispatchers.Main) {

                        Glide.with(binding.sellImageView.context)
                            .load(response.img_url)
                            .placeholder(null)
                            .transform(RoundedCorners(18))
                            .into(binding.sellImageView)
                        binding.categorySpinner.setSelection(category.toInt())
                        binding.ExpirationDateInsert.setText(response.shelf_life)
                        binding.sellTitle.setText(response.title)
                        binding.productNameInsert.setText(response.name)
                        binding.productAmountInsert.setText(response.rest.toString())
                        binding.priceExchangeInsert.setText(response.price)
                        binding.productWeightInsert.setText(response.weight!!)
                    }
                } catch (e: Exception) {
                }

            } else {

                try {
                    val response = withContext(Dispatchers.IO) {
                        exchangesAPI.getGoodsById(goods_id)
                    }
                    val category = response.categoryId.toInt()
                    withContext(Dispatchers.Main) {

                        Glide.with(binding.sellImageView.context)
                            .load(response.imageUrl)
                            .placeholder(null)
                            .transform(RoundedCorners(18))
                            .into(binding.sellImageView)
                        binding.categorySpinner.setSelection(category.toInt())
                        binding.ExpirationDateInsert.setText(response.shelfLife)
                        binding.sellTitle.setText(response.title)
                        binding.productNameInsert.setText(response.name)
                        binding.productAmountInsert.setText(response.count.toString())
                        binding.priceExchangeInsert.setText(response.wantItem)
                        binding.productWeightInsert.setText(response.weight!!)
                    }
                } catch (e: Exception) {
                }
            }

        }
        // 수정하기
        binding.sellRegistorBtn.setOnClickListener {
            if (binding.sellTitle.text.isNullOrBlank() || binding.productNameInsert.text.isNullOrBlank() ||
                binding.productAmountInsert.text.isNullOrBlank() || binding.ExpirationDateInsert.text.isNullOrBlank()
                || binding.priceExchangeInsert.text.isNullOrBlank()
            ) {
                Toast.makeText(applicationContext, "빈 항복을 채워주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (categorySpinner.selectedItemPosition == 0) {
                Toast.makeText(applicationContext, "카테고리를 선택해 주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val title = binding.sellTitle.text.toString()
            val productName = binding.productNameInsert.text.toString()
            val productAmount = binding.productAmountInsert.text.toString().toIntOrNull()
            val productWeight = binding.productWeightInsert.text.toString()
            val expirationDate = binding.ExpirationDateInsert.text.toString()
            val categoryIdx = binding.categorySpinner.selectedItemPosition.toLong()
            var salePrice: Int? = null
            var salePercent: Int? = null
            var category = Category(
                id = categoryIdx,
                name = null
            )


            // val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            //val imageFileName = "User_${userIdx}_${timeStamp}"
            //Log.d("IMGGG", "$imgUri")

            if (isGeneralSaleSelected){

                var currentPriceInt = currentPrice.toInt()
                val modifyPriceInt = binding.priceExchangeInsert.text.toString().toInt()

                if (modifyPriceInt < currentPriceInt) {
                    salePrice = modifyPriceInt
                    val discountPrice = currentPriceInt - modifyPriceInt
                    salePercent = (100 * discountPrice.toLong() / currentPriceInt.toLong()).toInt()
                } else if (modifyPriceInt > currentPriceInt) {
                    currentPriceInt = modifyPriceInt
                }
                CoroutineScope(Dispatchers.IO).launch{
                    try {

                        //val imageFile = convertImageUriToPngFile(applicationContext, imageFileName)
                        Log.d("modifyyy", "$categoryIdx")
                        val partialGoods = PartialGoods(
                            title = title,
                            price = currentPriceInt.toString(),
                            weight = productWeight,
                            rest = productAmount,
                            shelfLife = expirationDate,
                            category = category,
                            sale_price = salePrice,
                            sale_percent = salePercent,
                            name = productName
                        )

                        //Log.d("modifyyy", "$imageFile")

                        val response = withContext(Dispatchers.IO) {
                            goodsAPI.update(goods_id, partialGoods)
                        }
                        Log.d("modifyyy", "${response.isSuccessful}")
                        // HTTP 상태 코드와 메시지 출력
                        Log.d("modifyyy", "Status Code: ${response.code()}")
                        Log.d("modifyyy", "Message: ${response.message()}")

                        if (!response.isSuccessful) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(applicationContext, "수정에 실패하였습니다.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {

                        }

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "상품 정보를 수정하였습니다!", Toast.LENGTH_SHORT)
                                .show()
                            finish()
                        }
                    }
                }
            } else{
                Log.d("에러 잡자", "교환 수정 눌렀네")
                val wantItem = binding.priceExchangeInsert.text

                CoroutineScope(Dispatchers.IO).launch{
                    try {
                        //val imageFile = convertImageUriToPngFile(applicationContext, imageFileName)
                        Log.d("에러 잡자", "코루틴 시작")
                        val partialExchanges = PartialExchanges(
                            title = title,
                            shelfLife = expirationDate,
                            weight = productWeight,
                            wantItem = wantItem.toString(),
                            categoryId = categoryIdx,
                            count = productAmount,
                            name = productName,
                            userId = UserManager.getUserIdx(application).toString().toLong()
                        )

                        //Log.d("modifyyy", "$imageFile")
                        Log.d("에러 잡자", "DTO 만들었음")
                        val response = withContext(Dispatchers.IO) {
                            Log.d("에러 잡자", "호출 전")
                            exchangesAPI.update(goods_id, partialExchanges)
                        }
                        Log.d("에러 잡자", "api 호출 완료")


                        if (response.isSuccessful) {
                            Log.d("에러 잡자", "if문")
                            withContext(Dispatchers.Main) {
                                Toast.makeText(applicationContext, "상품 정보를 수정하였습니다!", Toast.LENGTH_SHORT)
                                    .show()
                                finish()
                            }
                        } else {
                            Log.d("에러 잡자", "else문")

                        }

                    } catch (e: Exception) {
                        Log.d("에러 잡자", "catch문")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "상품 정보를 수정하였습니다!", Toast.LENGTH_SHORT)
                                .show()
                            finish()
                        }
                    }
                }
            }

        }

        initview()
    }

    private fun initview() {
        with(binding) {

            // 앞에 있는 화면 터치하면 없어지게 만들기
            frontShadowLayout.setOnClickListener {
                frontShadowLayout.visibility = View.GONE
            }

            // 갤러리 호출
//            sellImageView.setOnClickListener {
//                val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
//                selectImageResultLauncher.launch(intent)
//            }

            if (sellOrExchange == "sell"){
                isGeneralSaleSelected = true    // 일반 구매
                productAmountTv.text = "판매 갯수"
                productAmountInsert.hint = "판매 갯수를 입력하세요."
                val params = priceExchangeTv.layoutParams
                params.width = dpToPx(90)
                priceExchangeTv.layoutParams = params
                priceExchangeTv.text = "개당 가격"
                priceExchangeInsert.hint = "개당 판매 가격을 입력하세요."
                priceExchangeInsert.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL

            }else{
                isGeneralSaleSelected = false   // 교환하기
                productAmountTv.text = "교환 갯수"
                productAmountInsert.hint = "교환 갯수를 입력하세요."
                val params = priceExchangeTv.layoutParams
                params.width = dpToPx(150)
                priceExchangeTv.layoutParams = params
                priceExchangeTv.text = "원하는 교환 품목"
                priceExchangeInsert.hint = "ex. 당근"
                priceExchangeInsert.inputType = InputType.TYPE_CLASS_TEXT

            }

            updateButtonUI()

        }

        // 카테고리
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                adapter.setSelectedItemVisibility(position == 0)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

    }

    // 일반 판매, 교환하기 선택
    private fun updateButtonUI() {
        with(binding) {

            // 서버 넘겨받으면 찜한 목록 중에서 어떤 건지 판단해서 recyclerview 띄우게 하기
            if (isGeneralSaleSelected) {
                generalSale.background = ContextCompat.getDrawable(
                    this@SaleReviseRegistrationActivity,
                    R.drawable.green_left_round
                )
                generalSale.setTextColor(
                    ContextCompat.getColor(
                        this@SaleReviseRegistrationActivity,
                        R.color.white
                    )
                )
                Exchange.background = ContextCompat.getDrawable(
                    this@SaleReviseRegistrationActivity,
                    R.drawable.white_right_round
                )
                Exchange.setTextColor(
                    ContextCompat.getColor(
                        this@SaleReviseRegistrationActivity,
                        R.color.main
                    )
                )
            } else {
                generalSale.background = ContextCompat.getDrawable(
                    this@SaleReviseRegistrationActivity,
                    R.drawable.white_left_round
                )
                generalSale.setTextColor(
                    ContextCompat.getColor(
                        this@SaleReviseRegistrationActivity,
                        R.color.main
                    )
                )
                Exchange.background = ContextCompat.getDrawable(
                    this@SaleReviseRegistrationActivity,
                    R.drawable.green_right_round
                )
                Exchange.setTextColor(
                    ContextCompat.getColor(
                        this@SaleReviseRegistrationActivity,
                        R.color.white
                    )
                )
            }
        }
    }

    suspend fun convertImageUriToPngFile(context: Context, fileName: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(imgUri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val file = File(context.cacheDir, fileName)
                val outputStream = FileOutputStream(file)
                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
                file
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun Context.dpToPx(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

}