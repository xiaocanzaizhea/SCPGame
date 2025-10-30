package xyz.xiaocan.configload;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.xiaocan.scpgame.SCPMain;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;

public class DefaultConfigGenerate {

    private SCPMain plugin;
    DefaultConfigGenerate(){
        this.plugin = SCPMain.getInstance();
    }

    public void init(){
        File file = new File(SCPMain.getInstance().getDataFolder(), "scpOption.yml");
        File file1 = new File(SCPMain.getInstance().getDataFolder(), "messages.yml");
        File file2 = new File(SCPMain.getInstance().getDataFolder(), "teams.yml");
        File file3 = new File(SCPMain.getInstance().getDataFolder(), "killsocre.yml");
        File file4 = new File(SCPMain.getInstance().getDataFolder(), "medicals.yml");
        File file5 = new File(SCPMain.getInstance().getDataFolder(), "cards.yml");
        File file6 = new File(SCPMain.getInstance().getDataFolder(), "doors.yml");
        File file7 = new File(SCPMain.getInstance().getDataFolder(), "guns.yml");
        File file9 = new File(SCPMain.getInstance().getDataFolder(), "items.yml");

        //此文件夹存放scp设置
        File scpSettingsFolder = new File(SCPMain.getInstance().getDataFolder(), "scp_settings");
        if (!scpSettingsFolder.exists()) {
            boolean created = scpSettingsFolder.mkdirs();
            if (created) {
                Bukkit.getLogger().info("成功创建 SCP 设置文件夹");
            } else {
                Bukkit.getLogger().warning("创建 SCP 设置文件夹失败");
            }
        }
        File file8 = new File(scpSettingsFolder, "scpspeicalsetting.yml");

        checkAndGenerate(file,this::generateScpOptionDefaultConfig);
        checkAndGenerate(file1,this::generateMessagesDefaultConfig);
        checkAndGenerate(file2,this::generateTeamsDefaultConfig);
        checkAndGenerate(file3,this::generateKillScoreConfig);
        checkAndGenerate(file4,this::generateMedicalSuppliesConfig);
        checkAndGenerate(file5,this::generateCardConfig);
        checkAndGenerate(file6,this::generateDoorsConfig);
        checkAndGenerate(file7,this::generateGunConfig);
        checkAndGenerate(file8,this::generateSCPConfig);
        checkAndGenerate(file9,this::generateItemConfig);
    }
    public void checkAndGenerate(File configFile, Consumer<File> generator){
        if(!configFile.exists()){
            generator.accept(configFile);
        }
    }

    public void generateScpOptionDefaultConfig(File configFile){
        FileConfiguration config = new YamlConfiguration();

        config.set("arena-name", "SCP游戏");
        config.set("min-players", 6);
        config.set("max-players", 24);

        config.set("game.duration", 1200);
        config.set("game.respawn-time", 120.0);
        config.set("game.wait-time", 10);
        config.set("game.allow-respawn", true);
        config.set("game.friendly-fire", true);
        config.set("game.auto-balance", true);

        config.set("world.border-size", 500);
        config.set("world.allow-pvp", true);
        config.set("world.allow-pve", true);

        ConfigurationSection debug = config.createSection("debug");
        debug.set("debug", false);

        ConfigurationSection gun = config.createSection("gun");
        gun.set("gunParticalStart", 0.05);

        setupSpawnPoint(config, "lobby-spawn",
                61.0, 122.0, -140.0, 0.0f, 0.0f);
        setupSpawnPoint(config,"escape-location",
                0.5,70.0,0.5,0.0f,0.0f);

        saveConfigFile(config, configFile, "竞技场");
    }

    private void generateMessagesDefaultConfig(File configFile) {
        FileConfiguration config = new YamlConfiguration();

        config.set("join", "&a玩家 {player} 加入了游戏");
        config.set("quit", "&c玩家 {player} 离开了游戏");
        config.set("victory", "&6{team} 队伍获得了胜利!");
        config.set("game-start", "&e游戏开始!");
        config.set("game-end", "&e游戏结束!");
        config.set("respawn-in", "&a{time}秒后复活");

        config.set("errors.insufficient-players", "&c玩家不足，无法开始游戏");
        config.set("errors.already-in-game", "&c你已经在游戏中");

        saveConfigFile(config, configFile, "消息");
    }

