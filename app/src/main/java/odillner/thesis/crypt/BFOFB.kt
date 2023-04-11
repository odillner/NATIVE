package odillner.thesis.crypt

import odillner.thesis.utils.DataHelper
import odillner.thesis.utils.RunResult
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class BFOFB: Algorithm {
    override val name = "BF-OFB"
    val keySize = 256

    private fun generateKeySpec(): SecretKeySpec {
        val random = SecureRandom()

        val key = ByteArray(keySize)
        random.nextBytes(key)

        return SecretKeySpec(key, "Blowfish")
    }

    override fun performRun(data: Array<String>, numberOfRuns: Int): RunResult {
        val keySpec = generateKeySpec()

        val encryptCipher = Cipher.getInstance("Blowfish")
        encryptCipher.init(Cipher.ENCRYPT_MODE, keySpec)

        val decryptCipher = Cipher.getInstance("Blowfish")
        decryptCipher.init(Cipher.DECRYPT_MODE, keySpec)

        val helper = DataHelper()

        val res = RunResult(numberOfRuns)
        val encodedData = helper.encode(data)
        var decodedData = Array(data.size){""}

        for (i in 0 until numberOfRuns) {
            var start = System.currentTimeMillis()
            val encryptedData = Array(data.size) {encryptCipher.doFinal(encodedData[it])}
            var end = System.currentTimeMillis()
            res.encryptTimings[i] = end - start

            start = System.currentTimeMillis()
            val decryptedData = Array(data.size) {decryptCipher.doFinal(encryptedData[it])}
            end = System.currentTimeMillis()
            res.decryptTimings[i] = end - start

            decodedData = helper.decode(decryptedData)
        }

        res.data = decodedData

        return res
    }
}