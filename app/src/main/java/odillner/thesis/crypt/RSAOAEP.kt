package odillner.thesis.crypt

import java.security.KeyPair
import java.security.KeyPairGenerator
import javax.crypto.Cipher
import kotlin.math.ceil

class RSAOAEP(keySize: Int) : Algorithm {
    override val algorithm = "RSA"
    override val name = "RSA-OAEP"
    override val configuration = "RSA/None/OAEPWithSHA256AndMGF1Padding"

    override val encryptCipher: Cipher = Cipher.getInstance(configuration)
    override val decryptCipher: Cipher = Cipher.getInstance(configuration)
    private val keyGenerator = KeyPairGenerator.getInstance(algorithm)

    private lateinit var keyPair: KeyPair

     init {
        keyGenerator.initialize(keySize)

        generateKey()

        encryptCipher.init(Cipher.ENCRYPT_MODE, keyPair.public)
        decryptCipher.init(Cipher.DECRYPT_MODE, keyPair.private)
    }

    override fun generateKey() {
        keyPair = keyGenerator.genKeyPair()
    }

    // encrypts data in chunks
    override fun encrypt(data: ByteArray): ByteArray {
        val size = encryptCipher.blockSize
        val len = ceil(data.size.toDouble()/size).toInt()

        val encryptedData = ByteArray(len * decryptCipher.blockSize)

        for (i in 0 until len) {
            val inputOffset = i * size
            val inputSize = if ((i+1) * size < data.size) size else (data.size - inputOffset)

            encryptCipher.doFinal(data, inputOffset, inputSize, encryptedData, i*decryptCipher.blockSize)
        }

        return encryptedData
    }

    // decrypts data in chunks
    override fun decrypt(data: ByteArray): ByteArray {
        val size = decryptCipher.blockSize
        val len = ceil(data.size.toDouble()/size).toInt()

        val decryptedData = ByteArray(len * 190)

        var last = 0

        for (i in 0 until len) {
            val inputOffset = i * size
            val inputSize = if ((i+1) * size < data.size) size else (data.size - inputOffset)

            last = decryptCipher.doFinal(data, inputOffset, inputSize, decryptedData, i * 190)
        }

        return decryptedData.copyOfRange(0, (len - 1) * 190 + last)
    }
}