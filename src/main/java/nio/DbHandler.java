package nio;

import common.CmdInstructions;
import common.util.*;
import dto.UserDto;
import nio.core.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static common.util.JarUtil.JAR_KIND;

public class DbHandler {
    private static final String DB_FILE = "group.dbfile";
    private static final String COMMAND = System.getProperty("sun.java.command");
    /**
     * 兼容ide本地
     */
    private static final String ROOT = COMMAND.endsWith(".jar") ? COMMAND : "src\\main\\resources";

    static ArrayList<UserDto> read() {
        try {
            return SerializableUtil.deSerializable(JarUtil.read(ROOT, DB_FILE));
        } catch (Exception e) {
            User.SELF.newMsg("无法获取服务器信息\n已初始化");
            return new ArrayList<>();
        }
    }

    static void write() {
        if (!SystemUtil.isWindows() || !ROOT.endsWith(JAR_KIND)) {
            try {
                System.out.println("update " + DB_FILE);
                JarUtil.write(ROOT, DB_FILE, toDb());
            } catch (IOException e) {
                ExceptionUtil.throwT(e);
            }
        }
        //本地客户端，没法直接将最新信息更新到文件里
    }

    public static void autoUpdate() {
        if (SystemUtil.isWindows() && ROOT.endsWith(JAR_KIND)) {
            //关闭后，更新本地jar
            DestroyedUtil.addListener(() -> {
                try {
                    CmdInstructions.inProp(DB_FILE, toDb());
                } catch (IOException e) {
                    ExceptionUtil.throwT(e);
                }
            });
        } else {
            //监听后续新建用户,修改用户,更新文件
            User.init(user -> {
                user.listenName.add(s -> write());
                write();
            },false);
            User.getAll()
                    .forEach(user -> user.listenName.add(s -> write()));
            //关闭后，更新
            DestroyedUtil.addListener(DbHandler::write);
        }
    }

    private static byte[] toDb() {
        return SerializableUtil.serializable((ArrayList<UserDto>) User.getAll()
                .map(UserDto::new)
                .collect(Collectors.toList()));
    }
}
