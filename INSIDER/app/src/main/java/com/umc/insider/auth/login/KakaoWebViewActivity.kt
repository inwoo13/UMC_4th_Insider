package com.umc.insider.auth.login

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.umc.insider.R
import com.umc.insider.databinding.ActivityKakaoWebViewBinding

class KakaoWebViewActivity : AppCompatActivity() {

    private lateinit var binding : ActivityKakaoWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_kakao_web_view)

        binding.webview.settings.javaScriptEnabled = true

        val client_id = getString(R.string.client_id)
        val redirect_uri = getString(R.string.redirect_uri)
        val response_type = getString(R.string.response_type)
        val client_secret = getString(R.string.client_secret)

        binding.webview.webViewClient = object  : WebViewClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                if (request?.url.toString().startsWith("http://localhost:8080/oauth2/callback/kakao")){
                    val code = Uri.parse(request!!.url.toString()).getQueryParameter("code")
                    val resultIntent = Intent()
                    resultIntent.putExtra("code", code)
                    setResult(101, resultIntent)
                    finish()
                    return true
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }

        binding.webview.loadUrl("https://kauth.kakao.com/oauth/authorize?client_id=$client_id&redirect_uri=$redirect_uri&response_type=$response_type&client_secret=$client_secret")

    }
}