package com.umc.insider

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.user.UserApiClient
import com.umc.insider.auth.TokenManager
import com.umc.insider.auth.UserManager
import com.umc.insider.auth.login.LogInActivity
import com.umc.insider.auth.signUp.AddressActivity
import com.umc.insider.databinding.ActivityEditProfileBinding
import com.umc.insider.retrofit.RegisterNumCheckInstance
import com.umc.insider.retrofit.RetrofitInstance
import com.umc.insider.retrofit.api.RegisterNumCheckInterface
import com.umc.insider.retrofit.api.UserInterface
import com.umc.insider.retrofit.model.UserPatchReq
import com.umc.insider.retrofit.model.UserPutProfileReq
import com.umc.insider.retrofit.model.UserPutReq
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    private lateinit var getSearchResult : ActivityResultLauncher<Intent>

    private val registerNumCheckAPI = RegisterNumCheckInstance.getInstance().create(
        RegisterNumCheckInterface::class.java)
    private var registerNum : Long? = null

    private var imgUri : Uri? = null
    private var password : String = ""
    private var flag = false
    private var isSeller = false


    private val selectImageResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imgUri = result.data?.data
            Glide.with(this)
                .load(imgUri)
                .into(binding.profileImg)
        }
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)

        binding.lifecycleOwner = this

        getSearchResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            val fulladdress = it.data?.getStringExtra("data")
            if (!fulladdress.isNullOrBlank()){
                val addressSplit = fulladdress.split(",")
                binding.editaddressText.text = addressSplit[0]
                binding.editaddressNum.text = addressSplit[1]
            }
        }

        val userIdx = UserManager.getUserIdx(applicationContext)!!.toLong()
        Log.d("EDITTT", "userIdx : {$userIdx}")
        val UserApi = RetrofitInstance.getInstance().create(UserInterface::class.java)

        // 정보 가져오기
        lifecycleScope.launch{
            try {
                val response = withContext(Dispatchers.IO){
                    UserApi.getUserById(userIdx)
                }
                Log.d("EDITTT", "$response")

                withContext(Dispatchers.Main){
                    binding.nicknameTV.setText(response.nickname)
                    binding.idTextView.text = response.userId
                    binding.emailTextView.setText(response.email)
                    binding.editaddressText.text = response.detailAddress
                    binding.editaddressNum.setText(response.zipCode.toString())
                    password = response.pw
                    Log.d("EDITTT", "password : $password")

                    if(response.sellerOrBuyer == 1){
                        binding.registerNumCheck.text = "인증 완료"
                        binding.registerNumCheck.setBackgroundColor(ContextCompat.getColor(this@EditProfileActivity, R.color.lightMain))
                        Log.d("EDITTT", "registerNum : ${response.registerNum}")
                        binding.registerTextView.setText(response.registerNum.toString())
                        isSeller = true
                    }else{

                    }

                    if(response.img != null) {
                        Glide.with(binding.profileImg.context)
                            .load(response.img)
                            .placeholder(android.R.color.transparent)
                            .into(binding.profileImg)
                    } else {

                    }
                }

            }catch( e : Exception){
                Log.e("EDITTT", "$e")
            }
        }

        // 사업자 등록 -> 버튼을 누를 경우 isSeller를 true로 변경하고 true일 경우에 registerNum update
        // 5683801056
        binding.registerNumCheck.setOnClickListener {
            if (binding.registerTextView.text.length != 10){
                Toast.makeText(this@EditProfileActivity, "사업자 번호 형식을 입력해 주세요",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val registerNumCheckInstance = lifecycleScope.launch {

                try {

                    val response = withContext(Dispatchers.IO) {
                        registerNumCheckAPI.fetchData(
                            key = getString(R.string.register_num_check_key),
                            query = binding.registerTextView.text.toString()
                        )
                    }

                    if (response.isSuccessful) {
                        val result = response.body()!!.items[0]
                        registerNum = binding.registerTextView.text.toString().toLong()
                        if (result.bstt == "계속사업자"){
                            isSeller = true
                            Toast.makeText(this@EditProfileActivity, "사업자 인증되었습니다.", Toast.LENGTH_SHORT).show()
                            binding.registerTextView.setCompoundDrawablesWithIntrinsicBounds(
                                null, null,
                                AppCompatResources.getDrawable(it.context, R.drawable.baseline_check_24), null
                            )
                            UserManager.setUserSellerOrBuyer(applicationContext,1)

                        }else{
                            Toast.makeText(this@EditProfileActivity, "사업자 번호가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                        }

                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e("REGISTERRR", e.message.toString())
                    }
                }
            }



            Log.d("REGISTERRR", "lifecycle 돌입 전")
            lifecycleScope.launchWhenStarted{
                registerNumCheckInstance.join()

                Log.d("REGISTERRR", "조건문 돌입 전")
                if(isSeller){
                    Log.d("REGISTERRR", "조건문 돌입 후")

                    val patchUserReq = UserPatchReq(
                        id = userIdx,
                        registerNum = registerNum
                    )

                    Log.d("REGISTERRR", "코루틴 들어옴")

                    try{
                        val response = UserApi.patchTransfer(patchUserReq)
                        Log.d("REGISTERRR", "진짜 끝일걸?")
                        if(response.sellerOrBuyer == 1)
                            Log.d("REGISTERRR", "성공!! ${response.registerNumber}")

                    }catch(e:Exception){
                        Log.e("REGISTERRR", "$e")
                    }
                }else{
                    Log.d("REGISTERRR", "변경할 게 없음")
                }
            }
        }




        // 정보 저장
        binding.infoSaveBtn.setOnClickListener {

            Log.d("EDITTT", "확인 전")

            if(binding.nicknameTV.text.isNullOrBlank() || binding.emailTextView.text.isNullOrBlank()){
                Toast.makeText(this@EditProfileActivity, "빈 칸을 채워주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("EDITTT", "데이터 넣기 전")

            val putUserReq = UserPutReq(
                id = userIdx,
                userId = binding.idTextView.text.toString(),
                nickname = binding.nicknameTV.text.toString(),
                pw = password,
                email = binding.emailTextView.text.toString(),
                zipCode = binding.editaddressNum.text.toString().toInt(),
                detailAddress = binding.editaddressText.text.toString()
            )

            Log.d("EDITTT", "성공 직전")

            val editUserJob = lifecycleScope.launch{
                try{
                    val response = UserApi.modifyUser(putUserReq)
                    Log.d("EDITTT", "${response.isSuccess}")

                }catch(e:Exception){
                    Log.e("EDITTT", "$e")
                }
            }

            // 이미지 수정
            val imageUploadJob = lifecycleScope.launch {
                val putUserProfileReq = UserPutProfileReq(id = userIdx)

                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val imageFileName = "User_${userIdx}_${timeStamp}"

                if (imgUri != null) {
                    val imageFile = convertImageUriToPngFile(applicationContext, imageFileName)
                    val imageRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile!!)
                    val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, imageRequestBody)
                    try {
                        val response = UserApi.registerProfile(putUserProfileReq, imagePart)
                        Log.d("EDITTT", "${response.isSuccess}")

                    } catch (e: Exception) {
                        Log.e("EDITTT", "$e")
                        flag = true
                    }
                } else {
                    Log.d("EDITTT", "이미지 파일이 존재하지 않습니다.")
                }

            }

            lifecycleScope.launch {
                imageUploadJob.join()
                editUserJob.join()

                if(flag){
                    Toast.makeText(applicationContext, "이미지가 너무 큽니다!", Toast.LENGTH_SHORT).show()
                    flag = false
                }else{
                    Toast.makeText(applicationContext, "수정을 완료하였습니다!", Toast.LENGTH_SHORT).show()
                    finish()
                }

            }


        }

        initview()

    }

    private fun initview(){
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        with(binding){

            if(nicknameTV.text.isNullOrBlank())
                nicknameTV.textSize = 14F
            else
                nicknameTV.textSize = 18F

            // 갤러리 호출
            bringImage.setOnClickListener{
                val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
                selectImageResultLauncher.launch(intent)
            }

            // 주소 수정
            addressFindBtn.setOnClickListener {
                val intent = Intent(this@EditProfileActivity, AddressActivity::class.java)
                getSearchResult.launch(intent)
            }

            // 로그아웃
            logoutTextView.setOnClickListener {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                val googleSignInClient = GoogleSignIn.getClient(this@EditProfileActivity, gso)
                googleSignInClient.signOut()
                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        //Toast.makeText(this, "로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
                        return@logout
                    }else {
                        //Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                    }
                }
                TokenManager.clearToken(this@EditProfileActivity)
                UserManager.clearUserIdx(this@EditProfileActivity)
                startActivity(Intent(this@EditProfileActivity, LogInActivity::class.java))
                finish()
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

    override fun onDestroy() {
        super.onDestroy()

    }

}