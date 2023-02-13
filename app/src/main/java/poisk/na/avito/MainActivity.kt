package poisk.na.avito

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity



class MainActivity : AppCompatActivity() {
    private var webView: CustomWebView? = null
    private var winManager: WindowManager? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                !Settings.canDrawOverlays(this)
            } else {
                TODO("VERSION.SDK_INT < M")
            }
        ) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, 0)
        } else {
            winManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

            webView = CustomWebView(this)
            webView!!.settings.javaScriptEnabled = true
            webView!!.isVerticalScrollBarEnabled = false
            webView!!.settings.domStorageEnabled = true;
            webView!!.settings.databaseEnabled = true;
            webView!!.addJavascriptInterface(AndroidInput(webView!!), "AndroidInput")
            val typeOverlay = if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                typeOverlay,
                //WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
            params.x = 0
            params.y = 0
            params.width = 0
            params.height = 0

            webView!!.webViewClient = client

            //val frame = FrameLayout(this)
            //frame.addView(webView)
            // winManager!!.addView(frame, params)


            setContentView(webView!!)
            WebView.setWebContentsDebuggingEnabled(true);

            webView!!.loadUrl("https://m.avito.ru/profile")


            //setContentView(R.layout.activity_main)
        }
    }


    private val client: WebViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            val p = Point()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val wMetrics = winManager!!.currentWindowMetrics
                val rect = wMetrics.bounds
                p.x = rect.width()
                p.y = rect.height()
            } else {
                winManager!!.defaultDisplay.getSize(p)
            }
            webView!!.measure(
                View.MeasureSpec.makeMeasureSpec(
                    p.x,
                    View.MeasureSpec.EXACTLY
                ),
                View.MeasureSpec.makeMeasureSpec(
                    p.y,
                    View.MeasureSpec.EXACTLY
                )
            )

            webView!!.layout(0, 0, webView!!.measuredWidth, webView!!.measuredHeight)
            if (webView!!.progress == 100) // prevent double running same code
                view.loadUrl(JSCODE.code)
        }
    }
}

class AndroidInput(private var webView: CustomWebView) {

    @JavascriptInterface
    fun sendTextInput(text: String) {
        webView.sendTextInput(text)
    }
}