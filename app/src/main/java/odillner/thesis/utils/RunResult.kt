/* DTO for storing results */

package odillner.thesis.utils

class RunResult(avgTime: Double, avgMem: Double, avgCPU: Double) {
    val avgTime: Double
    val avgMem: Double
    val avgCPU: Double

    init {
        this.avgTime = avgTime
        this.avgMem = avgMem
        this.avgCPU = avgCPU
    }

    override fun toString(): String {
        return "avgTime: $avgTime avgMem: $avgMem avgCPU: $avgCPU"
    }
}