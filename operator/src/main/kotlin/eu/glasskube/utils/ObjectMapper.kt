package eu.glasskube.utils

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okio.BufferedSink

private val MEDIA_TYPE_JSON = "application/json".toMediaType()

fun ObjectMapper.responseBody(value: Any?): RequestBody =
    object : RequestBody() {
        override fun contentType() = MEDIA_TYPE_JSON
        override fun writeTo(sink: BufferedSink) {
            writeValue(sink.outputStream(), value)
        }
    }
