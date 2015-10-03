package ch.unibe.msa.kotlintest

import com.goebl.david.Response
import com.goebl.david.Webb
import com.goebl.david.WebbException

class DataSender(val url: String) {

    companion object {
        val client = Webb.create()
    }

    fun send(data: String): Response<String>? {
        try {
            val response = DataSender.client.post(url).param("data", data).asString()
            return response
        } catch(e: WebbException) { return null }
    }
}