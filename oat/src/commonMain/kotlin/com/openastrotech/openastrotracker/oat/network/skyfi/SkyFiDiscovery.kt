package com.openastrotech.openastrotracker.oat.network.skyfi

import com.openastrotech.openastrotracker.oat.MeadeTelescope
import com.openastrotech.openastrotracker.oat.network.Connection
import com.openastrotech.openastrotracker.oat.network.UDPSocket
import com.openastrotech.openastrotracker.oat.utils.loge
import com.openastrotech.openastrotracker.oat.utils.logv
import io.ktor.util.network.*
import io.ktor.utils.io.core.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Suppress("EXPERIMENTAL_API_USAGE")
class SkyFiDiscovery(private val udpSocketFactory: UDPSocket.Factory) {

    companion object {

        private const val TAG = "SkyFiDiscovery"

        private const val COMMUNICATION_PORT = 4030

        private const val BROADCAST_DELAY = 1000L
    }

    fun discover(
        remoteAddress: NetworkAddress
    ) = callbackFlow {
        val socket = udpSocketFactory.create(remoteAddress)

        // send discovery packets (broadcast)
        launch {
            do {
                logv(TAG, "Sending discovery datagram")

                val message = "skyfi:?"
                socket.send(message.toByteArray())

                delay(BROADCAST_DELAY)
            } while (isActive and (BROADCAST_DELAY > 0))
        }

        var closed = false

        // receive incoming packets
        launch {
            while (isActive) {
                logv(TAG, "Waiting for incoming datagram")

                val buffer = ByteArray(1024)

                val size: Int = try {
                    socket.receive(buffer)
                } catch (e: IOException) {
                    if (!closed) throw e else continue
                }
                val message = String(buffer, 0, size)

                logv(TAG, "Received message $message")

                // check if the datagram matches expected response regex
                // this way we ignore our own broadcast and not parsable datagrams
                // expected result of form: skyfi:OATerScope@192.168.178.76
                val regex = Regex("""skyfi:(.*)@(.*)""")
                if (!regex.matches(message)) {
                    logv(TAG, "message does not match regex, skip")
                    continue
                }

                logv(TAG, "A device answered with: $message")

                // parse the message by using regex
                regex.find(message)?.let { result ->
                    val (name, _) = result.destructured
                    val telescope = MeadeTelescope(
                        name,
//                        TCPConnection(host, COMMUNICATION_PORT)
                        object : Connection {
                            override val address: String
                                get() = TODO("Not yet implemented")
                            override val protocol: Connection.Protocol
                                get() = TODO("Not yet implemented")

                            override suspend fun command(command: String) {
                                TODO("Not yet implemented")
                            }

                            override suspend fun commandWithResponse(command: String): String {
                                TODO("Not yet implemented")
                            }

                        }
                    )

                    logv(TAG, "Discovered $telescope")

                    offer(telescope)
                } ?: run {
                    loge(TAG, "Failed to parse message=\"$message\"")
                }

            }
        }

        awaitClose {
            logv(TAG, "Closing")
            closed = true
            socket.close()
        }
    }

}