package com.yusuffahrudin.masuyamobileapp

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar

import com.yusuffahrudin.masuyamobileapp.util.Server
import com.yusuffahrudin.masuyamobileapp.util.SessionManager
import kotlinx.android.synthetic.main.activity_pdf.*

import java.util.HashMap
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.content.Context.PRINT_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.print.PrintManager
import android.support.design.widget.FloatingActionButton


class PDFActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var toolBar: Toolbar
    private lateinit var floatingActionButton: FloatingActionButton

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)
        title = "Laporan Penjualan Barang"
        toolBar = toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        webView = webview
        progressBar = progressbar
        floatingActionButton = fab_print

        sessionManager = SessionManager(applicationContext)
        val user = sessionManager.userDetails
        val kdkota = user.get(SessionManager.kdkota)

        val a = Server(kdkota)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + a.URL() + "fpdfcreatepdf/tes.php")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                progressBar.visibility = View.GONE
                //webView.loadUrl("javascript:(function() { " +
                        //"document.getElementsByClassName('ndfHFb-c4YZDc-GSQQnc-LgbsSe ndfHFb-c4YZDc-to915-LgbsSe VIpgJd-TzA9Ye-eEGnhe ndfHFb-c4YZDc-LgbsSe')[0].style.display='none'; })()")
            }
        }

        floatingActionButton.setOnClickListener{
            view -> printPDF(webView)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            this.finish()
        }

        return super.onOptionsItemSelected(item)
    }

    //create a function to create the print job
    private fun createWebPrintJob(view: WebView) {

        //create object of print manager in your device
        val printManager = this.getSystemService(Context.PRINT_SERVICE) as PrintManager

        //create object of print adapter
        val printAdapter = view.createPrintDocumentAdapter()

        //provide name to your newly generated pdf file
        val jobName = getString(R.string.app_name) + " Print Test"

        //open print dialog
        printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
    }

    //perform click pdf creation operation on click of print button click
    fun printPDF(view: WebView) {
        createWebPrintJob(view)
    }
}
