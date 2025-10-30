package xyz.xiaocan.scpsystems.phasecontroller;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.xiaocan.bossbar.ScpBossBar;
import xyz.xiaocan.configload.option.ScpOption;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpsystems.SCPManager;
import xyz.xiaocan.teams.SCPPlayer;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.scpsystems.RespawnSystem;
import xyz.xiaocan.scpsystems.VictoryConditionChecker;
import xyz.xiaocan.scpsystems.messageSystem.MessageManager;
import xyz.xiaocan.scpmanager.TabManager;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.teams.roletypes.RoleCategory;
import xyz.xiaocan.teams.roletypes.RoleType;
import xyz.xiaocan.tools.util;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class PhaseController {  //阶段控制器
    private static PhaseController instance;
    private SCPMain plugin;
    private SCPGameState currentGameState = SCPGameState.WAITING;

    //lobby
    private int inLobbyCurrentTime = 0;
    // 倒计时任务
    private BukkitTask Task;

    //game
    private int gameTime = 0;

    //win
    private RoleCategory winningTeam = null;


    public PhaseController(){
        this.plugin = SCPMain.getInstance();

        Task = createAndRunTask(); //创建检测任务，一秒一次
    }

    public void handlePlayerJoin(Player player) {
        try{
            tpPlayerToLobby(player);
            addBossBarToPlayer(player);

            switch (currentGameState) {
                case WAITING:
                    handleWaitingPhaseJoin(player);
                    break;
                case ACTIVE:
                    handleActivePhaseJoin(player);
                    break;
                default:
                    player.sendMessage("未知的游戏状态");
                    break;
            }
        }catch (Exception e){
            Bukkit.getLogger().severe("处理玩家加入时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handlePlayerQuit(Player player) {
        removeBossBarFromPlayer(player);

        switch (currentGameState) {
            case WAITING:
                handleWaitingPhaseQuit(player);
                break;
            case ACTIVE:
                handleActivePhaseQuit(player);
                break;
        }
    }


    private void handleWaitingPhaseJoin (Player player){
        util.initPlayerDataAdven(player);
    }

    private void handleWaitingPhaseQuit (Player player){
        //donothing
    }

    private void handleActivePhaseJoin (Player player){
        util.initPlayerDataToSpec(player);
    }

    private void handleActivePhaseQuit (Player player){
        //玩家退出数据处理
        TeamManager.getInstance().getAllPlayersMapping().remove(player.getUniqueId());
    }

    private void addBossBarToPlayer(Player player){
        try {
            ScpBossBar.getInstance().getBossBar().addPlayer(player);
            ScpBossBar.getInstance().addCurrent(1);
        } catch (Exception e) {
            Bukkit.getLogger().warning("添加BossBar到玩家 " + player.getName() + " 时出错: " + e.getMessage());
        }
    }

    private void removeBossBarFromPlayer(Player player){
        ScpBossBar.getInstance().getBossBar().removePlayer(player);
        ScpBossBar.getInstance().addCurrent(-1);
    }

    public BukkitTask createAndRunTask(){
        // 如果任务不存在或已取消，创建新任务
        if(Task == null || Task.isCancelled()){
            Task = new BukkitRunnable() {
                @Override
                public void run() {
                    startGameLoop();
                }
            }.runTaskTimer(SCPMain.getInstance(), 0L, 20L);  //
        }
        return Task;
    }

    //此方法用于检测游戏开始，胜利条件等，一秒一次
    private void startGameLoop() {
        if(gameTime>=ScpOption.getInstance().getDuration()) setCurrentGameState(SCPGameState.ENDED);

        switch(currentGameState){
            case WAITING:
                handleWAITING();
                break;
            case ACTIVE:
                handleACTIVE();
                break;
            case ENDED:
                handleEND();
                break;
        }
    }

    public void handleWAITING(){
        ScpBossBar scpBossBar = ScpBossBar.getInstance();
        ScpOption scpOption = ScpOption.getInstance();
        int inLobbyWaitTime = ScpOption.getInstance().getWaitTime();

        if(Bukkit.getOnlinePlayers().size() >= scpOption.getMinPlayers()){

            int subTime = inLobbyWaitTime - inLobbyCurrentTime;
            if(subTime==inLobbyWaitTime){
                String s = new String(ChatColor.GRAY + "人数充足，稍作等待");
                MessageManager.boardCast(s);
            }else if(subTime < 3 && subTime > 0){
                String s = new String(ChatColor.GRAY + "倒计时" + subTime);
                MessageManager.boardCast(s);
            }else if(subTime<=0){
                setCurrentGameState(SCPGameState.ACTIVE);

                //分配队伍
                SCPManager.getInstance().getTeamAssigner().autoAssignTeams(new ArrayList<>(Bukkit.getOnlinePlayers()));

                //设置bossbar
                scpBossBar.setCurrentPlayer(ScpOption.getInstance().getDuration());
                scpBossBar.setMaxPlayer(ScpOption.getInstance().getDuration());

                //tp玩家到出生点
                tpPlayerToSpawnPoint();

                //隐藏所有玩家名字
                TabManager.getInstance().setNameHiden();
                // 开始游戏的逻辑写在这,写差不多了，可能有点需要补充
                // todo
            }

            inLobbyCurrentTime++;
        }else{
            if(inLobbyCurrentTime!=0){
                //只播报一次
                String s = new String(ChatColor.GRAY + "人数不足，取消游戏");
                MessageManager.boardCast(s);
            }
            inLobbyCurrentTime=0; //人数不足，重置时间
        }
    }

    //游戏进行中
    public void handleACTIVE(){
        //检测胜利
        gameTime += 1;
        ScpBossBar.getInstance().addCurrent(-1);
        if(gameTime%5==0)VictoryConditionChecker.getInstance().checkVictoryConditions();
        ScpBossBar.getInstance().addCurrent(-1);
        //检测重生
        RespawnSystem.getInstance().reSpawnCheck();
        //检测逃脱
//        EscapeSystem.getInstance().checkEscape();
    }

    /**
     * 游戏结束该做的事情，发放奖励，结束游戏清空数据等，都放在这里处理
     */
    public void handleEND(){
        String s = new String(ChatColor.GRAY + "获胜队伍是: " + ChatColor.GOLD + winningTeam);
        MessageManager.boardCast(s);

        Bukkit.broadcastMessage("游戏结束，正在重启服务器.....");
//        Bukkit.shutdown();   //最好隔5秒以上移除，这样可以避免一些数据没有被清除，结束后应该踢出玩家再清除

        new BukkitRunnable(){
            @Override
            public void run() {
                for (Player player:Bukkit.getOnlinePlayers()) {
                    player.kickPlayer(ChatColor.RED + "需要10秒左右重启,请耐心等待");
                    Bukkit.shutdown();
                }
            }
        }.runTaskLater(SCPMain.getInstance(), 60l);

        if(Task!=null && !Task.isCancelled()){ //只会运行一次这个方法
            Task.cancel();
            Task=null;
        }
    }

    public void tpPlayerToLobby(Player player){
        try {
            ScpOption scpOption = ScpOption.getInstance();
            player.teleport(scpOption.getLobbySpawn());
        } catch (Exception e) {
            Bukkit.getLogger().warning("传送玩家 " + player.getName() + " 到大厅时出错: " + e.getMessage());
        }
    }

    /**
     * tp已分配角色玩家到他们出生点
     */
    public void tpPlayerToSpawnPoint(){
        TeamManager teamManager = TeamManager.getInstance();

        for (Map.Entry<UUID, SCPPlayer> entry : teamManager.getAllPlayersMapping().entrySet()) {
            SCPPlayer scpPlayer = entry.getValue();

            RoleType roleType = scpPlayer.getRoleType();
            if(!teamManager.getRolesTemplates().containsKey(roleType)){
                Bukkit.getLogger().warning("RoleTemplates不包含" + roleType);
                return;
            }

            RoleTemplate roleTemplate = teamManager.getRolesTemplates().get(roleType);
            scpPlayer.getPlayer().teleport(roleTemplate.getLocation());
        }
    }

    public static PhaseController getInstance(){
        if(instance==null){
            instance = new PhaseController();
        }
        return instance;
    }
}
