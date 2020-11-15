package com.openastrotech.openastrotracker.oat.network


import com.openastrotech.openastrotracker.oat.utils.logv
import io.ktor.util.network.*
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

@Suppress("EXPERIMENTAL_API_USAGE")
data class TCPConnection(private val host: String, private val port: Int) : Connection {

    companion object {
        private const val TAG = "TCPClient"
    }

    object FACTORY : Connection.Factory {
        override fun create(address: String): Connection {
            val regex = Regex("""(.*):(.*)""")
            val (host, port) = regex.find(address)?.destructured
                ?: throw Exception("failed to parse address")
            return TCPConnection(host, port.toInt())
        }
    }

    override suspend fun command(command: String) {
        logv(TAG, "command() called with: command = $command")
        aSocket(ActorSelectorManager(Dispatchers.Default))
            .tcp()
            .connect(NetworkAddress(host, port))
            .use { socket ->
                logv(TAG, "sending command: $command")
                socket.openWriteChannel(true).write(command)
            }
    }

    override suspend fun commandWithResponse(command: String): String {
        logv(TAG, "preparing command with response: $command@$address")

        aSocket(ActorSelectorManager(Dispatchers.IO))
            .tcp()
            .connect(NetworkAddress(host, port))
            .use { socket ->
                val writeChannel = socket.openWriteChannel(true)


                logv(TAG, "sending command: $command")
                writeChannel.write(command)
                writeChannel.close()

                val readChannel = socket.openReadChannel()
                logv(TAG, "waiting for response")
                val packet = readChannel.readRemaining()
                return packet.readerUTF8().readText()
            }
    }

    override val address: String
        get() = "$host:$port"

    override val protocol: Connection.Protocol
        get() = Connection.Protocol.TCP

}