    private void generateTeamsDefaultConfig(File configFile) {
        FileConfiguration config = new YamlConfiguration();

        ConfigurationSection teamsSection = config.createSection("teams");
        World world = Bukkit.getWorlds().get(0);
        Location location = new Location(Bukkit.getWorlds().get(0), 100, 64, 200, -90f, 0f);

        Location chaoslocation = new Location(world, -69.0, -5.0, -102.0);
        //chaos步枪手
        setTeamConfig(teamsSection,"chaos-gunner", chaoslocation,
                100.0f,75.0,null,null, null, null,
                0.2f,10.0f,"混沌分裂者步枪手","GREEN", Arrays.asList("chaos", "dclass"), "chaos");
        //chaos机枪手
        setTeamConfig(teamsSection,"chaos-gunner2", chaoslocation,
                100.0f,75.0,null,null, null, null,
                0.2f,10.0f,"混沌分裂者机枪手","GREEN", Arrays.asList("chaos", "dclass"), "chaos");
        //dclass
        setTeamConfig(teamsSection,"dclass", new Location(world, 6.0, 52.0, 10.0),
                100.0f,75.0,null,null, null, null,
                0.2f,10.0f,"D级人员","GOLD", Arrays.asList("chaos", "dclass"), "chaos");
        //科学家
        setTeamConfig(teamsSection,"scientist", new Location(world, 5.0, 52.0, -128.0),
                100.0f,75.0,null,null, null, null,
                0.2f,10.0f,"科学家","WHITE", Arrays.asList("science", "mtf-captain", "mtf-soldier", "guard"), "mtf");

        Location mtflocation = new Location(world, -42.0, -5.0, -151.0);
        //mtf
        setTeamConfig(teamsSection,"mtf-captain", mtflocation,
                100.0f,75.0,null,null, null, null,
                0.2f,10.0f,"九尾狐指挥官","BLUE", Arrays.asList("science", "mtf-captain", "mtf-soldier", "guard"), "mtf");
        //mtf2
        setTeamConfig(teamsSection,"mtf-soldier", mtflocation,
                100.0f,75.0,null,null, null, null,
                0.2f,10.0f,"九尾狐列兵","BLUE", Arrays.asList("science", "mtf-captain", "mtf-soldier", "guard"), "mtf");
        //guard
        setTeamConfig(teamsSection, "guard", new Location(world, 83.0, 52.0, -51.0),
                100.0f, 75.0,null,null, null, null,
                0.2f,10.0f,"警卫","BLUE", Arrays.asList("science", "mtf-captain", "mtf-soldier", "guard"), "mtf");
        //spec
        setTeamConfig(teamsSection, "spec", location,
                100.0f, 75.0,null,null, null, null,
                0.2f,10.0f,"观察者","BLUE", Arrays.asList(), "mtf");
        //scp049
        setTeamConfig(teamsSection, "scp049", new Location(world, -106.0, 83.0, -83.0),
                1000.0f, 1000.0f,3.0,15.0, 10.0,5.0,
                0.2f, 10.0f,"乌鸦","RED", Arrays.asList("scp173", "scp049"), "scp");
        //scp173
        setTeamConfig(teamsSection, "scp173", new Location(world, -104.0, 78.0, -104.0),
                1000.0f, 1000.0, 3.0,15.0, 10.0,5.0,
                0.2f,10.0f, "花生","RED", Arrays.asList("scp173", "scp173"), "scp");

        saveConfigFile(config, configFile, "队伍配置");
    }

