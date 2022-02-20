package tools.packet;

import client.MapleCharacter;
import client.MapleClient;
import constants.ServerConstants;
import handling.MaplePacket;
import handling.SendPacketOpcode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.HexTool;
import tools.data.output.MaplePacketLittleEndianWriter;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoginPacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginPacket.class);

    public static final MaplePacket getHello(final short mapleVersion, final byte[] sendIv, final byte[] recvIv) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);

        if (ServerConstants.properties.isPacketDebugLogger()) {
            LOGGER.debug("getHello--------------------");
        }
        mplew.writeShort(13); // 13 = MSEA, 14 = GlobalMS, 15 = EMS
        mplew.writeShort(mapleVersion);
        mplew.write(new byte[]{0, 0});
        // mplew.writeMapleAsciiString(ServerConstants.MAPLE_PATCH);
        mplew.write(recvIv);
        mplew.write(sendIv);
        mplew.write(4); // 7 = MSEA, 8 = GlobalMS, 5 = Test Server

        return mplew.getPacket();
    }

    public static final MaplePacket getPing() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);

        if (ServerConstants.properties.isPacketDebugLogger()) {
            LOGGER.debug("getPing--------------------");
        }
        mplew.writeShort(SendPacketOpcode.PING.getValue());

        return mplew.getPacket();
    }

    /*public static final MaplePacket StrangeDATA() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);

        if (ServerConstants.properties.isPacketDebugLogger()) {
            LOGGER.debug("StrangeDATA--------------------");
        }
        mplew.writeShort(0x12);
        // long string = generated static public key
        mplew.writeMapleAsciiString("30819F300D06092A864886F70D010101050003818D0030818902818100994F4E66B003A7843C944E67BE4375203DAA203C676908E59839C9BADE95F53E848AAFE61DB9C09E80F48675CA2696F4E897B7F18CCB6398D221C4EC5823D11CA1FB9764A78F84711B8B6FCA9F01B171A51EC66C02CDA9308887CEE8E59C4FF0B146BF71F697EB11EDCEBFCE02FB0101A7076A3FEB64F6F6022C8417EB6B87270203010001");
        //mplew.writeMapleAsciiString("30819D300D06092A864886F70D010101050003818B00308187028181009E68DD55B554E5924BA42CCB2760C30236B66234AFAA420E8E300E74F1FDF27CD22B7FF323C324E714E143D71780C1982E6453AD87749F33E540DB44E9F8C627E6898F915587CD2A7D268471E002D30DF2E214E2774B4D3C58609155A7C79E517CEA332AF96C0161BFF6EDCF1CB44BA21392BED48CBF4BD1622517C6EA788D8D020111");

        return mplew.getPacket();
    }*/
    public static MaplePacket genderNeeded(MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);

        if (ServerConstants.properties.isPacketDebugLogger()) {
            LOGGER.debug("genderNeeded--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CHOOSE_GENDER.getValue());
        mplew.writeMapleAsciiString(c.getAccountName());

        return mplew.getPacket();
    }

    public static MaplePacket getLoginFailed(final int reason) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);

        if (ServerConstants.properties.isPacketDebugLogger()) {
            LOGGER.debug("getLoginFailed--------------------");
        }
        /**
         * * * * *
         * 3：身份证被删除或被阻挡 4：不正确的密码 5：不是一个注册的身份证 6：系统错误 7：已登录 8：系统错误 9：系统错误
         * 10：不能处理这么多连接 11：只有20岁以上的用户可以使用该频道 13：无法登录此知识产权 14：错误的网关或个人信息和奇怪的韩国按钮
         * 15：处理请求与韩国按钮！ 16：请通过电子邮件验证您的帐户… 17：错误的网关或个人信息 21：请通过电子邮件验证您的帐户…
         * 23：许可协议 25：欧洲枫叶欧洲公告 27：一些奇怪的完整的客户端通知，可能为试用版本 32：知识产权封锁 84：请重新通过网站-->
         * 0x07 recv响应00 / 01 /
         */
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.writeInt(reason);
        mplew.writeShort(0);

        return mplew.getPacket();
    }

    public static final MaplePacket getPermBan(final byte reason) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);

        if (ServerConstants.properties.isPacketDebugLogger()) {
            LOGGER.debug("getPermBan--------------------");
        }
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.writeShort(2); // Account is banned
        mplew.write(0);
        mplew.write(reason);
        mplew.write(HexTool.getByteArrayFromHexString("01 01 01 01 00"));

        return mplew.getPacket();
    }

    public static final MaplePacket getTempBan(final long timestampTill, final byte reason) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(17);

        if (ServerConstants.properties.isPacketDebugLogger()) {
            LOGGER.debug("getTempBan--------------------");
        }
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.write(2);
        mplew.write(HexTool.getByteArrayFromHexString("00 00 00 00 00"));
        mplew.write(reason);
        mplew.writeLong(timestampTill); // Tempban date is handled as a 64-bit long, number of 100NS intervals since 1/1/1601. Lulz.

        return mplew.getPacket();
    }

    public static final MaplePacket getGenderChanged(final MapleClient client) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        if (ServerConstants.properties.isPacketDebugLogger()) {
            LOGGER.debug("getGenderChanged--------------------");
        }
        mplew.writeShort(SendPacketOpcode.GENDER_SET.getValue());
        mplew.write(0);
        mplew.writeMapleAsciiString(client.getAccountName());
        mplew.writeMapleAsciiString(String.valueOf(client.getAccID()));

        return mplew.getPacket();
    }

    public static MaplePacket getGenderNeeded(MapleClient client) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        if (ServerConstants.properties.isPacketDebugLogger()) {
            LOGGER.debug("getGenderNeeded--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CHOOSE_GENDER.getValue());
        mplew.writeMapleAsciiString(client.getAccountName());

        return mplew.getPacket();
    }

    public static MaplePacket getAuthSuccessRequest(MapleClient client) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        if (ServerConstants.properties.isPacketDebugLogger()) {
            LOGGER.debug("getAuthSuccessRequest--------------------");
        }
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.write(0);
        mplew.writeInt(client.getAccID());
        mplew.write(client.getGender().getCodeByte());
        mplew.writeShort(client.isGm() ? 1 : 0); // Admin byte
        //mplew.writeInt(0);
        mplew.writeMapleAsciiString(client.getAccountName());
        mplew.write(HexTool.getByteArrayFromHexString("00 00 00 03 01 00 00 00 E2 ED A3 7A FA C9 01"));
        mplew.writeInt(0);
        mplew.writeLong(0L);
        mplew.writeMapleAsciiString(String.valueOf(client.getAccID()));
        mplew.writeMapleAsciiString(client.getAccountName());
        mplew.write(1);

        // mplew.writeLong(0);
        //  mplew.writeLong(0);
        //  mplew.writeLong(0);
        return mplew.getPacket();
    }

    public static final MaplePacket deleteCharResponse(final int cid, final int state) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        if (ServerConstants.properties.isPacketDebugLogger()) {
            LOGGER.debug("deleteCharResponse--------------------");
        }
        mplew.writeShort(SendPacketOpcode.DELETE_CHAR_RESPONSE.getValue());
        mplew.writeInt(cid);
        mplew.write(state);

        return mplew.getPacket();
    }

    public static final MaplePacket secondPwError(final byte mode) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);

        /*
         * 14 - Invalid password
         * 15 - Second password is incorrect
         */
        if (ServerConstants.properties.isPacketDebugLogger()) {
            LOGGER.debug("secondPwError--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SECONDPW_ERROR.getValue());
        mplew.write(mode);

        return mplew.getPacket();
    }

    public static MaplePacket getServerList(final int serverId, final String serverName, final Map<Integer, Integer> channelLoad) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        if (ServerConstants.properties.isPacketDebugLogger()) {
            LOGGER.debug("getServerList--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SERVERLIST.getValue());
        // 0 = Aquilla, 1 = bootes, 2 = cass, 3 = delphinus
        mplew.write(serverId);
        mplew.writeMapleAsciiString(serverName);
        mplew.write(ServerConstants.properties.getFlag());
        mplew.writeMapleAsciiString(ServerConstants.properties.getLoginEventMessage());
        mplew.writeShort(100);
        mplew.writeShort(100);

        int lastChannel = 1;
        Set<Integer> channels = channelLoad.keySet();
        for (int i = 30; i > 0; i--) {
            if (channels.contains(i)) {
                lastChannel = i;
                break;
            }
        }
        mplew.write(lastChannel);
        mplew.writeInt(500);

        int load;
        for (int i = 1; i <= lastChannel; i++) {
            if (channels.contains(i)) {
                load = channelLoad.get(i);
            } else {
                load = 1200;
            }
            mplew.writeMapleAsciiString(serverName + "-" + i);
            mplew.writeInt(load);
            mplew.write(serverId);
            mplew.writeShort(i - 1);
        }
        /*mplew.writeShort(ServerConstants.getBalloons().size());
        for (Balloon balloon : ServerConstants.getBalloons()) {
            mplew.writeShort(balloon.nX);
            mplew.writeShort(balloon.nY);
            mplew.writeMapleAsciiString(balloon.sMessage);
        }*/
        mplew.writeShort(0);

        //LOGGER.error(HexTool.toString(mplew.getPacket().getBytes()));
        return mplew.getPacket();
    }

    public static MaplePacket getEndOfServerList() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        if (ServerConstants.properties.isPacketDebugLogger()) {
            LOGGER.debug("getEndOfServerList--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SERVERLIST.getValue());
        mplew.write(0xFF);

        return mplew.getPacket();
    }

    public static final MaplePacket getServerStatus(final int status) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        if (ServerConstants.properties.isPacketDebugLogger()) {
            LOGGER.debug("getServerStatus--------------------");
        }
        /*	 * 0 - Normal
         * 1 - Highly populated
         * 2 - Full*/
        mplew.writeShort(SendPacketOpcode.SERVERSTATUS.getValue());
        mplew.writeShort(status);

        return mplew.getPacket();
    }

    public static MaplePacket getCharList(boolean secondpw, List<MapleCharacter> chars, int charslots) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        if (ServerConstants.properties.isPacketDebugLogger()) {
            LOGGER.debug("getCharList--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CHARLIST.getValue());
        mplew.write(0);
        mplew.writeInt(0); // 40 42 0F 00
        mplew.write(chars.size()); // 1

        for (MapleCharacter chr : chars) {
            addCharEntry(mplew, chr, !chr.isGM() && chr.getLevel() >= 10, false);
        }
        mplew.writeShort(3); // second pw request
        mplew.writeInt(charslots);

        return mplew.getPacket();
    }

    public static final MaplePacket addNewCharEntry(final MapleCharacter chr, final boolean worked) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        if (ServerConstants.properties.isPacketDebugLogger()) {
            LOGGER.debug("addNewCharEntry--------------------");
        }
        mplew.writeShort(SendPacketOpcode.ADD_NEW_CHAR_ENTRY.getValue());
        mplew.write(worked ? 0 : 1);
        addCharEntry(mplew, chr, false, false);

        return mplew.getPacket();
    }

    public static MaplePacket charNameResponse(String charname, boolean nameUsed) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        if (ServerConstants.properties.isPacketDebugLogger()) {
            LOGGER.debug("charNameResponse--------------------");
        }
        mplew.writeShort(SendPacketOpcode.CHAR_NAME_RESPONSE.getValue());
        mplew.writeMapleAsciiString(charname);
        mplew.write(nameUsed ? 1 : 0);

        return mplew.getPacket();
    }

    private static void addCharEntry(MaplePacketLittleEndianWriter mplew, MapleCharacter chr,
                                     boolean ranking, boolean viewAll) {
        if (ServerConstants.properties.isPacketDebugLogger()) {
            LOGGER.debug("addCharEntry--------------------");
        }
        PacketHelper.addCharStats(mplew, chr);
        PacketHelper.addCharLook(mplew, chr, true, viewAll);
        mplew.write(0); //<-- who knows
        if (chr.getJob() == 900) {
            mplew.write(2);
            return;
        }
        /* mplew.write(ranking ? 1 : 0);
        if (ranking) {
            mplew.writeInt(chr.getRank());
            mplew.writeInt(chr.getRankMove());
            mplew.writeInt(chr.getJobRank());
            mplew.writeInt(chr.getJobRankMove());
        }*/
    }
}
