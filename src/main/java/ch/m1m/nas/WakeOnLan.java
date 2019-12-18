package ch.m1m.nas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class WakeOnLan {

    private static Logger log = LoggerFactory.getLogger(NASControl.class);

    public static void main(String[] args) {

        String strBroadcastAddress = "192.168.1.255";
        String strMacAddress = "70:85:C2:DD:18:6F";

        try {
            // port 0 causes an exception
            //send(strMacAddress, strBroadcastAddress, 0);
            send(strMacAddress, strBroadcastAddress, 7);
            send(strMacAddress, strBroadcastAddress, 9);

        } catch (IOException e) {

            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Construct and send a datagram Wake on Lan packet
     *
     * @param macAddress       MAC address in the form 00:0D:61:08:22:4A or 00-0D-61-08-22-4A
     * @param broadcastAddress broadcast address e.g. 192.168.0.255
     * @param port             mostly 9, 7 (or 0)
     */
    public static void send(String macAddress, String broadcastAddress, int port) throws IOException {

        macAddress = macAddress.toUpperCase();

        try (DatagramSocket socket = new DatagramSocket()) {

            byte[] macBytes = getMacBytes(macAddress);
            byte[] bytes = new byte[6 + 16 * macBytes.length];

            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }

            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }

            InetAddress address = InetAddress.getByName(broadcastAddress);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, port);
            socket.send(packet);

            log.info("Wake-on-LAN packet sent to port {}", port);

        } catch (Exception e) {
            log.error("Failed to send Wake-on-LAN packet", e);
            throw e;
        }
    }

    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {

        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");

        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }

        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }

        return bytes;
    }
}
