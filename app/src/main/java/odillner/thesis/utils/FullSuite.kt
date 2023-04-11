package odillner.thesis.utils

import odillner.thesis.crypt.AESGCM
import odillner.thesis.crypt.BFOFB
import odillner.thesis.crypt.ECIESSECP256K1
import odillner.thesis.crypt.RSAOAEP

class FullSuite {
    val algorithms = arrayOf(RSAOAEP(), AESGCM(), BFOFB(), ECIESSECP256K1())
    val helper = DataHelper()

    fun run(dataSet: Array<String>, numberOfRuns: Int) {
        for (algorithm in algorithms) {
            println("Running ${algorithm.name}...")

            val res = algorithm.performRun(dataSet, numberOfRuns)

            if (res.data contentEquals dataSet) {
                println("Encrypted and decrypted data matches original dataset")
            } else {
                println("Encrypted and decrypted data does not match")
                println(res.data[0])
                break;
            }

            println("${algorithm.name} done, uploading data...")

            helper.uploadResult(res, algorithm.name, dataSet.size)
        }

        println("All tests done!")
    }
}