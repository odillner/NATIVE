package odillner.thesis.utils
import kotlinx.coroutines.*
import kotlin.math.ceil

class FullSuite {
    private val helper = DataHelper()

    private fun work(transformation: (data: ByteArray) -> ByteArray, input: Array<ByteArray>, output: Array<ByteArray>, start: Int, end: Int) {
        for (i in start until end) {
            output[i] = transformation(input[i])
        }
    }

    suspend fun run(name: String, dataSet: Array<String>, numberOfRuns: Int, numberOfThreads: Int): Array<Double> {
        val algorithms = Array(numberOfThreads) {helper.algorithmFromName(name)}

        val dataSetSize = dataSet.size
        val sliceSize = ceil(dataSetSize.toDouble()/numberOfThreads).toInt()

        val encodedData = helper.encode(dataSet)

        val encryptedData = Array(dataSetSize){ByteArray(0)}
        val decryptedData = Array(dataSetSize){ByteArray(0)}

        val timings = LongArray(numberOfRuns)

        println("Running $name...")

        println("Starting key generation...")

        if (name != "RSA-PSS") {
            for (j in 0 until numberOfRuns) {
                val startTime = System.currentTimeMillis()

                val jobs = Array(numberOfThreads) { it1 ->
                    GlobalScope.launch {
                        repeat(sliceSize) {
                            algorithms[it1].generateKey()
                        }
                    }
                }

                jobs.forEach { job -> job.join() }

                timings[j] = System.currentTimeMillis() - startTime
                System.gc()
            }
        }

        val keyGenTimings = timings.average()

        println("Key Generation: $keyGenTimings")

        System.gc()

        delay(1000)

        println("Starting encryption...")

        for (j in 0 until numberOfRuns) {
            val startTime = System.currentTimeMillis()

            val jobs = Array(numberOfThreads){ GlobalScope.launch {
                val start = it*sliceSize
                val end = (it+1) * sliceSize

                work(algorithms[it]::encrypt, encodedData, encryptedData, start, if (end < dataSetSize) end else dataSetSize)
            }}

            jobs.forEach {job -> job.join()}

            timings[j] = System.currentTimeMillis() - startTime
            System.gc()
        }

        val encryptTimings = timings.average()

        println("Encryption: $encryptTimings")

        System.gc()

        delay(1000)

        println("Starting decryption...")

        for (j in 0 until numberOfRuns) {
            val startTime = System.currentTimeMillis()

            val jobs = Array(numberOfThreads){ GlobalScope.launch {
                val start = it*sliceSize
                val end = (it+1) * sliceSize

                work(algorithms[it]::decrypt, encryptedData, decryptedData, start, if (end < dataSetSize) end else dataSetSize)
            }}

            jobs.forEach {job -> job.join()}

            timings[j] = System.currentTimeMillis() - startTime
            System.gc()
        }

        val decryptTimings = timings.average()

        println("Encryption: $decryptTimings")

        if (name == "ECDSA-P521" || name == "RSA-PSS") {
            if (decryptedData.all {it contentEquals ByteArray(1){1}}) {
                println("Encrypted and decrypted data matches original dataset")
            } else {
                println("Encrypted and decrypted data does not match")
            }
        } else {
            val decodedData = helper.decode(decryptedData)

            if (decodedData contentEquals dataSet) {
                println("Encrypted and decrypted data matches original dataset")
            } else {
                println("Encrypted and decrypted data does not match")
            }
        }

        System.gc()

        return arrayOf(encryptTimings, decryptTimings, keyGenTimings)
    }
}
