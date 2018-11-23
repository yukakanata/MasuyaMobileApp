package com.yusuffahrudin.masuyamobileapp

import android.app.AlertDialog
import android.app.DownloadManager
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.annotation.RequiresApi
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.Toast
import android.widget.ViewSwitcher
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.yusuffahrudin.masuyamobileapp.adapter.AdapterMain
import com.yusuffahrudin.masuyamobileapp.controller.AppController
import com.yusuffahrudin.masuyamobileapp.controller.DialogAlert
import com.yusuffahrudin.masuyamobileapp.customer.CustomerActivity
import com.yusuffahrudin.masuyamobileapp.data.ArrayTampung
import com.yusuffahrudin.masuyamobileapp.data.User
import com.yusuffahrudin.masuyamobileapp.history_pembelian.HistoryPembelianActivity
import com.yusuffahrudin.masuyamobileapp.history_penjualan.HistoryPenjualanActivity
import com.yusuffahrudin.masuyamobileapp.informasi_barang.ListBarangActivity
import com.yusuffahrudin.masuyamobileapp.sales_order.SalesOrderActivity
import com.yusuffahrudin.masuyamobileapp.stock_opname.StockOpnameActivity
import com.yusuffahrudin.masuyamobileapp.update_pricelist.UpdatePricelistActivity
import com.yusuffahrudin.masuyamobileapp.user_manage.UserManageActivity
import com.yusuffahrudin.masuyamobileapp.util.Server
import com.yusuffahrudin.masuyamobileapp.util.SessionManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*

