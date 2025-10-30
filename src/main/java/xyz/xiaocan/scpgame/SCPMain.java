package xyz.xiaocan.scpgame;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.xiaocan.commands.DoorCommand;
import xyz.xiaocan.commands.GunTest;
import xyz.xiaocan.commands.ScptestCommands;
import xyz.xiaocan.commands.InteractionTest;
import xyz.xiaocan.configload.ConfigManager;
import xyz.xiaocan.configload.option.ScpOption;
import xyz.xiaocan.doorsystem.DoorManager;
import xyz.xiaocan.dropitemsystem.DropManager;
import xyz.xiaocan.scpListener.*;
import xyz.xiaocan.scpsystems.SCPManager;

@Getter
@Setter
public class SCPMain extends JavaPlugin {

    private static SCPMain instance;

    private SCPManager scpManager;

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;

        //必须先对配置文件进行初始化和读取!!
        configManager = ConfigManager.getInstance();
        scpManager = SCPManager.getInstance();

        // 配置文件的初始化，初始一个配置文件和读取配置文件
        configManager.init();
        scpManager.init();

        loadConfig();
        registerListeners();
        registerCommands();
        getLogger().info("debug Mode: " + ScpOption.getInstance().isDebug());
        getLogger().info("所有配置文件加载完成!");
    }

    @Override
    public void onDisable() {
        removeData();
    }

    public void registerListeners(){
        //注册事件
        getServer().getPluginManager().registerEvents(new SCPListener(),this);
        getServer().getPluginManager().registerEvents(new DoorListener(), this);
        getServer().getPluginManager().registerEvents(new MedicalListener(), this);
        getServer().getPluginManager().registerEvents(new GunListener(), this);
        getServer().getPluginManager().registerEvents(new Scp049Listener(), this);
        getServer().getPluginManager().registerEvents(new Scp173Listener(), this);
        getServer().getPluginManager().registerEvents(new GrenadeListener(), this);
        getServer().getPluginManager().registerEvents(new DropAndPickUpItemListener(), this);
    }

    public void registerCommands(){
        //注册命令
        getCommand("scpdoor").setExecutor(new DoorCommand());
        getCommand("test").setExecutor(new ScptestCommands());
        getCommand("gun").setExecutor(new GunTest());
        getCommand("inter").setExecutor(new InteractionTest());
    }

    public void loadConfig(){
        DoorManager.getInstance().loadDoorsInstance();  //读取门的实例
    }

    public static SCPMain getInstance(){
        return instance;
    }

    public void removeData(){
        DoorManager.getInstance().removeAllDoorData();  //移除display模型
        DropManager.getInstance().removeAllDropItem();  //移除所有掉落物
    }

}
