package me.rohanjahagirdar.healthapp

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.zxing.Result

import kotlinx.android.synthetic.main.activity_main.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import me.rohanjahagirdar.healthapp.Networking.OkHttpRequest
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
    lateinit var mScannerView: ZXingScannerView
    var paused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setSupportActionBar(toolbar)
        mScannerView = ZXingScannerView(this)
        setContentView(mScannerView)

//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }
    }


    override fun onResume() {
        super.onResume()
        mScannerView.setResultHandler(this)
        mScannerView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        mScannerView.stopCamera()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun handleResult(result: Result?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        println(result?.text)

        println(result?.barcodeFormat.toString())


        var client = OkHttpClient()
        var request= OkHttpRequest(client)
        val url = "https://world.openfoodfacts.org/api/v0/product/" + result?.text+ ".json"

        request.GET(url, object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {

            }

            override fun onResponse(call: Call?, response: Response) {
                val responseData = response.body()?.string()
                runOnUiThread {
                    try {
                        var json = JSONObject(responseData)
                        println("SUCCESS - " + json)

                        if(json.getInt("status") == 1) {
                            Toast.makeText(this@MainActivity, json.getJSONObject("product").getString("product_name"), Toast.LENGTH_SHORT).show()
                            Toast.makeText(this@MainActivity, json.getJSONObject("product").getString("nutriments"), Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this@MainActivity, "Not Found!", Toast.LENGTH_SHORT).show()
                        }


                    } catch (e: JSONException) {
                        e.printStackTrace()

                    }
                }
            }
        })
    }
}
