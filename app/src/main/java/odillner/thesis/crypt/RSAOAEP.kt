package odillner.thesis.crypt

import odillner.thesis.utils.DataHelper
import odillner.thesis.utils.RunResult
import java.security.KeyPair
import java.security.KeyPairGenerator
import javax.crypto.Cipher

class RSAOAEP : Algorithm {
    override val name = "RSA-OAEP"
    val keySize = 2048
    val chunkSize = 214

    private fun generateKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(keySize)

        return keyGen.genKeyPair()
    }


    override fun performRun(data: Array<String>, numberOfRuns: Int): RunResult {
        val keyPair = generateKeyPair()

        val encryptCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding")
        val decryptCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding")

        encryptCipher.init(Cipher.ENCRYPT_MODE, keyPair.public)
        decryptCipher.init(Cipher.DECRYPT_MODE, keyPair.private)

        val helper = DataHelper()

        val res = RunResult(numberOfRuns)
        val encodedData = helper.encode(data)
        var decodedData = Array(data.size){""}

        for (i in 0 until numberOfRuns) {
            var start = System.currentTimeMillis()
            val chunkedData = Array(encodedData.size){helper.chunk(encodedData[it], chunkSize)}
            val encryptedData = Array(data.size) {
                it1 -> Array(chunkedData[it1].size) {
                    it2 -> encryptCipher.doFinal(chunkedData[it1][it2])
                }
            }
            var end = System.currentTimeMillis()
            res.encryptTimings[i] = end - start

            start = System.currentTimeMillis()
            val decryptedData = Array(data.size) {
                it1 -> Array(encryptedData[it1].size) {
                    it2 -> decryptCipher.doFinal(encryptedData[it1][it2])
                }
            }
            val dechunkedData = Array(data.size){helper.dechunk(decryptedData[it])}
            end = System.currentTimeMillis()
            res.decryptTimings[i] = end - start

            decodedData = helper.decode(dechunkedData)
        }

        res.data = decodedData

        return res
    }
}