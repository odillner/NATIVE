package odillner.thesis

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import odillner.thesis.utils.DataHelper
import odillner.thesis.utils.FullSuite
import odillner.thesis.utils.RunResult
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security


class MainActivity : Activity() {
    private lateinit var displayText: TextView
    private lateinit var encryptButton: Button
    private lateinit var runPicker: NumberPicker
    private lateinit var sizePicker: NumberPicker
    private lateinit var threadPicker: NumberPicker
    private lateinit var algorithmPicker: NumberPicker

    private var dataSetSize = 100
    private var numberOfRuns = 10
    private var numberOfThreads = Runtime.getRuntime().availableProcessors()
    private var algorithm = "AES-CBC"

    private var runSizes = arrayOf("1", "10", "20", "50", "100", "1000", "10000", "100000")
    private var dataSetSizes = arrayOf("100", "1000", "10000")
    private var algorithmNames = arrayOf("AES-CBC", "AES-CTR", "AES-GCM", "BF-CBC", "ECIES-SECP256K1", "RSA-OAEP", "ECDSA-P521", "RSA-PSS")

    override fun onCreate(savedInstanceState: Bundle?) {
        Security.removeProvider("BC")
        Security.insertProviderAt(BouncyCastleProvider(), 1)

        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayText = findViewById(R.id.displayText)
        encryptButton = findViewById(R.id.encryptButton)

        runPicker = findViewById(R.id.runPicker)
        runPicker.value = 0
        runPicker.minValue = 0
        runPicker.maxValue = 7
        runPicker.displayedValues = runSizes

        runPicker.setOnValueChangedListener{ _, _, newVal ->
            numberOfRuns = runSizes[newVal].toInt()
        }

        threadPicker = findViewById(R.id.threadPicker)
        threadPicker.value = 4
        threadPicker.minValue = 1
        threadPicker.maxValue = 20

        threadPicker.setOnValueChangedListener{ _, _, newVal ->
            numberOfThreads = newVal
        }

        sizePicker = findViewById(R.id.sizePicker)
        sizePicker.value = 0
        sizePicker.minValue = 0
        sizePicker.maxValue = 2
        sizePicker.displayedValues = dataSetSizes

        sizePicker.setOnValueChangedListener{ _, _, newVal ->
           dataSetSize = dataSetSizes[newVal].toInt()
        }

        algorithmPicker = findViewById(R.id.algorithmPicker)
        algorithmPicker.value = 0
        algorithmPicker.minValue = 0
        algorithmPicker.maxValue = 7
        algorithmPicker.displayedValues = algorithmNames

        algorithmPicker.setOnValueChangedListener{ _, _, newVal ->
            algorithm = algorithmNames[newVal]
        }

        fun prompt(encrypt: Double, decrypt: Double, keygen: Double) {
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            builder.setTitle("Data")
            val dialogLayout = inflater.inflate(R.layout.prompt, null)

            builder.setView(dialogLayout)
            builder.setPositiveButton("OK") { _, _ ->
                val encryptResult = RunResult(
                    encrypt,
                    dialogLayout.findViewById<EditText>(R.id.encmem).text.toString().toDouble(),
                    dialogLayout.findViewById<EditText>(R.id.enccpu).text.toString().toDouble() / 100,
                )

                val decryptResult = RunResult(
                    decrypt,
                    dialogLayout.findViewById<EditText>(R.id.decmem).text.toString().toDouble(),
                    dialogLayout.findViewById<EditText>(R.id.deccpu).text.toString().toDouble() / 100,
                )

                val keyGenResult = RunResult(
                    keygen,
                    dialogLayout.findViewById<EditText>(R.id.keymem).text.toString().toDouble(),
                    dialogLayout.findViewById<EditText>(R.id.keycpu).text.toString().toDouble() / 100,
                )

                println("Key Generation: $keyGenResult")
                println("Encrypt: $encryptResult")
                println("Decrypt: $decryptResult")

                println("Uploading results..")

                DataHelper().uploadResult(encryptResult, decryptResult, keyGenResult, algorithm, dataSetSize, numberOfRuns)
            }

            builder.show()
        }

        encryptButton.setOnClickListener {
            CoroutineScope(Dispatchers.Default).launch {
                val res = FullSuite().run(
                    algorithm,
                    DataHelper().fetchDataSet(application, dataSetSize),
                    numberOfRuns,
                    numberOfThreads,
                )

                runOnUiThread {
                    prompt(res[0], res[1], res[2])
                }
            }
        }

    }
}