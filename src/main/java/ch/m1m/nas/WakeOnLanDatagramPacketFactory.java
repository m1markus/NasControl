package ch.m1m.nas;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * Factory that creates new Wake on LAN {@link DatagramPacket datagram packets} for given MAC address, broadcast address
 * and port.
 */
public class WakeOnLanDatagramPacketFactory {

    private WakeOnLanDatagramPacketFactory() {
        // This factory is currently not instantiatable
    }

    /**
     * @param macAddress       MAC address in the form 00:0D:61:08:22:4A or 00-0D-61-08-22-4A
     * @param broadcastAddress broadcast address e.g. 192.168.0.255
     * @param port             mostly 9, 7 (or 0)
     * @return the datagram package ready to be sent
     */
    public static DatagramPacket newInstance(String macAddress, String broadcastAddress, int port) {
        Objects.requireNonNull(macAddress);
        Objects.requireNonNull(broadcastAddress);
        Objects.requireNonNull(port);

        byte[] macBytes = getMacAddressInBytes(macAddress);
        byte[] bytes = new byte[6 + 16 * macBytes.length];

        for (int i = 0; i < 6; i++) {
            bytes[i] = (byte) 0xff;
        }

        for (int i = 6; i < bytes.length; i += macBytes.length) {
            System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
        }

        InetAddress address;
        try {
            address = InetAddress.getByName(broadcastAddress);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid broadcast address.", e);
        }

        return new DatagramPacket(bytes, bytes.length, address, port);
    }

    private static byte[] getMacAddressInBytes(String macAddress) {
        final String[] hex = macAddress.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }

        final byte[] bytes = new byte[6];
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
