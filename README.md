# Free Fire VPN Panel

An Android application built in Kotlin that sets up a local VPN service using `VpnService` and a TUN interface to intercept and parse raw IP/UDP packets.

## Project Structure
- `app/src/main/java/com/example/vpnpanel/service/GameVpnService.kt` - Manages the local VPN TUN interface and background packet-reading loop.
- `app/src/main/java/com/example/vpnpanel/parser/PacketParser.kt` - Parses raw IPv4 and UDP headers from the packet buffer.
- `app/src/main/java/com/example/vpnpanel/MainActivity.kt` - Handles user permission requests and service control.

## Getting Started
1. Clone the repository or open it in Android Studio.
2. Sync Gradle dependencies.
3. Build and run the app on an Android device or emulator.
4. Tap the button to grant VPN permissions and start capturing local traffic.
