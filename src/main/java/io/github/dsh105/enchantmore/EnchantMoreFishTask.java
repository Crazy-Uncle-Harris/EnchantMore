package io.github.dsh105.enchantmore;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class EnchantMoreFishTask extends BukkitRunnable {
    Player player;
    World world;

    public EnchantMoreFishTask(Player player, World world) {
        this.player = player;
        this.world = world;
    }

    @Override
    public void run() {
        ItemStack tool = player.getItemInHand();
        if (tool != null && tool.getType() == Material.FISHING_ROD) {
            world.dropItemNaturally(player.getLocation(), new ItemStack(Material.LEGACY_RAW_FISH, 1));
            //TODO UPDATE THIS TO ALL RAW FISH
            EnchantMoreListener.damage(tool, player);
        }
    }
}
