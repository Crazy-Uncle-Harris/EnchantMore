package io.github.dsh105.enchantmore;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EnchantMoreChangeMaterialTask extends BukkitRunnable {
    Block block;
    Player player;
    EnchantMoreListener listener;
    Material material;


    public EnchantMoreChangeMaterialTask(Block block, Player player, Material material, EnchantMoreListener listener) {
        this.block = block;
        this.player = player;
        this.material = material;
        this.listener = listener;
    }
    
    @Override
    public void run() {
         listener.plugin.safeSetBlock(player, block, material);

    }
}
