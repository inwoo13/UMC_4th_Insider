package com.umc.insider

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.umc.insider.auth.login.LogInActivity
import com.umc.insider.databinding.ActivityMainBinding
import com.umc.insider.fragments.HomeFragment
import com.umc.insider.fragments.MyPageFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val homeFragment = HomeFragment()
    private val myPageFragment = MyPageFragment()

    override fun onBackPressed() {
        // 바텀 네비게이션의 선택된 아이템이 홈(또는 첫번째)이 아니라면 홈으로 이동
        if (binding.bottomNav.selectedItemId != R.id.home) {
            binding.bottomNav.selectedItemId = R.id.home
        } else {
            super.onBackPressed()  // 홈에 있을 경우 기본 뒤로 가기 동작 실행
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        replaceFragment(homeFragment)
        initView()

    }

    private fun initView(){
        with(binding){
            bottomNav.setOnItemSelectedListener {
                when(it.itemId){
                    R.id.home -> {
                        replaceFragment(homeFragment)
                        return@setOnItemSelectedListener true
                    }
                    R.id.favorite -> {
                        //replaceFragment(cameraFragment)
                        return@setOnItemSelectedListener true
                    }
                    R.id.trade -> {
                        //replaceFragment(homeFragment)
                        return@setOnItemSelectedListener true
                    }
                    R.id.mypage -> {
                        replaceFragment(myPageFragment)
                        return@setOnItemSelectedListener true
                    }
                    else -> return@setOnItemSelectedListener false
                }

            }

        }
    }
    private fun replaceFragment(fragment : Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, fragment)
            commit()
        }
    }
}