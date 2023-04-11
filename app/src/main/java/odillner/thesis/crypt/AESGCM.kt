package odillner.thesis.crypt

import odillner.thesis.utils.DataHelper
import odillner.thesis.utils.RunResult
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class AESGCM: Algorithm {
    override val name = "AES-GCM"
    val keySize = 256
    val IVLength = 12

    private fun generateKeySpec(): SecretKeySpec {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(keySize)

        return SecretKeySpec(keyGenerator.generateKey().encoded, "AES")
    }

    private fun encrypt(data: ByteArray, paramSpec: GCMParameterSpec, keySpec: SecretKeySpec): ByteArray {
        val encryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
        encryptCipher.init(Cipher.ENCRYPT_MODE, keySpec, paramSpec)

        return encryptCipher.doFinal(data)
    }

    private fun decrypt(data: ByteArray, paramSpec: GCMParameterSpec, keySpec: SecretKeySpec): ByteArray {
        val decryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
        decryptCipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec)

        return decryptCipher.doFinal(data)
    }

    override fun performRun(data: Array<String>, numberOfRuns: Int): RunResult {
        val keySpec = generateKeySpec()
        val random = SecureRandom()

        val helper = DataHelper()

        val res = RunResult(numberOfRuns)
        val encodedData = helper.encode(data)
        var decodedData = Array(data.size){""}

        for (i in 0 until numberOfRuns) {
            val iv = ByteArray(IVLength)

            val paramSpec = Array(data.size){
                random.nextBytes(iv)
                GCMParameterSpec(128, iv)
            }

            var start = System.currentTimeMillis()
            val encryptedData = Array(data.size) {encrypt(encodedData[it], paramSpec[it], keySpec)}
            var end = System.currentTimeMillis()
            res.encryptTimings[i] = end - start

            start = System.currentTimeMillis()
            val decryptedData = Array(data.size) {decrypt(encryptedData[it], paramSpec[it], keySpec)}
            end = System.currentTimeMillis()
            res.decryptTimings[i] = end - start

            decodedData = helper.decode(decryptedData)
        }

        res.data = decodedData

        return res
    }
}