package com.openastrotech.openastrotracker.oat.network

actual class TCPConnection(
    override val address: String,
    override val protocol: Connection.Protocol
) : Connection {
    override suspend fun command(command: String) {
        TODO("Not yet implemented")
    }

    override suspend fun commandWithResponse(command: String): String {
        TODO("Not yet implemented")
    }

}