    private void generateKillScoreConfig(File configFile){
        FileConfiguration config = new YamlConfiguration();

        ConfigurationSection killsocres = config.createSection("killscores");

        //dclass
        setKillScore(killsocres, "dclass", null,null,1.0,1.0,
                null,10.0,100.0,10.0,null);
        //chaos-gunner
        setKillScore(killsocres,"chaos-gunner", null,null,1.0,1.0,
                -1.0,10.0,100.0,10.0,10.0);
        //chaos-gunner2
        setKillScore(killsocres,"chaos-gunner2", null,null,1.0,1.0,
                -1.0,10.0,100.0,10.0,null);
        //mtf-soldier
        setKillScore(killsocres,"mtf-soldier", 1.0,1.0,null,null,
                10.0,-1.0,100.0,10.0,10.0);
        //mtf-captain
        setKillScore(killsocres,"mtf-captain", 1.0,1.0,null,null,
                10.0,-1.0,100.0,10.0,10.0);
        //guard
        setKillScore(killsocres,"guard", 1.0,1.0,null,null,
                10.0,-1.0,100.0,10.0,10.0);
        //scientist
        setKillScore(killsocres,"scientist", 1.0,1.0,null,null,
                10.0,-1.0,100.0,10.0,null);
        //scp173
        setKillScore(killsocres, "scp173",1.0,1.0,1.0,1.0,
                10.0,10.0,-1.0,10.0,null);
        //scp049
        setKillScore(killsocres,"scp049", 1.0,1.0,1.0,1.0,
                10.0,10.0,-1.0,10.0,null);

        saveConfigFile(config, configFile, "击杀得分");
    }

    private void generateMedicalSuppliesConfig(File configFile){
        FileConfiguration config = new YamlConfiguration();

        ConfigurationSection medical = config.createSection("medicals");

        //medicalbag
        setMedical(medical, "medicalbag", "医疗包", 1.0, 50.0, null, null);
        //painkiller
        setMedical(medical, "painkiller", "止痛药", 2.0,50.0, null,10.0);
        //stimulant
        setMedical(medical, "stimulant", "肾上腺素", 2.0,null,50.0, null);

        saveConfigFile(config, configFile,"医疗物品");
    }

    public void generateCardConfig(File configFile){
        FileConfiguration config = new YamlConfiguration();

        ConfigurationSection cards = config.createSection("cards");

        //紫卡-清洁工
        setCards(cards,"janitor","清洁工钥匙卡","PAPER",List.of(1,0,0),0);
        //黄卡-科学家
        setCards(cards, "scientist","科学家钥匙卡","PAPER", List.of(2,0,0),1);
        //橙卡-研究员
        setCards(cards, "researcher","研究主管钥匙卡","PAPER", List.of(2,0,1),2);
        //绿卡-区域卡
        setCards(cards, "zone","区域总监钥匙卡","PAPER", List.of(1,0,1),3);
        //灰卡-安保卡
        setCards(cards, "guard","设施警卫钥匙卡","PAPER", List.of(1,1,1),4);
        //青卡-新兵卡
        setCards(cards, "private","九尾狐列兵钥匙卡","PAPER", List.of(2,2,2),5);
        //绿卡-收容工程师
        setCards(cards, "engineer","收容工程师钥匙卡","PAPER", List.of(3,0,1),6);
        //浅蓝卡-九尾狐特工
        setCards(cards, "mtfspy","MTF特工钥匙卡","PAPER", List.of(2,2,1),7);
        //蓝卡-指挥官卡
        setCards(cards, "commander","九尾狐指挥官钥匙卡","PAPER", List.of(2,3,2),8);
        //红卡-设施卡
        setCards(cards, "facility","设施总监钥匙卡","PAPER", List.of(3,0,3),9);
        //混沌-解码器
        setCards(cards, "decoder","混沌分裂者破译装置","PAPER", List.of(2,3,2),10);
        //黑卡-O5议会卡
        setCards(cards, "o5","O5钥匙卡","PAPER", List.of(3,3,3),11);


        saveConfigFile(config, configFile,"卡片模版");
    }
    public void generateItemConfig(File configFile){
        FileConfiguration config = new YamlConfiguration();

        ConfigurationSection cards = config.createSection("items");

        //手雷设置
        setGrenade(cards, "grenade", "破片手雷", 80.0, 5.0,3.0);
        setSmokeGrenade(cards, "smokegrenade", "烟雾弹", 20.0, 5.0,2.0);

        saveConfigFile(config, configFile,"物品列表");
    }
    public void generateDoorsConfig(File configFile){
        FileConfiguration config = new YamlConfiguration();

        ConfigurationSection doors = config.createSection("doors");

        //doortemp
        setDoors(doors, "doortemp", 0.8,
                0.5,0.5,Arrays.asList(1,1,1));

        saveConfigFile(config, configFile,"门模版");
    }

