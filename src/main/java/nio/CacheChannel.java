package nio;

import dto.UserDto;
import nio.core.User;
import nio.message.SendMessage;
import service.UpdateUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CacheChannel {
    private final ServerBoot boot;
    private final Map<User, BaseChannelProxy> CACHE_MAP = new ConcurrentHashMap<>();
    private final Set<User> JOINED = new HashSet<>();

    public CacheChannel(final ServerBoot boot) {
        this.boot = boot;
        new NewNameService(this);
    }

    void handlerChannel(BaseChannelProxy proxy) {
        if (proxy == null || proxy instanceof ChannelServer) {
            return;
        }
        proxy.user.setStatus(1);
        CACHE_MAP.put(proxy.user, proxy);
        JOINED.add(proxy.user);
    }

    void handlerJoinChannel(ChannelServer.ChannelJoin join) {
        handlerChannel(join);
        User user = join.user;
        //通知其他在线用户
        SendMessage.Prepare<UserDto> prepare = new SendMessage.Prepare<>(new UserDto(user));
        CACHE_MAP.values()
                .stream()
                .filter(p -> !user.equals(p.user))
                .forEach(p -> boot.send(prepare.toSendMessage(p.user, UpdateUser.class, 5), p));
        //给该用户其他在线不在线用户
        boot.send(new SendMessage(user, UpdateUser.class, (ArrayList) JOINED.stream()
                .map(UserDto::new)
                .collect(Collectors.toList()), 5), join);
    }

    public BaseChannelProxy getChannel(final User user) {
        BaseChannelProxy channelProxy = CACHE_MAP.computeIfAbsent(user, key -> ChannelClient.newChannelClient(boot, key));
        //不可以合并至lambda内 会死锁
        handlerChannel(channelProxy);
        return channelProxy;
    }

    public void removeChannel(final IOException e, final BaseChannelProxy proxy) throws IOException {
        if (e != null && !"Connection reset by peer".equals(e.getMessage())) {
            e.printStackTrace();
        }
        System.err.println("close " + proxy);
        proxy.user.setStatus(0);
        CACHE_MAP.remove(proxy.user);
        proxy.channel.close();
        //通知其他在线用户
        SendMessage.Prepare<UserDto> prepare = new SendMessage.Prepare<>(new UserDto(proxy.user));
        CACHE_MAP.values()
                .forEach(p -> boot.send(prepare.toSendMessage(p.user, UpdateUser.class, 5), p));

    }

    public static class NewNameService extends RpcService<String> {
        private final CacheChannel cacheChannel;

        public NewNameService(final CacheChannel cacheChannel) {
            super(6);
            this.cacheChannel = cacheChannel;
        }

        @Override
        public void service(final String s, final UserDto sender) {
            User user = sender.getUser();
            user.setName(s);
            //通知其他在线用户
            SendMessage.Prepare<UserDto> prepare = new SendMessage.Prepare<>(new UserDto(user));
            cacheChannel.CACHE_MAP.values()
                    .forEach(p -> cacheChannel.boot.send(prepare.toSendMessage(p.user, UpdateUser.class, 5), p));
        }
    }
}
