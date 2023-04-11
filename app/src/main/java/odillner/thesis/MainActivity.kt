package odillner.thesis

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import odillner.thesis.utils.DataHelper
import odillner.thesis.utils.FullSuite

class MainActivity : Activity() {
    private lateinit var displayText: TextView
    private lateinit var encryptButton: Button
    private lateinit var runPicker: NumberPicker
    private lateinit var sizePicker: NumberPicker

    private var dataSetSize = 100
    private var numberOfRuns = 1
    private var dataSetSizes = arrayOf("100", "1000", "10000")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayText = findViewById(R.id.displayText)
        encryptButton = findViewById(R.id.encryptButton)


        runPicker = findViewById(R.id.runPicker)
        runPicker.value = 1
        runPicker.minValue = 1
        runPicker.maxValue = 100

        runPicker.setOnValueChangedListener{ _, _, newVal ->
            numberOfRuns = newVal
        }


        sizePicker = findViewById(R.id.sizePicker)
        sizePicker.value = 0
        sizePicker.minValue = 0
        sizePicker.maxValue = 2
        sizePicker.displayedValues = dataSetSizes

        sizePicker.setOnValueChangedListener{ _, _, newVal ->
           dataSetSize = dataSetSizes[newVal].toInt()
        }

        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())

        encryptButton.setOnClickListener {
            GlobalScope.launch {
                FullSuite().run(DataHelper().fetchDataSet(application, dataSetSize), numberOfRuns)
            }
        }
    }
}