package com.openastrotech.openastrotracker.oat.network

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers

//actual class TCPConnection(
//    override val address: String,
//    override val protocol: Connection.Protocol
//) : Connection {
//
//    override suspend fun command(command: String) {
//        aSocket(ActorSelectorManager(Dispatchers.IO)).udp().bind(
//            configure =
//        )
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun commandWithResponse(command: String): String {
//        TODO("Not yet implemented")
//    }
//
//}