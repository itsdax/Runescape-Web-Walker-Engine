package scripts.webwalker_logic.shared.helpers;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;


public class WorldHelper {

    private static WorldHelper instance = null;
    private Map<Integer, World> worldList;

    private WorldHelper() {
        try {
            worldList = loadWorldList();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static WorldHelper getInstance() {
        return instance != null ? instance : (instance = new WorldHelper());
    }

    public static boolean isMember(int worldNumber) {
        worldNumber = worldNumber % 300;
        World world = getInstance().getWorldList().get(worldNumber);
        return world != null && world.isMember();
    }

    public Map<Integer, World> getWorldList() {
        return worldList;
    }

    /**
     * Credit to AlphaDog
     * @return
     * @throws IOException
     */
    private static Map<Integer, World> loadWorldList() throws IOException {
        Map<Integer, World> worldList = new HashMap<>();
        URLConnection urlConnection = new URL("http://oldschool.runescape.com/slr").openConnection();
        try (DataInputStream dataInputStream = new DataInputStream(urlConnection.getInputStream())) {
            int size = dataInputStream.readInt() & 0xFF;
            int worldCount = dataInputStream.readShort();
            for (int i = 0; i < worldCount; i++) {
                int world = (dataInputStream.readShort() & 0xFFFF) % 300, flag = dataInputStream.readInt();
                boolean member = (flag & 0x1) != 0, pvp = (flag & 0x4) != 0, highRisk = (flag & 0x400) != 0;
                StringBuilder sb = new StringBuilder();
                String host = null, activity;
                byte b;
                while (true) {
                    if ((b = dataInputStream.readByte()) == 0) {
                        if (host == null) {
                            host = sb.toString();
                            sb = new StringBuilder();
                        } else {
                            activity = sb.toString();
                            break;
                        }
                    } else {
                        sb.append((char) b);
                    }
                }
                worldList.put(world, new World(world, flag, member, pvp, highRisk, host, activity, dataInputStream.readByte() & 0xFF, dataInputStream.readShort()));
            }
        }

        return worldList;
    }


    public static class World {

        private final int id;
        private final boolean member;
        private final boolean pvp;
        private final boolean highRisk;
        private final boolean deadman;
        private final String host;
        private final String activity;
        private final int serverLoc;
        private final int playerCount;
        private final int flag;

        public World(int id, int flag, boolean member, boolean pvp, boolean highRisk, String host, String activity, int serverLoc, int playerCount) {
            this.id = id;
            this.flag = flag;
            this.member = member;
            this.pvp = pvp;
            this.highRisk = highRisk;
            this.host = host;
            this.activity = activity;
            this.serverLoc = serverLoc;
            this.playerCount = playerCount;
            deadman = activity.toLowerCase().contains("deadman");
        }

        public int getId() {
            return id;
        }

        public int getFlag() {
            return flag;
        }

        public boolean isMember() {
            return member;
        }

        public boolean isPvp() {
            return pvp;
        }

        public boolean isHighRisk() {
            return highRisk;
        }

        public boolean isDeadman() {
            return deadman;
        }

        public String getHost() {
            return host;
        }

        public String getActivity() {
            return activity;
        }

        public int getServerLoc() {
            return serverLoc;
        }

        public int getPlayerCount() {
            return playerCount;
        }

        @Override
        public String toString() {
            return "[World " + id + " | " + playerCount + " players | " + (member ? "Members" : "F2P") + " | " + activity + "]\n";
        }

    }
}
