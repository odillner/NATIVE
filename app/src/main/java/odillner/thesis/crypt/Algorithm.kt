package odillner.thesis.crypt

import javax.crypto.Cipher

interface Algorithm {
    val algorithm: String
    val name: String
    val configuration: String

    val encryptCipher: Cipher
    val decryptCipher: Cipher

    fun encrypt(data: ByteArray): ByteArray {
        return encryptCipher.doFinal(data)
    }

    fun decrypt(data: ByteArray): ByteArray {
        return decryptCipher.doFinal(data)
    }

    fun generateKey() {}
}