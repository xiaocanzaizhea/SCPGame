package xyz.xiaocan.scpEntity;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.xiaocan.configload.option.Card;
import xyz.xiaocan.configload.option.RoleTemplate;
import xyz.xiaocan.scpgame.SCPMain;
import xyz.xiaocan.scpitemstacks.ItemManager;

@Getter
@Setter
public abstract class SCPEntity extends GameEntity{

    // SCP特有属性
    // scp移动后就不能回复血量但是可以回复护盾
    // scp受伤后不能回复血量和护盾
    private boolean canHeal;
    private double healHpCount;
    private double healHpNeedTime;
    private long lastMoveTime = -1;  //用于自然恢复血量

    private boolean canRecover = false;
    private double recoverShieldCount;
    private double recoverShieldNeedTime;
    private long lastDamagedTime = -1;  //用于自然恢复护盾和血量

    private BukkitTask healingTask;

    public SCPEntity(Player player, RoleTemplate roleTemplate) {
        super(player, roleTemplate);
        this.healHpCount = roleTemplate.getHealHpCount();
        this.healHpNeedTime = roleTemplate.getHealHpNeedTime();

        this.recoverShieldCount = roleTemplate.getRecoverShieldCount();
        this.recoverShieldNeedTime = roleTemplate.getRecoverShieldNeedTime();

        this.maxShield = roleTemplate.getMaxShield();
        this.shield = maxShield;
    }

    public void giveSCPCardsItem(){
        ItemManager itemManager = ItemManager.getInstance();

        Card card = itemManager.getCard("private");

        this.player.getInventory().addItem(card.createCardItemStack());
    }

    @Override
    protected void onDeathCleanup(){
        if(healingTask != null) {
            if(!healingTask.isCancelled()) {
                healingTask.cancel();
            }
            healingTask = null;
        }
    }

    @Override
    public void update(){
        // 回血回盾任务创建
        if(healingTask == null){  //1、移动取消回血 2、要站立不动一段时间才可以回血
            healingTask = new BukkitRunnable(){
                boolean isMove = false;
                boolean isDamaged = false;

                @Override
                public void run() {

                    long currentTime = System.currentTimeMillis();

                    isMove = isOnCooldown(currentTime,
                            getLastMoveTime(),
                            (long)(getHealHpNeedTime() * 1000));

                    isDamaged = isOnCooldown(currentTime,
                            getLastDamagedTime(),
                            (long)(getRecoverShieldNeedTime() * 1000));

                    setCanHeal(!isDamaged && !isMove);
                    setCanRecover(!isDamaged);

                    if (canHeal && hp < maxHp) {
                        setHp(hp + healHpCount);
//                        player.sendRawMessage("§a♥ 血量恢复中...");
                    }

                    if (canRecover && shield < maxShield) {
                        setShield(shield + recoverShieldCount);
//                        player.sendRawMessage("§b🛡 护盾恢复中...");
                    }
                }
            }.runTaskTimer(SCPMain.getInstance(),0l,20l);
        }
    }

    public boolean isOnCooldown(long currentTime, long lastTime, long cooldown){
        if((currentTime - lastTime) > cooldown){
            return false;
        }
        return true;
    }

    @Override
    public void onDamaged(){
        lastDamagedTime = System.currentTimeMillis();
    }
}
