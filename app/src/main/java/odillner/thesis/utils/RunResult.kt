package odillner.thesis.utils

class RunResult(size: Int) {
    var encryptTimings = LongArray(size)
    var decryptTimings = LongArray(size)
    lateinit var data: Array<String>
}