package xyz.xiaocan.scpsystems;

import xyz.xiaocan.scpsystems.phasecontroller.PhaseController;
import xyz.xiaocan.scpsystems.phasecontroller.SCPGameState;
import xyz.xiaocan.scpmanager.TeamManager;
import xyz.xiaocan.teams.roletypes.HumanType;
import xyz.xiaocan.teams.roletypes.RoleCategory;
import xyz.xiaocan.teams.roletypes.RoleType;
import xyz.xiaocan.teams.roletypes.ScpType;

import java.util.Map;

/**
 * 胜利条件检查器
 */
public class VictoryConditionChecker {
    private static VictoryConditionChecker instance;
    public VictoryConditionChecker() {

    }

    /**
     * 检查所有胜利条件
     */
    public void checkVictoryConditions() {
        Map<RoleType, Integer> map = TeamManager.getInstance().getTeamsCount();

        int dclassNumber = map.getOrDefault(HumanType.DCLASS,0);
        int scientistNumber = map.getOrDefault(HumanType.SCIENTIST,0);
        int guardNumber = map.getOrDefault(HumanType.GUARD,0);
        int nineTailFoxNumber = map.getOrDefault(HumanType.MTFSOLDIER,0)
                + map.getOrDefault(HumanType.MTFCAPTAIN,0);
        int chaosNumber = map.getOrDefault(HumanType.CHAOSGUNNER,0)
                + map.getOrDefault(HumanType.CHAOSGUNNER2,0);
        int scpMonsterNumber = getScpMonsterNumber();

        int mtf = scientistNumber + nineTailFoxNumber + guardNumber;
        int chaos = chaosNumber +  dclassNumber;

        PhaseController phaseController = PhaseController.getInstance();

        if (checkVictory(mtf , chaos + scpMonsterNumber)) {
            //mtf win
            phaseController.setCurrentGameState(SCPGameState.ENDED);
            phaseController.setWinningTeam(RoleCategory.MTF);
            return;
        }

        if (checkVictory(scpMonsterNumber, mtf + chaos)) {
            //scp win
            phaseController.setCurrentGameState(SCPGameState.ENDED);
            phaseController.setWinningTeam(RoleCategory.SCP);
            return;
        }

        if (checkVictory(chaos, mtf + scpMonsterNumber)) {
            //scp win
            phaseController.setCurrentGameState(SCPGameState.ENDED);
            phaseController.setWinningTeam(RoleCategory.CHAOS);
            return;
        }
    }

    private boolean checkVictory(int num, int num2) {
        if(num>0 && num2<=0){
            return true;
        }
        return false;
    }

    int getScpMonsterNumber() {
        int cnt = 0;

        for (ScpType scpType : ScpType.values()) {
            cnt += TeamManager.getInstance().getTeamsCount().getOrDefault(
                    scpType,0);
        }
        return cnt;
    }

    public static VictoryConditionChecker getInstance(){
        if(instance==null){
            instance = new VictoryConditionChecker();
        }
        return instance;
    }
}
