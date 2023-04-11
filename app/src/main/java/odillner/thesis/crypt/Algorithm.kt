package odillner.thesis.crypt

import odillner.thesis.utils.RunResult

interface Algorithm {
    val name: String

    fun performRun(data: Array<String>, numberOfRuns: Int): RunResult {
        return RunResult(1)
    }
}