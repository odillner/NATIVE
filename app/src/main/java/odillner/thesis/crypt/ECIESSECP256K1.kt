package odillner.thesis.crypt

import odillner.thesis.utils.DataHelper
import odillner.thesis.utils.RunResult
import org.spongycastle.jce.provider.BouncyCastleProvider
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Security
import java.security.spec.ECGenParameterSpec
import javax.crypto.Cipher

class ECIESSECP256K1: Algorithm {
    override val name = "ECIES-SECP256K1"

    private fun generateKeyPair(): KeyPair {
        val gen = KeyPairGenerator.getInstance("ECDH")
        gen.initialize(ECGenParameterSpec("secp256k1"))

        return gen.genKeyPair()
    }

    override fun performRun(data: Array<String>, numberOfRuns: Int): RunResult {
        Security.removeProvider("BC")
        Security.insertProviderAt(BouncyCastleProvider(), 1)

        val keyPair = generateKeyPair()

        val encryptCipher = Cipher.getInstance("ECIES")
        encryptCipher.init(Cipher.ENCRYPT_MODE, keyPair.public)

        val decryptCipher = Cipher.getInstance("ECIES")
        decryptCipher.init(Cipher.DECRYPT_MODE, keyPair.private)

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