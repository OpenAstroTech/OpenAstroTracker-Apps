package com.openastrotech.openastrotracker.oat.network

interface TCPSocketDelegate {

    fun send(host: String, port: Int, message: String)

    fun sendAndRead(host: String, port: Int, bufferSize: Int): String

}