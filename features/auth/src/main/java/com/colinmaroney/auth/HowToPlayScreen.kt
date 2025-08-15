package com.colinmaroney.auth

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.InputStream

private const val WEBVIEW_URL = "https://doggoes.maroneyhere.net/#/rules"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HowToPlayScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val myWebViewClient = object: WebViewClient() {

        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            request?.url?.path?.let { path ->
                if (path.endsWith("main.html")) {
                    scope.launch(Dispatchers.Main) {
                        onNavigateBack()
                    }
                    return WebResourceResponse("text/html","UTF-8", ByteArrayInputStream("".toByteArray()))
                }
            }
            return super.shouldInterceptRequest(view, request)
        }
    }
    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
                )
            webViewClient = myWebViewClient
            settings.javaScriptEnabled = true
            settings.setSupportMultipleWindows(false)
            settings.domStorageEnabled = false
        }
    }

    AndroidView(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .fillMaxSize(),
        factory =  { webView },
        update = { it.loadUrl(WEBVIEW_URL) }
    )
}