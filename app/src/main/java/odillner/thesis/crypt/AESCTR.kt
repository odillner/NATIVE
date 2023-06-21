package odillner.thesis.crypt

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class AESCTR(keySize: Int, private val IVSize: Int): Algorithm {
    override val algorithm: String = "AES"
    override val name = "AES-CTR"
    override val configuration = "AES/CTR/NoPadding"

    override val encryptCipher: Cipher = Cipher.getInstance(configuration)
    override val decryptCipher: Cipher = Cipher.getInstance(configuration)
    private val keyGenerator: KeyGenerator = KeyGenerator.getInstance(algorithm)

    private lateinit var keySpec: SecretKeySpec
    private lateinit var iv: ByteArray

    init {
        keyGenerator.init(keySize)

        generateKey()
        generateIV()
    }

    override fun generateKey() {
        keySpec = SecretKeySpec(keyGenerator.generateKey().encoded, algorithm)
    }


    private fun generateIV() {
        val random = SecureRandom()

        val iv = ByteArray(IVSize)
        random.nextBytes(iv)

        this.iv = iv
    }

    private fun getNextIV(): ByteArray {
        iv[iv.size - 1] = iv[iv.size - 1].inc()

        return iv
    }

    override fun encrypt(data: ByteArray): ByteArray {
        encryptCipher.init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(getNextIV()))

        return iv + encryptCipher.doFinal(data)
    }

    override fun decrypt(data: ByteArray): ByteArray {
        decryptCipher.init(Cipher.DECRYPT_MODE, keySpec, IvParameterSpec(data, 0, IVSize))

        return decryptCipher.doFinal(data, IVSize, data.size - IVSize)
    }
}
