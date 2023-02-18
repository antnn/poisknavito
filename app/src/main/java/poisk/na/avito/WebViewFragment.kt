package poisk.na.avito


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import java.util.stream.Collectors


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private val ua = "Mozilla/5.0 (Linux; Android " +
        android.os.Build.VERSION.RELEASE + getDeviceName() +
        ") AppleWebKit/537.36 " +
        "(KHTML, like Gecko) Chrome/110.0.0.0 Mobile Safari/537.36"

/**
 * A simple [Fragment] subclass.
 * Use the [WebViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WebViewFragment : Fragment() {
    private var webView: CustomWebView? = null
    private var winManager: WindowManager? = null

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentRootView = inflater.inflate(R.layout.fragment_web_view, container, false)
        webView = fragmentRootView.findViewById(R.id.mainWebView) as CustomWebView?
        _setupWebView()
        if (savedInstanceState != null)
            webView!!.restoreState(savedInstanceState.getBundle("webViewState")!!);
        else
            webView!!.loadUrl("https://m.avito.ru/profile")

        winManager = requireActivity().getSystemService( Context.WINDOW_SERVICE) as WindowManager
        return fragmentRootView
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val bundle = Bundle()
        webView!!.saveState(bundle)
        outState.putBundle("webViewState", bundle)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WebViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WebViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
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
            if (webView!!.progress == 100) {
                // prevent double running the same code
                view.loadUrl("javascript:" + webView!!.jsCode)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun _setupWebView() {
        /*if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
            winManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager }*/

        webView!!.settings.javaScriptEnabled = true
        webView!!.isVerticalScrollBarEnabled = false
        webView!!.settings.domStorageEnabled = true;
        webView!!.settings.databaseEnabled = true;
        webView!!.settings.userAgentString = ua;
        webView!!.addJavascriptInterface(AndroidInput(webView!!), "AndroidInput")
        WebView.setWebContentsDebuggingEnabled(true);
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

        val inputStream = requireContext().assets.open(
            "main.js"
        )
        webView!!.jsCode = BufferedReader(InputStreamReader(inputStream))
            .lines().parallel().collect(Collectors.joining("\n"))

    }

}

class AndroidInput(private var webView: CustomWebView) {

    @JavascriptInterface
    fun sendTextInput(text: String) {
        webView.sendTextInput(text)
    }
}


fun getDeviceName(): String? {
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    return if (model.lowercase(Locale.getDefault())
            .startsWith(manufacturer.lowercase(Locale.getDefault()))
    ) {
        capitalize(model)
    } else {
        capitalize(manufacturer) + " " + model
    }
}


private fun capitalize(s: String?): String {
    if (s == null || s.isEmpty()) {
        return ""
    }
    val first = s[0]
    return if (Character.isUpperCase(first)) {
        s
    } else {
        first.uppercaseChar().toString() + s.substring(1)
    }
}