package odillner.thesis.utils

import android.content.Context
import android.os.Build
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import odillner.thesis.crypt.*
import org.json.JSONObject


class DataHelper {
    fun encode(data: Array<String>) : Array<ByteArray> {
        return Array(data.size){data[it].toByteArray()}
    }

    fun decode(data: Array<ByteArray>) : Array<String> {
        return Array(data.size){String(data[it])}
    }

    fun fetchDataSet(context: Context, size: Int) : Array<String> {
        val fileContent = context.assets.open("${size}.json").bufferedReader().use {it.readText()}.split("}}},").toTypedArray()
        return Array(fileContent.size){fileContent[it] + "}}}"}
    }

    fun algorithmFromName(name: String): Algorithm {
        when (name) {
            "AES-CBC" -> return AESCBC(256, 16)
            "AES-CTR" -> return AESCTR(256, 12)
            "AES-GCM" -> return AESGCM(256, 12)
            "BF-CBC" -> return BFCBC(256, 8)
            "ECIES-SECP256K1" -> return ECIESSECP256K1()
            "ECDSA-P521" -> return ECDSAP521()
            "RSA-OAEP" -> return RSAOAEP(2048)
            "RSA-PSS" -> return RSAPSS(2048)
        }

        return AESCBC(256, 16)
    }

    fun uploadResult(encryptRes: RunResult, decryptRes: RunResult, keyGenResult: RunResult, algorithmName: String, dataSetSize: Int, numberOfRuns: Int) {
        val body = JSONObject()

        body.put("platform", "NATIVE")
        body.put("deviceBrand", Build.BRAND.uppercase())
        body.put("deviceModel", Build.MODEL.uppercase())
        body.put("deviceOS", "ANDROID" + Build.VERSION.RELEASE)

        body.put("algorithm", algorithmName)
        body.put("dataSetSize", dataSetSize)
        body.put("numberOfRuns", numberOfRuns)

        body.put("avgKeyGenTime", keyGenResult.avgTime)
        body.put("avgKeyGenMem", keyGenResult.avgMem)
        body.put("avgKeyGenCPU", keyGenResult.avgCPU)

        body.put("avgEncryptTime", encryptRes.avgTime)
        body.put("avgEncryptMem", encryptRes.avgMem)
        body.put("avgEncryptCPU", encryptRes.avgCPU)

        body.put("avgDecryptTime", decryptRes.avgTime)
        body.put("avgDecryptMem", decryptRes.avgMem)
        body.put("avgDecryptCPU", decryptRes.avgCPU)

        Fuel.post("https://data-gatherer.onrender.com/api/data").jsonBody(body.toString()).response()
    }
}