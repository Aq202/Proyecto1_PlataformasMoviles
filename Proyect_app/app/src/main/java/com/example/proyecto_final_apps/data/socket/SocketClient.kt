package com.example.proyecto_final_apps.data.socket

import io.socket.client.IO;
import io.socket.client.Socket
import java.net.URISyntaxException

object SocketClient {

    private lateinit var mSocket: Socket

    @Synchronized
    fun setSocket() {
        try {
            mSocket = IO.socket("http://192.168.1.15:2004")
        } catch (e: URISyntaxException) {

        }
    }

    @Synchronized
    fun getSocket(): Socket {
        return mSocket
    }

    @Synchronized
    fun connect() {
        mSocket.connect()
    }

    @Synchronized
    fun close() {
        mSocket.disconnect()
    }
}