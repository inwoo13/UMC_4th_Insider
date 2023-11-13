package com.umc.insider.auth.signUp

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.umc.insider.R
import com.umc.insider.databinding.ActivityAddressBinding

class AddressActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.VISIBLE

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.addJavascriptInterface(BridgeInterface(), "Android")

        binding.webView.webViewClient = object: WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                // Show progress bar
                binding.progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String) {
                // Android -> JavaScript
                binding.logo.visibility = View.VISIBLE
                binding.webView.loadUrl("javascript:sample2_execDaumPostcode();")
                binding.progressBar.visibility = View.GONE
            }
        }
        // 최소 웹뷰 로드
        binding.webView.loadUrl("insider-392003.web.app")
    }

    private inner class BridgeInterface {
        @JavascriptInterface
        fun processDATA(data : String) {
            // 카카오 주소 검색 API 결과 값이 브릿지 통로를 통해 전달 받음 - from Javascript
            val intent = Intent()
            intent.putExtra("data", data)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}