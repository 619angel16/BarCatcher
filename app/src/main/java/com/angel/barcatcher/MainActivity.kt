package com.angel.barcatcher

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.angel.barcatcher.ui.theme.BarCatcherTheme
import net.ravendb.client.documents.DocumentStore
import net.ravendb.client.documents.IDocumentStore
import java.io.FileInputStream
import java.security.KeyStore
import java.time.Instant
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DocumentStore("http://live-test.ravendb.net", "Demo").initialize().use { store ->
            for (i in 0..1000) {
                store.openSession().use { session ->
                    val insert = CafeBar(
                        name = "Casa Paco",
                        tel = "123546",
                        streetAddress = "Av. Virgen de la Montaña, nº 26",
                        addressLocality = "Cáceres",
                        postalCode = 10002,
                        addressCountry = "ES",
                        geolong = null,
                        url = null,
                        geolat = null,
                        capacity = null,
                        email = null,
                        customId = "Cafebar/619"
                    )
                    session.store(insert)
                    val d = 7.0
                    session.timeSeriesFor(insert, "ReminderSeverity").append(
                        Date.from(Instant.now().plusSeconds(60)), d
                    )
                    session.saveChanges()
                }
            }
        }
        /*val session = DocumentStoreHolder.createStore(this).openSession()
        try {
            val insert = CafeBar(
                "Casa Paco",
                tel = "123546",
                streetAddress = "Av. Virgen de la Montaña, nº 26",
                addressLocality = "Cáceres",
                postalCode = 10002,
                addressCountry = "ES",
                geolong = null,
                url = null,
                geolat = null,
                capacity = null,
                email = null
            )
            session.store(insert, "Cafebar/619")
            //session.saveChanges()
        } finally {
            session.close()
        }*/
        enableEdgeToEdge()
        setContent {
            BarCatcherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(255, 0, 255, 100))
    ) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BarCatcherTheme {
        Greeting("Android")
    }
}

object DocumentStoreHolder {
    //    private var CERTIFICATE_PATH = ""
    private val CERTIFICATE_PASSWORD = "apk1234".toCharArray()

    fun createStore(context: Context): IDocumentStore {
        val documentStore = DocumentStore().apply {
            urls = arrayOf("https://a.free.apeaorre.ravendb.cloud")
            database = "PIM_Testing"
            certificate = loadCertificateFromPfx(CERTIFICATE_PASSWORD, context)
        }
        return documentStore.initialize()
    }
}

private fun loadCertificateFromPfx(pfxPassword: CharArray, context: Context): KeyStore {
    val keyStore = KeyStore.getInstance("PKCS12")
    context.resources.openRawResource(R.raw.apk).use { inputStream ->
        keyStore.load(inputStream, pfxPassword)
    }
    return keyStore
}
