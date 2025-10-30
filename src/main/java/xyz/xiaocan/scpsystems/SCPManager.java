package xyz.xiaocan.scpsystems;

import lombok.Getter;
import lombok.Setter;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.ItemManager;
import xyz.xiaocan.scpsystems.phasecontroller.PhaseController;
import xyz.xiaocan.scpmanager.TabManager;
import xyz.xiaocan.scpmanager.TeamManager;

/*
    管理子系统
 */
@Getter
@Setter
public class SCPManager {
    private static SCPManager instance;
    private final SCPMain plugin;

    // 子系统管理器
    private VictoryConditionChecker victoryChecker;
    private TeamAssignmentSystem teamAssigner;
    private RespawnSystem respawnSystem;
    private EscapeSystem escapeSystem;
    private PhaseController phaseController;
    private TeamManager teamManager;
    private ItemManager itemManager;
    private TabManager tabManager;

    //  初始化
    private SCPManager(){
        this.plugin = SCPMain.getInstance();
    }

    // init主要处理循环依赖问题，在需要的创建完后，再赋值
    public void init(){
        // 初始化子系统
        this.victoryChecker = VictoryConditionChecker.getInstance();
        this.teamAssigner = TeamAssignmentSystem.getInstance();
        this.respawnSystem = RespawnSystem.getInstance();
        this.escapeSystem = EscapeSystem.getInstance();
        this.phaseController = PhaseController.getInstance();
        this.teamManager = TeamManager.getInstance();
        this.itemManager = ItemManager.getInstance();
        this.tabManager = TabManager.getInstance();
        teamManager.init();
    }

    public static SCPManager getInstance(){
        if(instance ==null){
            instance = new SCPManager();
        }
        return instance;
    }
}
