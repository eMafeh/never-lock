package common.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public interface SystemUtil {
    boolean LOCAL = ((BooleanSupplier) () -> {
        final String property = System.getProperty("os.name");
        return property != null && property.toUpperCase()
                .contains("WINDOWS");
    }).getAsBoolean();

    String[] _IP_MACID = ((Supplier<String[]>) () -> {
        Enumeration<NetworkInterface> networkInterfaces;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        InetAddress ipadd;
        String macId = null;
        String ip = null;
        m:
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                ipadd = inetAddresses.nextElement();
                if (ipadd instanceof Inet4Address) {
                    ip = ipadd.getHostAddress();
                    if (ip != null && !"127.0.0.1".equals(ip)) {
                        if (ip.matches("(\\d{1,3}\\.){3}\\d{1,3}")) {
                            try {
                                byte[] bytes = networkInterface.getHardwareAddress();
                                StringBuilder mac = new StringBuilder();
                                byte currentByte;
                                boolean first = false;
                                for (byte b : bytes) {
                                    if (first) {
                                        mac.append('-');
                                    }
                                    currentByte = (byte) ((b & 240) >> 4);
                                    mac.append(Integer.toHexString(currentByte));
                                    currentByte = (byte) (b & 15);
                                    mac.append(Integer.toHexString(currentByte));
                                    first = true;
                                }
                                macId = mac.toString()
                                        .toUpperCase();
                            } catch (SocketException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        break m;
                    }
                    ip = null;
                }
            }
        }
        return new String[]{ip, macId};
    }).get();

    String IP = _IP_MACID[0];
    String MAC_ID = _IP_MACID[1];

}
