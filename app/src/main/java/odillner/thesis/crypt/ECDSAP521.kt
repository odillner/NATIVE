package odillner.thesis.crypt

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Signature
import java.security.spec.ECGenParameterSpec
import javax.crypto.Cipher

class ECDSAP521: Algorithm {
    override val algorithm = "ECDSA"
    override val name = "ECDSA-P521"
    override val configuration = "SHA256withECDSA"
    val curve = "P-521"

    override val encryptCipher: Cipher = Cipher.getInstance("ECIES")
    override val decryptCipher: Cipher = Cipher.getInstance("ECIES")
    private val keyGenerator = KeyPairGenerator.getInstance(algorithm)

    val signer: Signature = Signature.getInstance(configuration)
    val verifier: Signature = Signature.getInstance(configuration)

    private lateinit var keyPair: KeyPair

    init {
        keyGenerator.initialize(ECGenParameterSpec(curve))

        generateKey()

        signer.initSign(keyPair.private)
        verifier.initVerify(keyPair.public)
    }

    override fun generateKey() {
        keyPair = keyGenerator.genKeyPair()
    }

    // verifies given data, stores signature length in first byte followed by signature followed by data
    override fun encrypt(data: ByteArray): ByteArray {
        signer.update(data)

        val signature = signer.sign()

        return ByteArray(1){(signature.size - 128).toByte()} + signature + data
    }

    // extracts signature and verifies data, returns whether or not it was successful
    override fun decrypt(data: ByteArray): ByteArray {
        val signatureLength = data[0].toInt() + 128

        verifier.update(data, signatureLength + 1, data.size - (signatureLength + 1))
        return if (verifier.verify(data, 1, signatureLength)) ByteArray(1){1} else ByteArray(1){0}
    }
}