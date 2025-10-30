package xyz.xiaocan.configload;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.xiaocan.configload.option.*;
import xyz.xiaocan.doorsystem.DoorManager;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.ItemManager;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.scpitemstacks.gun.AmmoType;
import xyz.xiaocan.scpitemstacks.gun.GunManager;
import xyz.xiaocan.scpitemstacks.gun.GunType;
import xyz.xiaocan.scpmanager.TeamManager;

import java.io.File;
import java.util.List;

@Getter
@Setter
public class ConfigLoad {
    private final SCPMain plugin;
    public ConfigLoad() {
        this.plugin = SCPMain.getInstance();
    }
    public void init(){
        loadRoleTemplates();
        loadScpOption();
        loadKillScore();
        loadMedical();
        loadCards();
        loadDoorTemplates();
        loadGunAndAmmo();
        loadScp049Setting();
        loadScp173Setting();
        loadItems();
    }
    public void loadRoleTemplates() {
        TeamManager teamManager = TeamManager.getInstance();

        File configFile = new File(SCPMain.getInstance().getDataFolder(), "teams.yml");
        if (!configFile.exists()) {
            SCPMain.getInstance().getLogger().warning("teams.yml 配置文件不存在");
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection teamsSection = config.getConfigurationSection("teams");

        if (teamsSection == null) {
            SCPMain.getInstance().getLogger().warning("teams 配置节不存在");
            return;
        }

        for (String teamKey : teamsSection.getKeys(false)) {
            ConfigurationSection teamSection = teamsSection.getConfigurationSection(teamKey);
            if (teamSection != null) {
                RoleTemplate roleTemplate = createRoleTemplateFromConfig(teamKey, teamSection);
                if (roleTemplate != null) {
                    teamManager.getRolesTemplates().put(roleTemplate.getRoleType(), roleTemplate);
                }
            }
        }

        loadInfo(configFile.getName());
    }
    public void loadScpOption(){
        File configFile = new File(SCPMain.getInstance().getDataFolder(), "scpOption.yml");
        if (!configFile.exists()) {
            SCPMain.getInstance().getLogger().warning("scpOption.yml 配置文件不存在");
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        // 读取基本配置
        ScpOption scpOption = new ScpOption();
        scpOption.setArenaName(config.getString("arena-name", "SCP游戏"));
        scpOption.setMinPlayers(config.getInt("min-players", 6));
        scpOption.setMaxPlayers(config.getInt("max-players", 24));

        ConfigurationSection gameSection = config.getConfigurationSection("game");
        if (gameSection != null) {
            scpOption.setDuration(gameSection.getInt("duration", 1200));
            scpOption.setRespawnTime(gameSection.getDouble("respawn-time", 10.0));
            scpOption.setWaitTime(gameSection.getInt("wait-time", 30));
            scpOption.setAllowRespawn(gameSection.getBoolean("allow-respawn", true));
            scpOption.setFriendlyFire(gameSection.getBoolean("friendly-fire", true));
            scpOption.setAutoBalance(gameSection.getBoolean("auto-balance", true));
        }

        ConfigurationSection worldSection = config.getConfigurationSection("world");
        if (worldSection != null) {
            scpOption.setBorderSize(worldSection.getInt("border-size", 500));
            scpOption.setAllowPvp(worldSection.getBoolean("allow-pvp", true));
            scpOption.setAllowPve(worldSection.getBoolean("allow-pve", true));
        }

        ConfigurationSection lobbySpawnSection = config.getConfigurationSection("lobby-spawn");
        if (lobbySpawnSection != null) {
            Location lobbySpawn = createLocationFromConfig(lobbySpawnSection);
            scpOption.setLobbySpawn(lobbySpawn);
        }

        ConfigurationSection escapeSection = config.getConfigurationSection("escape-location");
        if(escapeSection!=null){
            Location escapelocation = createLocationFromConfig(escapeSection);
            scpOption.setEscapeLocation(escapelocation);
        }

        ConfigurationSection debugSection = config.getConfigurationSection("debug");
        scpOption.setDebug(debugSection.getBoolean("debug"));

        ConfigurationSection gunSection = config.getConfigurationSection("gun");
        scpOption.setGunParticalStart(gunSection.getDouble("gunParticalStart"));

        ScpOption.setInstance(scpOption);
        loadInfo(configFile.getName());
    }
    public void loadKillScore(){
        TeamManager teamManager = TeamManager.getInstance();

        File configFile = new File(SCPMain.getInstance().getDataFolder(), "killsocre.yml");
        if (!configFile.exists()) {
            SCPMain.getInstance().getLogger().warning("killscores.yml 配置文件不存在");
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection section = config.getConfigurationSection("killscores");

        for (String scoreKey: section.getKeys(false)) {
            ConfigurationSection roleScoreSection = section.getConfigurationSection(scoreKey);
            if(roleScoreSection!=null){
                KillScore killScore = createKillScoreFromConfig(scoreKey, roleScoreSection);
                if(killScore!=null){
                    teamManager.getAllKillScore().put(killScore.getRoleType(), killScore);
                }
            }
        }

        loadInfo(configFile.getName());
    }
    public void loadMedical(){
        ItemManager itemManager = ItemManager.getInstance();

        File configFile = new File(SCPMain.getInstance().getDataFolder(), "medicals.yml");
        if (!configFile.exists()) {
            SCPMain.getInstance().getLogger().warning("medicals.yml 配置文件不存在");
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection section = config.getConfigurationSection("medicals");

        for (String key: section.getKeys(false) ) {
            ConfigurationSection key2 = section.getConfigurationSection(key);
            if(key2!=null){
                Medical medical = createMedicalFromConfig(key, key2);
                if(medical!=null){
                    itemManager.getAllMedicals().put(medical.getMedicalType(), medical);
                }
            }
        }

        loadInfo(configFile.getName());
    }
    public void loadCards(){
        ItemManager itemManager = ItemManager.getInstance();

        File configFile = new File(SCPMain.getInstance().getDataFolder(), "cards.yml");
        if (!configFile.exists()) {
            SCPMain.getInstance().getLogger().warning("cards.yml 配置文件不存在");
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection section = config.getConfigurationSection("cards");

        for (String key: section.getKeys(false) ) {
            ConfigurationSection key2 = section.getConfigurationSection(key);
            if(key2!=null){
                Card card = createCardFromConfig(key, key2);
                if(card!=null){
                    itemManager.getAllCards().put(card.getId(), card);
                }
            }
        }

        loadInfo(configFile.getName());
    }
    public void loadDoorTemplates(){
        DoorManager doorManager = DoorManager.getInstance();

        File configFile = new File(SCPMain.getInstance().getDataFolder(), "doors.yml");
        if (!configFile.exists()) {
            SCPMain.getInstance().getLogger().warning("doors.yml 配置文件不存在");
            return;
        }
        //File FileConfiguration ConfigurationSection
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection section = config.getConfigurationSection("doors");

        for (String key: section.getKeys(false)) {
            ConfigurationSection key2 = section.getConfigurationSection(key);
            if(key2!=null){
                DoorTemplate doorTemplate = createDoorTemplate(key, key2);
                if(doorTemplate!=null){
                    doorManager.getDoorTemplates().put(doorTemplate.getId(), doorTemplate);
                }
            }
        }

        loadInfo(configFile.getName());
    }
    public void loadGunAndAmmo(){
        File configFile = new File(SCPMain.getInstance().getDataFolder(), "guns.yml");
        if (!configFile.exists()) {
            SCPMain.getInstance().getLogger().warning("guns.yml 配置文件不存在");
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection section = config.getConfigurationSection("guns");

        for (String key: section.getKeys(false)) {
            ConfigurationSection key2 = section.getConfigurationSection(key);
            if(key2!=null){
                Gun gun = createGun(key, key2);
                if(gun!=null){
                    GunManager.getInstance().getAllGun().
                            put(GunType.getByEnumName(gun.getId()), gun);
                }
            }
        }

        ConfigurationSection section2 = config.getConfigurationSection("ammo");
        for (String key: section2.getKeys(false)) {
            ConfigurationSection key2 = section2.getConfigurationSection(key);
            if(key2!=null){
                Ammo ammo = createAmmo(key, key2);
                if(ammo!=null){
                    GunManager.getInstance().getAllAmmo()
                            .put(AmmoType.getByEnumName(ammo.getId()), ammo);
                }
            }
        }


        loadInfo(configFile.getName());
    }
    public void loadItems(){
        ItemManager itemManager = ItemManager.getInstance();

        File configFile = new File(SCPMain.getInstance().getDataFolder(), "items.yml");
        if (!configFile.exists()) {
            SCPMain.getInstance().getLogger().warning("items.yml 配置文件不存在");
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection section = config.getConfigurationSection("items");

        ConfigurationSection grenadeSection = section.getConfigurationSection("grenade");
        Grenade grenade = createGrenade("grenade", grenadeSection);
        Grenade.setInstance(grenade);

        ConfigurationSection smokeGrenadeSection = section.getConfigurationSection("smokegrenade");
        SmokeGrenade smokeGrenade = createSmokeGrenade("grenade", smokeGrenadeSection);
        SmokeGrenade.setInstance(smokeGrenade);

        //存储
        itemManager.getAllScpItems().put(grenade.getId(),grenade);
        itemManager.getAllScpItems().put(smokeGrenade.getId(),smokeGrenade);

        loadInfo(configFile.getName());
    }
    public void loadScp049Setting(){
        File scpSettingsFolder = new File(SCPMain.getInstance().getDataFolder(), "scp_settings");
        if(!scpSettingsFolder.exists()){
            SCPMain.getInstance().getLogger().warning("scp_setting 文件夹不存在");
            return;
        }

        File configFile = new File(scpSettingsFolder, "scpspeicalsetting.yml");
        if (!configFile.exists()) {
            SCPMain.getInstance().getLogger().warning("scpspeicalsetting.yml 配置文件不存在");
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection section = config.getConfigurationSection("scp049");

        createSCP049Setting("", section);

        loadInfo(configFile.getName());
    }

    public void loadScp173Setting(){
        File scpSettingsFolder = new File(SCPMain.getInstance().getDataFolder(), "scp_settings");
        if(!scpSettingsFolder.exists()){
            SCPMain.getInstance().getLogger().warning("scp_setting 文件夹不存在");
            return;
        }

        File configFile = new File(scpSettingsFolder, "scpspeicalsetting.yml");
        if (!configFile.exists()) {
            SCPMain.getInstance().getLogger().warning("scpspeicalsetting.yml 配置文件不存在");
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection section = config.getConfigurationSection("scp173");

        createSCP173Setting("", section);

        loadInfo(configFile.getName());
    }
    private void createSCP049Setting(String key, ConfigurationSection section){
        String id = section.getString("id", key);
        double helpTime = section.getDouble("helpTime", 0.0);

        ConfigurationSection section1 = section.getConfigurationSection("normalattack");
        double damage = section1.getDouble("damage", 0.0);
        double damageDuringTime = section1.getDouble("damageDuringTime", 0.0);
        double attackCooldown = section1.getDouble("attackCooldown", 0.0);

        ConfigurationSection section2 = section.getConfigurationSection("fskill");
        double fSkillDuringTimeage = section2.getDouble("fSkillDuringTime", 0.0);
        double fSkillCooldown = section2.getDouble("fSkillCooldown", 0.0);
        double fSkillSpeedAdd = section2.getDouble("fSkillSpeedAdd", 0.0);

        ConfigurationSection section3 = section.getConfigurationSection("rskill");
        double rSkillRadius = section3.getDouble("rSkillRadius", 0.0);
        double totalShield = section3.getDouble("totalShield", 0.0);
        double rSkillDuringTime = section3.getDouble("rSkillDuringTime", 0.0);
        double rSkillColldown = section3.getDouble("rSkillColldown", 0.0);

        new SCP049SpiecalSetting(id,damage,damageDuringTime,
                attackCooldown,fSkillDuringTimeage,fSkillCooldown,fSkillSpeedAdd,
                rSkillRadius, totalShield, rSkillDuringTime,rSkillColldown,helpTime);
    }

    private void createSCP173Setting(String key, ConfigurationSection section){
        String id = section.getString("id", key);
        double damage = section.getDouble("damage", 0.0);
        double radius = section.getDouble("radius", 0.0);

        ConfigurationSection section1 = section.getConfigurationSection("tp");
        double cdOfTeleport = section1.getDouble("cdOfTeleport", 0.0);
        double teleportDistance = section1.getDouble("teleportDistance", 0.0);

        ConfigurationSection section2 = section.getConfigurationSection("mud");
        double cdOfMud = section2.getDouble("cdOfMud", 0.0);
        double mudDuringTime = section2.getDouble("mudDuringTime", 0.0);

        ConfigurationSection section3 = section.getConfigurationSection("highspeed");
        double cdOfHighSpeed = section3.getDouble("cdOfHighSpeed", 0.0);
        double highSpeedDuringTime = section3.getDouble("highSpeedDuringTime", 0.0);
        double highSpeedAdd = section3.getDouble("highSpeedAdd", 0.0);
        double highSpeedDistanceAdd = section3.getDouble("highSpeedDistanceAdd", 0.0);
        double percentOfTeleportTime = section3.getDouble("percentOfTeleportTime", 0.5);

        new SCP173SpiecalSetting(id,
                damage, radius,
                cdOfTeleport, teleportDistance,
                cdOfMud, mudDuringTime,
                cdOfHighSpeed,highSpeedDuringTime, highSpeedAdd, highSpeedDistanceAdd, percentOfTeleportTime);
    }
    private Gun createGun(String key, ConfigurationSection section){
        String id = section.getString("id", key);
        String disPlayName = section.getString("disPlayName", "--null--");
        double damage = section.getDouble("damage", 0.0);
        double reloadTime = section.getDouble("reloadTime", 0.0);
        String ammoType = section.getString("ammoType", "--null--");
        double rateOfFire = section.getDouble("rateOfFire", 0.2);
        int maxAmmo = section.getInt("maxAmmo", 0);
        String sound = section.getString("sound","--null--");
        String partical = section.getString("partical","--null--");
        int customModelData = section.getInt("customModelData",0);
        double AimingAccuracy = section.getDouble("aimingAccuracy",0.0);
        double RunningAccuracy = section.getDouble("waistShootAccuracy",0.0);

        return new Gun(id, disPlayName,damage, reloadTime, ammoType,rateOfFire, maxAmmo,
                sound, partical, customModelData, AimingAccuracy, RunningAccuracy);
    }
    private Ammo createAmmo(String key, ConfigurationSection section){
        String id = section.getString("id", key);
        String disPlayName = section.getString("disPlayName", "--null--");
        int maxAmmo = section.getInt("maxAmmoTake", 0);
        double maxDistance = section.getDouble("maxDistance",0.0);

        return new Ammo(id, disPlayName, maxAmmo, maxDistance);
    }

    private Grenade createGrenade(String key, ConfigurationSection section){
        String id = section.getString("id", key);
        String disPlayName = section.getString("disPlayName", "--null--");
        double damage = section.getDouble("damage", 0.0);
        double explosionTime = section.getDouble("explosionTime", 2.0);
        double radius = section.getDouble("radius", 5.0);

        return new Grenade(damage,radius,explosionTime,id,disPlayName);
    }
    private SmokeGrenade createSmokeGrenade(String key, ConfigurationSection section){
        String id = section.getString("id", key);
        String disPlayName = section.getString("disPlayName", "--null--");
        double duration = section.getDouble("duration", 10.0);
        double explosionTime = section.getDouble("explosionTime", 2.0);
        double radius = section.getDouble("radius", 5.0);

        return new SmokeGrenade(duration,radius,explosionTime,id,disPlayName);
    }
    private DoorTemplate createDoorTemplate(String key, ConfigurationSection section){
        String id = section.getString("id", key);
        ConfigurationSection animtime = section.getConfigurationSection("animtime");

        double animationtime = animtime.getDouble("animationtime", 0);
        double boxdisappeartime_scale = animtime.getDouble("boxdisappeartime_scale", 0);
        double boxappeartime_scale = animtime.getDouble("boxappeartime_scale", 0);

        ConfigurationSection soundeffect = section.getConfigurationSection("soundeffect");
        String open = soundeffect.getString("open", "ENTITY_ENDERMAN_TELEPORT");
        String closed = soundeffect.getString("closed", "ENTITY_ENDERMAN_TELEPORT");
        String fail = soundeffect.getString("fail", "ENTITY_ENDERMAN_TELEPORT");

        List<Integer> permissionsLevel = section.getIntegerList("permissionsLevel");

        return new DoorTemplate(id, open, closed,fail
                        ,permissionsLevel, animationtime,
                boxdisappeartime_scale, boxappeartime_scale);
    }
    private Card createCardFromConfig(String key, ConfigurationSection section){

        String id = section.getString("id", key);
        String disPlayName = section.getString("disPlayName", "--null--");
        List<Integer> permissionsLevel = section.getIntegerList("permissionsLevel");
        String material = section.getString("material", "PAPER");
        int custommodeldata = section.getInt("custommodeldata", 1000);

        return new Card(id, disPlayName, permissionsLevel, material, custommodeldata);
    }
    private Medical createMedicalFromConfig(String key, ConfigurationSection section){
        String id = section.getString("id", key);
        String disPlayName = section.getString("disPlayName", "--null--");
        double usageTime = section.getDouble("usageTime", 0);
        double healingHp = section.getDouble("healingHp", 0);
        double duringTime = section.getDouble("duringTime", 1);
        double healingShield = section.getDouble("healingShield", 0);

        return new Medical(id,disPlayName,usageTime,duringTime,healingHp, healingShield);
    }
    private RoleTemplate createRoleTemplateFromConfig(String teamKey, ConfigurationSection teamSection) {
        try {
            String id = teamSection.getString("id", teamKey);

            ConfigurationSection hpKey = teamSection.getConfigurationSection("hp");
            double hp = hpKey.getDouble("hp", 20.0);
            double healHpCount = hpKey.getDouble("healHpCount", 0.0);
            double healHpNeedTime = hpKey.getDouble("healHpNeedTime", 20.0);

            ConfigurationSection shieldKey = teamSection.getConfigurationSection("shield");
            double shield = shieldKey.getDouble("shield", 0.0);
            double recoverShieldCount = shieldKey.getDouble("recoverShieldCount", 0.0);
            double recoverShieldNeedTime = shieldKey.getDouble("recoverShieldNeedTime", 20.0);

            double moveSpeed = teamSection.getDouble("movespeed", 0.2);
            double armor = teamSection.getDouble("armor", 0.0);
            String color = teamSection.getString("color", "WHITE");
            String displayName = teamSection.getString("displayName", teamKey);
            String camp = teamSection.getString("camp", "");

            ConfigurationSection spawnSection = teamSection.getConfigurationSection("spawnpoint");
            Location location = null;
            if (spawnSection != null) {
                location = createLocationFromConfig(spawnSection);
            }

            List<String> friendTeamsId = teamSection.getStringList("friendTeamsId");

            return new RoleTemplate(id, displayName,
                    hp, shield,
                    healHpCount, healHpNeedTime, recoverShieldCount, recoverShieldNeedTime,
                    moveSpeed, armor,
                    color, location, friendTeamsId, camp);

        } catch (Exception e) {
            SCPMain.getInstance().getLogger().warning("创建角色模板失败: " + teamKey + " - " + e.getMessage());
            return null;
        }
    }
    private KillScore createKillScoreFromConfig(String key, ConfigurationSection section){
        String id = section.getString("id", key);
        double d = section.getDouble("d", 0);
        double scientist = section.getDouble("scientist",0);
        double scp = section.getDouble("scp", 0);
        double winscore = section.getDouble("winscore", 0);
        double speicalscore = section.getDouble("speicalscore", 0);

        return new KillScore(id, d, scientist, scp, winscore, speicalscore);
    }
    private Location createLocationFromConfig(ConfigurationSection section){
        World world = Bukkit.getWorlds().get(0); // 主世界
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        float yaw = (float) section.getDouble("yaw");
        float pitch = (float) section.getDouble("pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }
    private void loadInfo(String fileName){
        SCPMain.getInstance().getLogger().info(fileName + "读取成功");
    }
}