    public void generateGunConfig(File configFile){
        FileConfiguration config = new YamlConfiguration();

        ConfigurationSection guns = config.createSection("guns");
        ConfigurationSection ammo = config.createSection("ammo");

        //gun
        setGun(guns, "com15","COM-15",25.0,4.0,
                "a919",  0.1, 12, "ITEM_CROSSBOW_SHOOT", "LAVA", 0,
                0.2, 0.7);
        setGun(guns, "com18","COM-18",21.2,4.0,
                "a919",  0.1, 15, "ITEM_CROSSBOW_SHOOT", "LAVA", 1,
                0.2, 0.7);
        setGun(guns, "crossvec","Crossvec冲锋枪",23.0,4.25,
                "a919",  0.05, 40, "ITEM_CROSSBOW_SHOOT", "LAVA", 2,
                0.2, 0.7);
        setGun(guns, "com45","COM-45",75.0,3,
                "a919",  0.1, 12, "ITEM_CROSSBOW_SHOOT", "LAVA", 100,
                0.2, 0.7);
        setGun(guns, ".44",".44左轮手枪",58.0,6,
                "a44",  0.5, 6, "ITEM_CROSSBOW_SHOOT", "LAVA", 10,
                0.1, 0.3);

        //ammo
        setAmmo(ammo,"a919", "9✖19毫米弹药", 70, 50);
        setAmmo(ammo,"a556", "5.56✖45毫米弹药", 40, 50);
        setAmmo(ammo,"a762", "7.62✖39毫米弹药", 40, 50);
        setAmmo(ammo,"a12", "12/70弹药", 14, 50);
        setAmmo(ammo,"a44", ".44Mag", 18, 50);

        saveConfigFile(config, configFile, "枪械和弹药列表");
    }

    public void generateSCPConfig(File configFile){
        FileConfiguration config = new YamlConfiguration();

        ConfigurationSection scp049 = config.createSection("scp049");

        ConfigurationSection scp173 = config.createSection("scp173");

        setSCP049(scp049,"scp049", 176.0, 22.0, 5,
                30.0,30.0, 0.1,
                3.0,700, 20.0,20.0,
                5.0);

        setSCP173(scp173, "scp173",200.0, 3.0, 3.0,6,20.0
        ,100.0,20.0,15.0,0.2, 4, 0.5);

        saveConfigFile(config, configFile, "scp的特殊设置");
    }

    public void setSCP049(ConfigurationSection key, String id,
                          double damage, double damageDuringTime, double attackCooldown,
                            double fSkillDuringTime, double fSkillCooldown, double speedAdd,
                              double rSkillRadius,double totalShield, double rSkillDuringTime, double rSkillColldown,
                                double helpTime){
        key.set("id", id);

        ConfigurationSection normalAttack = key.createSection("normalattack");
        normalAttack.set("damage", damage);
        normalAttack.set("damageDuringTime", damageDuringTime);
        normalAttack.set("attackCooldown", attackCooldown);

        ConfigurationSection fSkill = key.createSection("fskill");
        fSkill.set("fSkillDuringTime", fSkillDuringTime);
        fSkill.set("fSkillCooldown", fSkillCooldown);
        fSkill.set("fSkillSpeedAdd", speedAdd);

        ConfigurationSection rSkill = key.createSection("rskill");
        rSkill.set("rSkillRadius", rSkillRadius);
        rSkill.set("totalShield", totalShield);
        rSkill.set("rSkillDuringTime", rSkillDuringTime);
        rSkill.set("rSkillColldown", rSkillColldown);

        key.set("helpTime", helpTime);
    }

