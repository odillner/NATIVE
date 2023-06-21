package odillner.thesis.crypt

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Signature
import javax.crypto.Cipher

class RSAPSS(private val keySize: Int) : Algorithm {
    override val algorithm = "RSA"
    override val name = "RSA-PSS"
    override val configuration = "SHA256withRSA/PSS"
    val signatureLength = keySize / 8

    override val encryptCipher: Cipher = Cipher.getInstance("ECIES")
    override val decryptCipher: Cipher = Cipher.getInstance("ECIES")
    private val keyGenerator = KeyPairGenerator.getInstance(algorithm)

    val signer: Signature = Signature.getInstance(configuration)
    val verifier: Signature = Signature.getInstance(configuration)

    private lateinit var keyPair: KeyPair

    init {
        keyGenerator.initialize(keySize)

        generateKey()

        signer.initSign(keyPair.private)
        verifier.initVerify(keyPair.public)
    }

    override fun generateKey() {
        keyPair = keyGenerator.genKeyPair()
    }

    override fun encrypt(data: ByteArray): ByteArray {
        signer.update(data)

        val signature = signer.sign()

        return signature + data
    }

    override fun decrypt(data: ByteArray): ByteArray {
        verifier.update(data, signatureLength, data.size - signatureLength)
        return if (verifier.verify(data, 0, signatureLength)) ByteArray(1){1} else ByteArray(1){0}
    }
}