class HomeActivity: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var sessionManager: SessionManager
    private lateinit var level: String
    private lateinit var kdkota:String
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapterMain: AdapterMain
    private var listAkses: MutableList<User> = ArrayTampung.getListAkses()
    private var broadcastReceiver: BroadcastReceiver? = null
    private lateinit var downloadManager: DownloadManager
    private lateinit var timer: Timer
    private var position: Int = 0
    private val DURATION = 5000
    private val defValue = -1
    private lateinit var url_akses: String
    private lateinit var url_select_version: String
    private val TAG = HomeActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        title = "Masuya Mobile App v" + getApplicationVersionName()
        sessionManager = SessionManager(this)
        val user = sessionManager.userDetails
        level = user[SessionManager.level].toString()
        kdkota = user[SessionManager.kdkota].toString()
        cekPermission()
        //cekVersion()
        cekAkses()

        linearLayoutManager = GridLayoutManager(this, 2) as LinearLayoutManager
        rv_main.setHasFixedSize(true)
        rv_main.layoutManager = linearLayoutManager
        adapterMain = AdapterMain(this, resources.getStringArray(R.array.gv_main_text), resources.obtainTypedArray(R.array.gv_main_img))
        rv_main.adapter = adapterMain

        //========================== set title show if collapsed ===================================
        app_bar_layout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1
            override fun onOffsetChanged(app_bar_layout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = app_bar_layout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsing_toolbar.title = "Masuya Sales App"
                    isShow = true
                } else if (isShow) {
                    collapsing_toolbar.title = " "//carefull there should a space between double quote otherwise it wont work
                    isShow = false
                }
            }
        })

        //============================= Set image slide ============================================
        img_switch.setFactory {
            val imageView = ImageView(applicationContext)
            imageView.scaleType = ImageView.ScaleType.FIT_XY
            imageView
        }

        createNotif()

        //============================= Set animations =============================================
        // https://danielme.com/2013/08/18/diseno-android-transiciones-entre-activities/
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        img_switch.inAnimation = fadeIn
        img_switch.outAnimation = fadeOut
        position = 0
        startSlider()

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.setDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    //Programmatically get the current version Name
    private fun getApplicationVersionName(): String {

        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            return packageInfo.versionName
        } catch (ignored: Exception) {
        }

        return ""
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
                .setMessage("Yakin ingin keluar?")
                .setCancelable(false)
                .setPositiveButton("Ya") { _, _ ->
                    listAkses.clear()
                    if (broadcastReceiver != null) {
                        this.unregisterReceiver(broadcastReceiver)
                    }
                    broadcastReceiver = null
                    this.finish()
                }
                .setNegativeButton("Tidak", null)
                .show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        val title = item.title.toString()
        //var cek = false

        for (i in listAkses.indices) {
            val modul = listAkses.get(i).modul

            if (modul.equals(title, ignoreCase = true) && listAkses.get(i).isAkses) {
                when(id){
                    R.id.nav_informasi_barang -> startActivity<PDFActivity>()
                    R.id.nav_mycustomer -> startActivity<CustomerActivity>()
                    R.id.nav_history_penjualan -> startActivity<HistoryPenjualanActivity>()
                    R.id.nav_history_pembelian -> startActivity<HistoryPembelianActivity>()
                    R.id.nav_update_pricelist -> startActivity<UpdatePricelistActivity>()
                    R.id.nav_sales_order -> longToast("Sedang dalam proses development")//startActivity<SalesOrderActivity>()
                    R.id.nav_stock_opname -> {
                        val listData = ArrayTampung.getListOpname()
                        listData.clear()
                        startActivity<StockOpnameActivity>()
                    }
                    R.id.nav_user_management -> startActivity<UserManageActivity>()
                }
                //cek = true
            }
        }

        when(id){
            R.id.nav_logout -> {
                sessionManager.logout()
                finish()
                return true
            }
            R.id.nav_exit -> {
                listAkses.clear()
                if (broadcastReceiver != null) {
                    this.unregisterReceiver(broadcastReceiver)
                }
                broadcastReceiver = null
                this.finish()
            }
        }

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun startSlider() {
        val gallery = resources.obtainTypedArray(R.array.gallery)
        timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                // avoid exception:
                // "Only the original thread that created a view hierarchy can touch its views"
                runOnUiThread {
                    img_switch.setImageResource(gallery.getResourceId(position, defValue))
                    position++
                    if (position == gallery.length()) {
                        position = 0
                    }
                }
            }
        }, 0, DURATION.toLong())
    }

    private fun createNotif() {
        val intent = Intent(this, ListBarangActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.masuyalogo)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
    }

    private fun cekAkses() {
        listAkses.clear()
        val a = Server(kdkota)
        url_akses = a.URL() + "tools/cek_akses.php"
        val strReq = object : StringRequest(Request.Method.POST, url_akses, Response.Listener { response ->
            Log.d(TAG, "Response : " + response.toString())

            try {
                val jsonObject = JSONObject(response)
                val result = jsonObject.getJSONArray("result")
                for (i in 0 until result.length()) {
                    try {
                        val obj = result.getJSONObject(i)

                        val item = User()

                        item.modul = obj.getString("Modul")

                        item.isAkses = obj.getInt("Akses") == 1

                        item.isAdd = obj.getInt("Add") == 1

                        item.isEdit = obj.getInt("Edit") == 1

                        item.isDelete = obj.getInt("Delete") == 1

                        item.isPost = obj.getInt("Post") == 1

                        //menambah item ke array
                        listAkses.add(item)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { error -> DialogAlert(error.message, "error", this) }) {

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                //Posting parameter ke post url
                val params = HashMap<String, String>()
                params["level"] = level

                return params
            }
        }
        AppController.getInstance().addToRequestQueue(strReq, resources.getString(R.string.tag_json_obj))
    }

    private fun cekVersion() {
        val a: Server
        a = Server(kdkota)
        url_select_version = a.URL() + "tools/select_version.php"

        val strReq = StringRequest(Request.Method.POST, url_select_version, Response.Listener { response ->
            try {
                Log.v(TAG, "Response : " + response.toString())
                val jsonObject = JSONObject(response)
                val result = jsonObject.getJSONArray("result")
                for (i in 0 until result.length()) {
                    try {
                        val obj = result.getJSONObject(i)

                        val packageInfo = packageManager.getPackageInfo(packageName, 0)
                        val versi = obj.getString("VersionMajor") + "." + obj.getString("VersionMinor") + "." + obj.getString("VersionBuild")
                        if (!packageInfo.versionName.equals(versi, ignoreCase = true)) {
                            AlertDialog.Builder(this)
                                    .setMessage("Update version available, download it now?")
                                    .setCancelable(false)
                                    .setPositiveButton("Ya") { _, _ ->
                                        println("================= diupdate ")
                                        val apk = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/masuya.apk")
                                        if (apk.exists()) {
                                            apk.delete()
                                            downloadAPK(a, apk)
                                            println("================= APK is exist dan didelete ")
                                        } else {
                                            println("================= APK didownload ")
                                            downloadAPK(a, apk)
                                        }
                                    }
                                    .setNegativeButton("Tidak") { _, _ ->
                                        listAkses.clear()
                                        this.finish()
                                    }
                                    .show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
            } catch (ignored: Exception) {

            }
        }, Response.ErrorListener { error ->
            Log.e(TAG, "Error cek Version : " + error.message)
            DialogAlert(error.message, "error", this)
        })

        AppController.getInstance().addToRequestQueue(strReq, resources.getString(R.string.tag_json_obj))
    }

    private fun downloadAPK(a: Server, apk: File) {
        downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(a.URL_APK())
        val request = DownloadManager.Request(uri)
        request.setTitle("Download APK Masuya Mobile")
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "masuya.apk")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        //val reference = downloadManager.enqueue(request)

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                var intentDownload = intent
                val action = intentDownload.action
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                    Toast.makeText(applicationContext, "Download Completed", Toast.LENGTH_LONG).show()
                    if (apk.exists()) {
                        //sessionManager.logout();
                        intentDownload = Intent(Intent.ACTION_VIEW, Uri.fromFile(apk))
                        intentDownload.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                        intentDownload.setDataAndType(Uri.fromFile(apk),
                                "application/vnd.android.package-archive")
                        intentDownload.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        intentDownload.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        startActivity(intentDownload)
                    } else {
                        Log.e(TAG, "Error install APK, File tidak ditemukan")
                        DialogAlert("Error install APK, File tidak ditemukan", "error", this@HomeActivity)
                    }


                }
            }
        }
        registerReceiver(broadcastReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private fun cekPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission is granted")
        } else {
            Log.v(TAG, "Permission is not granted")
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
    }
}