package xyz.xiaocan.scpitemstacks.grenade;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import xyz.xiaocan.configload.option.Grenade;
import xyz.xiaocan.scpitemstacks.SCPItem;

@Getter
@Setter
public abstract class AbstractGrenade implements SCPItem {


    protected String id;
    protected String displayName;
    protected double radius;
    protected double explosionTime;

    public AbstractGrenade(double radius, double explosionTime, String id, String displayName) {
        this.radius = radius;
        this.explosionTime = explosionTime;
        this.id = id;
        this.displayName = displayName;
    }

    @Override
    public String getItemType() {
        return "Grenade";
    }


}