    private void setSCP173(ConfigurationSection key, String id,
                           double damage, double radius,
                             double cdOfTeleport, double teleportDistance,
                               double cdOfMud, double mudDuringTime,
                                 double cdOfHighSpeed, double highSpeedDuringTime,
                                    double highSpeedAdd, double highSpeedDistanceAdd, double percentOfTeleportTime){
        key.set("id", id);
        key.set("damage", damage);
        key.set("radius", radius);

        ConfigurationSection tp = key.createSection("tp");
        tp.set("cdOfTeleport", cdOfTeleport);
        tp.set("teleportDistance", teleportDistance);

        ConfigurationSection mud = key.createSection("mud");
        mud.set("cdOfMud", cdOfMud);
        mud.set("mudDuringTime", mudDuringTime);

        ConfigurationSection highspeed = key.createSection("highspeed");
        highspeed.set("cdOfHighSpeed", cdOfHighSpeed);
        highspeed.set("highSpeedDuringTime", highSpeedDuringTime);
        highspeed.set("highSpeedAdd", highSpeedAdd);
        highspeed.set("highSpeedDistanceAdd", highSpeedDistanceAdd);
        highspeed.set("percentOfTeleportTime", percentOfTeleportTime);

    }
    public void setGun(ConfigurationSection key, String id, String disPlayName,
                       double damage, double reloadTime, String AmmoTypeId, double rateOfFire, int maxAmmo,
                       String sound, String partical, int customModelData,
                       double aimingAccuracy, double waistShootAccuracy){
        ConfigurationSection key2 = key.createSection(id);
        key2.set("id", id);
        key2.set("disPlayName", disPlayName);
        key2.set("damage", damage);
        key2.set("reloadTime", reloadTime);
        key2.set("ammoType", AmmoTypeId);
        key2.set("rateOfFire", rateOfFire);
        key2.set("maxAmmo", maxAmmo);
        key2.set("sound", sound);
        key2.set("partical", partical);
        key2.set("customModelData", customModelData);
        key2.set("aimingAccuracy", aimingAccuracy);
        key2.set("waistShootAccuracy", waistShootAccuracy);
    }
    public void setAmmo(ConfigurationSection key, String id, String disPlayName, int maxAmmoTake, double maxDistance){
        ConfigurationSection key2 = key.createSection(id);
        key2.set("id", id);
        key2.set("disPlayName", disPlayName);
        key2.set("maxAmmoTake", maxAmmoTake);
        key2.set("maxDistance", maxDistance);
    }
    public void setGrenade(ConfigurationSection key, String id, String disPlayName, double damage, double radius, double explosionTime){
        ConfigurationSection key2 = key.createSection(id);
        key2.set("id", id);
        key2.set("disPlayName", disPlayName);
        key2.set("damage", damage);
        key2.set("radius", radius);
        key2.set("explosionTime", explosionTime);
    }

    public void setSmokeGrenade(ConfigurationSection key, String id, String disPlayName, double smokeDuration, double radius, double explosionTime){
        ConfigurationSection key2 = key.createSection(id);
        key2.set("id", id);
        key2.set("disPlayName", disPlayName);
        key2.set("smokeDuration", smokeDuration);
        key2.set("radius", radius);
        key2.set("explosionTime", explosionTime);
    }

    public void setDoors(ConfigurationSection key, String id, double animationtime, double boxdisappeartime_scale,
                         double boxappeartime_scale, List<Integer> permissionsLevel){
        ConfigurationSection key2 = key.createSection(id);
        key2.set("id", id);
        ConfigurationSection key3 = key2.createSection("animtime");
        key3.set("animationtime", animationtime);
        key3.set("boxdisappeartime_scale", boxdisappeartime_scale);
        key3.set("boxappeartime_scale", boxappeartime_scale);

        ConfigurationSection key4 = key2.createSection("soundeffect");
        key4.set("open", "BLOCK_IRON_DOOR_OPEN");
        key4.set("closed", "BLOCK_IRON_DOOR_CLOSE");
        key4.set("fail", "ENTITY_VILLAGER_NO");

        key2.set("permissionsLevel", permissionsLevel);
    }

