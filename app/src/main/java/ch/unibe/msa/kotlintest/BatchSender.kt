package ch.unibe.msa.kotlintest

import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.scheduleAtFixedRate

/**
 * Sends data in internal queue to web endpoint at configured rate
 */
class BatchSender(val queue: BlockingQueue<IJsonable> = LinkedBlockingQueue<IJsonable>()) {

    var timer = Timer()

    public fun start() {
        timer = Timer()
        timer.scheduleAtFixedRate(0, 5000) { sendSensorData() }
    }

    public fun stop() {
        sendSensorData()
        timer.cancel()
    }

    private fun sendSensorData() {
        val data: MutableList<IJsonable> = arrayListOf()

        while (queue.size() > 0) {
            data.add(queue.take())
        }

        val jsonData = data.map { it.toJson() }.join(",", "[", "]")

        if (jsonData.length() > 0) {
            DataSender("http://192.168.0.109:3000").send(jsonData)
            println("Sending ${data.size()} nodes")
        }
    }
}