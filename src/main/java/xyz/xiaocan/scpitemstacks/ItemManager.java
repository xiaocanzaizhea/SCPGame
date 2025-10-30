package xyz.xiaocan.scpitemstacks;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import xyz.xiaocan.configload.option.Card;
import xyz.xiaocan.configload.option.Gun;
import xyz.xiaocan.configload.option.Medical;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.gun.AmmoType;
import xyz.xiaocan.scpitemstacks.medical.MedicalType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class ItemManager {
    private static ItemManager instance;
    private ItemManager(){

    }

    //所有的scpitems都存储在这里
    public Map<String, SCPItem> allScpItems = new HashMap<>();


    //存放药物类型的配置文件数据 - 模版          --后续改写,card，medical，ammo等都要改写
    public Map<MedicalType, Medical> allMedicals = new HashMap<>();

    //存放配置文件中卡片数据,根据id存放 - 模版
    public Map<String, Card> allCards = new HashMap<>();



    public static ItemManager getInstance(){
        if(instance==null)instance = new ItemManager();
        return instance;
    }

    public Card getCard(String cardId) {
        return allCards.get(cardId);
    }

    public void test(){
        for (Map.Entry<String, SCPItem> entry:allScpItems.entrySet()) {
            Bukkit.getLogger().warning(entry.getValue().toString());
        }

        Bukkit.getLogger().info("共读取了" + allScpItems.size() + "个SCPItem");
    }
}
