package com.sitepopblocker

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import java.io.ByteArrayInputStream

class MainActivity : android.app.Activity() {
    private lateinit var browser: WebView
    private lateinit var address: EditText
    private lateinit var progress: ProgressBar
    private val prefs by lazy { getSharedPreferences("settings", MODE_PRIVATE) }
    private val blockedHosts = setOf(
        "doubleclick.net", "googlesyndication.com", "googleadservices.com",
        "adnxs.com", "adsrvr.org", "taboola.com", "outbrain.com", "popads.net"
    )

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(16, 20, 25))
        }
        val controls = LinearLayout(this).apply {
            gravity = Gravity.CENTER_VERTICAL
            setPadding(12, 10, 12, 10)
        }
        address = EditText(this).apply {
            hint = "https://example.com"
            setSingleLine(true)
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_URI
            setText(prefs.getString("lastUrl", "https://example.com"))
        }
        val go = Button(this).apply { text = "Abrir"; setOnClickListener { openAddress() } }
        val reload = Button(this).apply { text = "↻"; contentDescription = "Recarregar"; setOnClickListener { browser.reload() } }
        controls.addView(address, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        controls.addView(go)
        controls.addView(reload)
        progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply { max = 100 }
        browser = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = true
            settings.setSupportMultipleWindows(false)
            settings.javaScriptCanOpenWindowsAutomatically = false
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            webChromeClient = object : WebChromeClient() {
                override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, result: android.os.Message?): Boolean {
                    Toast.makeText(this@MainActivity, "Janela emergente bloqueada", Toast.LENGTH_SHORT).show()
                    return false
                }
                override fun onProgressChanged(view: WebView?, percent: Int) { progress.progress = percent }
            }
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean = !request.isForMainFrame
                override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                    val host = request.url.host?.lowercase() ?: return null
                    return if (blockedHosts.any { host == it || host.endsWith(".$it") })
                        WebResourceResponse("text/plain", "utf-8", ByteArrayInputStream(ByteArray(0)))
                    else null
                }
                override fun onPageFinished(view: WebView, url: String) {
                    address.setText(url)
                    prefs.edit().putString("lastUrl", url).apply()
                    view.evaluateJavascript("window.open=function(){return null};") { }
                }
            }
        }
        root.addView(controls)
        root.addView(progress, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5))
        root.addView(browser, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        setContentView(root)
        browser.loadUrl(address.text.toString())
    }

    private fun openAddress() {
        var url = address.text.toString().trim()
        if (url.isEmpty()) return
        if (Uri.parse(url).scheme == null) url = "https://$url"
        browser.loadUrl(url)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && browser.canGoBack()) { browser.goBack(); return true }
        if (keyCode == KeyEvent.KEYCODE_ENTER && address.hasFocus()) { openAddress(); return true }
        return super.onKeyDown(keyCode, event)
    }
    override fun onDestroy() { browser.destroy(); super.onDestroy() }
}
