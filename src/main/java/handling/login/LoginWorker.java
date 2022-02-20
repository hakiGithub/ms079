package handling.login;

import client.MapleClient;
import com.github.mrzhqiang.maplestory.auth.AuthenticationServer;
import com.github.mrzhqiang.maplestory.domain.Gender;
import com.github.mrzhqiang.maplestory.timer.Timer;
import handling.channel.ChannelServer;
import tools.MaplePacketCreator;
import tools.packet.LoginPacket;

import java.util.Map;
import java.util.Map.Entry;

/**
 * 登录 工作器
 * todo interface
 */
public class LoginWorker {

    private static long lastUpdate = 0;

    public static void registerClient(MapleClient client, AuthenticationServer authenticationServer) {
        // 判断是否维护阶段 只允许 管理员和 GM登录
        if (authenticationServer.isAdminOnly() && !client.isGm()) {
            client.getSession().write(MaplePacketCreator.serverNotice(1, "服务器正在维护中"));
            client.getSession().write(LoginPacket.getLoginFailed(7));
            return;
        }
        // 每10分钟更新一次
        if (System.currentTimeMillis() - lastUpdate > 600000) {
            lastUpdate = System.currentTimeMillis();
            Map<Integer, Integer> load = ChannelServer.getChannelLoad();
            int usersOn = 0;
            // In an unfortunate event that client logged in before load
            if (load == null || load.size() <= 0) {
                lastUpdate = 0;
                client.getSession().write(LoginPacket.getLoginFailed(7));

                return;
            }
            double loads = load.size();
            double userlimit = authenticationServer.getUserLimit();
            double loadFactor = 1200 / ((double) authenticationServer.getUserLimit() / load.size());
            for (Entry<Integer, Integer> entry : load.entrySet()) {
                usersOn += entry.getValue();
                load.put(entry.getKey(), Math.min(1200, (int) (entry.getValue() * loadFactor)));

            }
            authenticationServer.setLoad(load, usersOn);
            lastUpdate = System.currentTimeMillis();

        }

        if (client.finishLogin() == 0) {
            if (client.getGender() == Gender.UNKNOWN) {
                client.getSession().write(LoginPacket.getGenderNeeded(client));
            } else {
                client.getSession().write(LoginPacket.getAuthSuccessRequest(client));
                client.getSession().write(LoginPacket.getServerList(0,
                        authenticationServer.getServerName(), authenticationServer.getLoad()));
                client.getSession().write(LoginPacket.getEndOfServerList());

            }
            client.setIdleTask(Timer.PING.schedule(() -> {
//                    client.getSession().close();
            }, 10 * 60 * 10000));
        } else {
            if (client.getGender() == Gender.UNKNOWN) {
                client.getSession().write(LoginPacket.getGenderNeeded(client));

            } else {
                client.getSession().write(LoginPacket.getAuthSuccessRequest(client));
                client.getSession().write(LoginPacket.getServerList(0,
                        authenticationServer.getServerName(), authenticationServer.getLoad()));
                client.getSession().write(LoginPacket.getEndOfServerList());

            }
           /* client.getSession().write(LoginPacket.getLoginFailed(7));

            LOGGER.debug("登录Z");
            return;*/
        }
    }
}
