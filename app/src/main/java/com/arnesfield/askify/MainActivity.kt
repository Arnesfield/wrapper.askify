package com.arnesfield.askify

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.annotation.SuppressLint
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.JavascriptInterface
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val DEV = true
    private val TAG = "tagx"
    private val URL =
        if (DEV) "http://192.168.43.115/school/askify/web.askify/dist"
        else "http://askify.x10.mx/"

    private lateinit var webView: WebView
    private lateinit var textView: TextView
    private lateinit var button: Button
    private lateinit var refreshBlock: View
    private var flag: Int = 0

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.webView = findViewById(R.id.webview)
        this.textView = findViewById(R.id.textView)
        this.button = findViewById(R.id.button)
        this.refreshBlock = findViewById(R.id.refreshBlock)

        flag = View.GONE

        button.setOnClickListener {
            load()
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.addJavascriptInterface(this, "android")

        val activity = this
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 1000)
            }
        }
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                Log.d(TAG, "2")
                refreshBlock.visibility = if (flag == View.GONE) View.VISIBLE else flag
                webView.visibility = flag
            }

            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                flag = View.GONE
                Log.d(TAG, "1")
                Toast.makeText(activity, "Oh no! $description", Toast.LENGTH_SHORT).show()
            }
        }

        this.load()
    }

    private fun load() {
        flag = View.VISIBLE
        Log.d(TAG, URL)
        webView.loadUrl(URL)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    webView.loadUrl("javascript:android.back(\$mobile.back())")
                    //webView.loadUrl("javascript:\$mobile.back()")
                    return true
                }
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    @JavascriptInterface
    public fun back(value: Boolean) {
        Log.d(TAG, value.toString())
        if (!value) {
            finish()
        }
    }

}
