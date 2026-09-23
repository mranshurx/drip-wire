package com.example.vpnpanel.parser

import java.nio.ByteBuffer

object PacketParser {

    data class UdpPacketInfo(
        val sourceIp: String,
        val destIp: String,
        val sourcePort: Int,
        val destPort: Int,
        val payloadOffset: Int,
        val payloadLength: Int
    )

    fun parseUdpPacket(buffer: ByteBuffer, length: Int): UdpPacketInfo? {
        if (length < 28) return null // Minimum IPv4 (20 bytes) + UDP (8 bytes) header size

        val data = buffer.array()
        
        // Read IP version and header length
        val versionAndIhl = data[0].toInt() and 0xFF
        val version = versionAndIhl shr 4
        if (version != 4) return null // Only handle IPv4 for now

        val ihl = (versionAndIhl and 0x0F) * 4
        
        // Check protocol (17 for UDP)
        val protocol = data[9].toInt() and 0xFF
        if (protocol != 17) return null

        // Extract Source IP (Bytes 12-15)
        val sourceIp = "${data[12].toInt() and 0xFF}.${data[13].toInt() and 0xFF}.${data[14].toInt() and 0xFF}.${data[15].toInt() and 0xFF}"
        
        // Extract Destination IP (Bytes 16-19)
        val destIp = "${data[16].toInt() and 0xFF}.${data[17].toInt() and 0xFF}.${data[18].toInt() and 0xFF}.${data[19].toInt() and 0xFF}"

        // UDP Header starts at `ihl` offset
        val udpHeaderOffset = ihl
        val sourcePort = ((data[udpHeaderOffset].toInt() and 0xFF) shl 8) or (data[udpHeaderOffset + 1].toInt() and 0xFF)
        val destPort = ((data[udpHeaderOffset + 2].toInt() and 0xFF) shl 8) or (data[udpHeaderOffset + 3].toInt() and 0xFF)
        
        val udpLength = ((data[udpHeaderOffset + 4].toInt() and 0xFF) shl 8) or (data[udpHeaderOffset + 5].toInt() and 0xFF)
        val payloadOffset = udpHeaderOffset + 8
        val payloadLength = udpLength - 8

        return UdpPacketInfo(
            sourceIp = sourceIp,
            destIp = destIp,
            sourcePort = sourcePort,
            destPort = destPort,
            payloadOffset = payloadOffset,
            payloadLength.coerceAtLeast(0)
        )
    }
}
