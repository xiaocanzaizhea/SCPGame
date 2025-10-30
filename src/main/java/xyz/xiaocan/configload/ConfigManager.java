package xyz.xiaocan.configload;

import lombok.Getter;
import lombok.Setter;
import xyz.xiaocan.scpgame.SCPMain;

@Getter
@Setter
public class ConfigManager {
    private SCPMain plugin;
    private static ConfigManager instance;

    private DefaultConfigGenerate defaultConfigGenerate;
    private ConfigLoad configLoad;

    private ConfigManager(){
        this.plugin = SCPMain.getInstance();
        defaultConfigGenerate = new DefaultConfigGenerate();
        configLoad = new ConfigLoad();
    }

    public void init(){
        defaultConfigGenerate.init(); //生成默认配置文件
        configLoad.init(); //读取配置文件
    }

    public static ConfigManager getInstance(){
        if(instance==null){
            instance = new ConfigManager();
        }

        return instance;
    }
}
