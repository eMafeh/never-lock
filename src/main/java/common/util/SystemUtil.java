package common.util;

import java.net.*;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * 应用系统级工具<br>
 * 〈功能详细描述〉
 *
 * @author 17021650
 * @see [相关类/方法]（可选）
 * @since V20171120
 */
public class SystemUtil {
    private static final String LOCALHOST = "127.0.0.1";
    private static final String ANYHOST = "0.0.0.0";
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    private static final boolean LOCAL;

    public static boolean isWindows() {
        return LOCAL;
    }

    static {
        final String property = System.getProperty("os.name");
        LOCAL = property != null && property.toUpperCase()
                .contains("WINDOWS");
    }


    private static final String IP;
    private static final String MAC_ID;

    public static String getIP() {
        return IP;
    }

    public static String getMacId() {
        return MAC_ID;
    }

    private static String getMacFromBytes(byte[] bytes) {
        StringBuilder mac = new StringBuilder();
        byte currentByte;
        boolean first = false;
        for (byte b : bytes) {
            if (first) {
                mac.append("-");
            }
            currentByte = (byte) ((b & 240) >> 4);
            mac.append(Integer.toHexString(currentByte));
            currentByte = (byte) (b & 15);
            mac.append(Integer.toHexString(currentByte));
            first = true;
        }
        return mac.toString()
                .toUpperCase();
    }

    static {
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            ExceptionUtil.throwT(e);
        }
        String macId = null;
        String ip = null;

        try {
            InetAddress localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                ip = localAddress.getHostAddress();
            }
        } catch (UnknownHostException e) {
            ExceptionUtil.throwT(e);
        }

        while (interfaces.hasMoreElements()) {
            NetworkInterface network = interfaces.nextElement();
            Enumeration<InetAddress> addresses = network.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (isValidAddress(address)) {
                    if (ip == null) {
                        ip = address.getHostAddress();
                    }
                    try {
                        macId = getMacFromBytes(network.getHardwareAddress());
                    } catch (SocketException e) {
                        ExceptionUtil.throwT(e);
                    }
                }
            }
        }
        IP = ip;
        MAC_ID = macId;
    }

    private static boolean isValidAddress(InetAddress address) {
        if (address != null && !address.isLoopbackAddress()) {
            String name = address.getHostAddress();
            return name != null && !ANYHOST.equals(name) && !LOCALHOST.equals(name) && IP_PATTERN.matcher(name)
                    .matches();
        } else {
            return false;
        }
    }
}
