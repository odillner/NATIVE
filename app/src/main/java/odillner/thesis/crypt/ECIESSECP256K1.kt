package odillner.thesis.crypt

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.spec.ECGenParameterSpec
import javax.crypto.Cipher

class ECIESSECP256K1: Algorithm {
    override val algorithm = "ECIES"
    override val name = "ECIES-SECP256K1"
    override val configuration = ""
    val curve = "secp256k1"

    override val encryptCipher: Cipher = Cipher.getInstance(algorithm)
    override val decryptCipher: Cipher = Cipher.getInstance(algorithm)
    private val keyGenerator = KeyPairGenerator.getInstance("ECDH")

    private lateinit var keyPair: KeyPair

    init {
        keyGenerator.initialize(ECGenParameterSpec(curve))

        generateKey()

        encryptCipher.init(Cipher.ENCRYPT_MODE, keyPair.public)
        decryptCipher.init(Cipher.DECRYPT_MODE, keyPair.private)
    }

    override fun generateKey() {
        keyPair = keyGenerator.genKeyPair()
    }
}