    public void setCards(ConfigurationSection key, String id, String disPlayName, String material, List<Integer> permissionsLevel, int custommodeldata){
        ConfigurationSection key2 = key.createSection(id);
        key2.set("id",id);
        key2.set("disPlayName", disPlayName);
        key2.set("material", material);
        key2.set("permissionsLevel", permissionsLevel);
        key2.set("custommodeldata", custommodeldata);
    }
    public void setMedical(ConfigurationSection key, String id, String disPlayName, Double usageTime, Double healingHp, Double healingShield, Double duringTime){
        ConfigurationSection key2 = key.createSection(id);
        key2.set("id", id);
        key2.set("disPlayName", disPlayName);
        key2.set("usageTime", usageTime);
        key2.set("healingHp", healingHp);
        if(duringTime!=null) key2.set("duringTime", duringTime);
        if(healingShield!=null) key2.set("healingShield", healingShield);
    }

    public void setKillScore(ConfigurationSection key, String id,Double chaos_gunner,Double chaos_gunner2, Double mtf_soldier,
                             Double mtf_captain, Double d, Double scientist, Double scp, Double winscore, Double speicalsocre){
        ConfigurationSection key2 = key.createSection(id);
        key2.set("id",id);
        key2.set("chaos_gunner", chaos_gunner!=null ? chaos_gunner : null);
        key2.set("chaos_gunner2", chaos_gunner2!=null ? chaos_gunner2 : null);
        key2.set("mtf_soldier", mtf_soldier!=null ? mtf_soldier : null);
        key2.set("mtf_captain", mtf_captain!=null ? mtf_captain : null);
        key2.set("d", d!=null ? d : null);
        key2.set("scientist", scientist!=null ? scientist : null);
        key2.set("scp", scp!=null ? scp : null);
        key2.set("winscore", winscore!=null ? winscore : null);
        key2.set("speicalscore", speicalsocre!=null ? speicalsocre : null);

    }

    public void setTeamConfig(ConfigurationSection key, String id, Location location,
                              double maxHp, double maxShield, Double healHpCount, Double recoverShieldCount, Double healHpNeedTime, Double recoverShieldNeedTime,
                              double moveSpeed, double armor, String disPlayName, String Color, List<String> friendTeamsId, String camp){
        ConfigurationSection key2 = key.createSection(id);
        key2.set("id", id);
        key2.set("displayName", disPlayName);

        ConfigurationSection hpKey = key2.createSection("hp");
        hpKey.set("hp", maxHp);
        if(healHpCount!=null)hpKey.set("healHpCount", healHpCount);
        if(healHpNeedTime!=null)hpKey.set("healHpNeedTime", healHpNeedTime);

        ConfigurationSection shieldKey = key2.createSection("shield");
        shieldKey.set("shield", maxShield);
        if(healHpCount!=null)shieldKey.set("recoverShieldCount", recoverShieldCount);
        if(recoverShieldNeedTime!=null)shieldKey.set("recoverShieldNeedTime", recoverShieldNeedTime);

        key2.set("armor", armor);
        key2.set("movespeed", moveSpeed);
        key2.set("color", Color);
        key2.set("camp", camp);

        ConfigurationSection spawnpoint = key2.createSection("spawnpoint");
        spawnpoint.set("x", location.getX());
        spawnpoint.set("y", location.getY());
        spawnpoint.set("z", location.getZ());
        spawnpoint.set("yaw", location.getYaw());
        spawnpoint.set("pitch", location.getPitch());

        key2.set("friendTeamsId",friendTeamsId);
    }

    private void setupSpawnPoint(ConfigurationSection parent, String key,
                                 double x, double y, double z, float yaw, float pitch) {
        ConfigurationSection spawn = parent.createSection(key);
        spawn.set("x", x);
        spawn.set("y", y);
        spawn.set("z", z);
        spawn.set("yaw", yaw);
        spawn.set("pitch", pitch);
    }

    private void saveConfigFile(FileConfiguration config, File configFile, String Name) {
        try {
            if (!configFile.getParentFile().exists()) {
                configFile.getParentFile().mkdirs();
            }
            config.save(configFile);
            SCPMain.getInstance().getLogger().info("" + Name + "默认配置文件已生成: " + configFile.getName());
        } catch (IOException e) {
            SCPMain.getInstance().getLogger().log(Level.SEVERE, "无法保存配置文件: " + configFile.getName(), e);
        }
    }
}
