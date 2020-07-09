package io.github.dsh105.enchantmore;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class EnchantMore extends JavaPlugin {

    WorldGuardPlugin wg = null;
    private static EnchantMore instance;

    private WorldGuardPlugin getWorldGuard() {
        Plugin wgPlugin = this.getServer().getPluginManager().getPlugin("WorldGuard");
        if (wgPlugin == null || !(wgPlugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) wgPlugin;
    }

    public static EnchantMore getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
        PluginManager manager = this.getServer().getPluginManager();
        String fileName = getDataFolder() + System.getProperty("file.separator") + "config.yml";
        File file = new File(fileName);
        if (!file.exists()) {
            if (!newConfig(file)) {
                manager.disablePlugin(this);
                return;
            }
        }
        reloadConfig();
        manager.registerEvents(new EnchantMoreListener(this), this);
        if (this.getConfig().getBoolean("moveListener", true)) {
            manager.registerEvents(new EnchantMorePlayerMoveListener(this), this);
        }
        if (getWorldGuard() != null) {
            this.wg = this.getWorldGuard();
        }
    }

    // Copy default configuration
    // Needed because getConfig().options().copyDefaults(true); doesn't preserve comments!
    public boolean newConfig(File file) {
        FileWriter fileWriter = null;
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        try {
            fileWriter = new FileWriter(file);
        } catch (IOException e) {
            this.getLogger().severe("Could not write config file: " + e.getMessage());
            return false;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(getResource("config.yml"))));
        BufferedWriter w = new BufferedWriter(fileWriter);
        try {
            String line = reader.readLine();
            while (line != null) {
                w.write(line + System.getProperty("line.separator"));
                line = reader.readLine();
            }
            if (this.getConfig().getBoolean("verboseLogger")) {
                this.getLogger().info("Default configuration file written successfully");
            }
        } catch (IOException e) {
            this.getLogger().severe("Error writing config: " + e.getMessage());
        } finally {
            try {
                w.close();
                reader.close();
            } catch (IOException e) {
                this.getLogger().severe("Error writing config: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    public void onDisable() {
    }

    public boolean verboseLogger() { //Use verbose logger?
        if (this.getConfig().getBoolean("verboseLogger", false)) {
            return true;
        }
        return false;
    }

    /* We ignore cancelled events, but that isn't good enough for WorldGuard
    #worldguard @ irc.esper.net 2012/02/23 
> when blocks are broken in protected regions, why doesn't WorldGuard cancel the event so other plugins could just use ignoreCancelled=true to respect regions, instead of hooking into WorldGuard's API?
<zml2008> It does do that, just at a priority that is too high
> hmm, interesting. if I register my handler as priority MONITOR, I do see the event is cancelled, as expected. but what's the best practice? should I be registering all my listeners as MONITOR?
<zml2008> That's generally a terrible idea. The event priorities need to be corrected in WG
> is that something I can change in the config? or is it a bug in WorldGuard that needs to be fixed?
<zml2008> It's a WG bug
> so all plugins have to workaround it?
<zml2008> Until I have time, yes.
> what would you recommend in the meantime?
<zml2008>  Using WG's API
*/
    public boolean canPVP(Player player, Block block, Material material, Enchantment ench) { //Check if WorldGuard allows lightning
        if (wg != null) {
            World world = block.getWorld();
            Location loc = block.getLocation();
            RegionManager regionManager = wg.getRegionManager(world);
            ApplicableRegionSet set = regionManager.getApplicableRegions(loc);
            if (!set.allows(DefaultFlag.PVP)) {
                String packed = EnchantMoreListener.packEnchItem(material, ench);
                if (verboseLogger()) {
                    this.getLogger().info("Effect " + material.name() + " + " + ench + " = " + packed + " blocked by WorldGuard.");
                }
                player.sendMessage(ChatColor.GOLD + "[EnchantMore] " + ChatColor.RED + "Effect " +material.name() + " + " + ench + " = " + packed + " blocked by WorldGuard.");
            }
            return set.allows(DefaultFlag.PVP);
        }
        return true;
    }

    public boolean canPVP(Player player, Location loc, Material material, Enchantment ench) { //Check if WorldGuard allows lightning
        if (wg != null) {
            World world = loc.getWorld();
            RegionManager regionManager = wg.getRegionManager(world);
            ApplicableRegionSet set = regionManager.getApplicableRegions(loc);
            if (!set.allows(DefaultFlag.PVP)) {
                String packed = EnchantMoreListener.packEnchItem(material, ench);
                if (verboseLogger()) {
                    this.getLogger().info("Effect " + material.name()  + " + " + ench + " = " + packed + " blocked by WorldGuard.");
                }
                player.sendMessage(ChatColor.GOLD + "[EnchantMore] " + ChatColor.RED + "Effect " + material.name() + " + " + ench + " = " + packed + " blocked by WorldGuard.");
            }
            return set.allows(DefaultFlag.PVP);
        }
        return true;
    }

    public boolean canStrikeLightning(Player player, Block block, Material material, Enchantment ench) { //Check if WorldGuard allows lightning
        if (wg != null) {
            World world = block.getWorld();
            Location loc = block.getLocation();
            RegionManager regionManager = wg.getRegionManager(world);
            ApplicableRegionSet set = regionManager.getApplicableRegions(loc);
            if (!set.allows(DefaultFlag.LIGHTNING)) {
                String packed = EnchantMoreListener.packEnchItem(material, ench);
                if (verboseLogger()) {
                    this.getLogger().info("Effect " + material.name()  + " + " + ench + " = " + packed + " blocked by WorldGuard.");
                }
                player.sendMessage(ChatColor.GOLD + "[EnchantMore] " + ChatColor.RED + "Effect " +material.name() + " + " + ench + " = " + packed + " blocked by WorldGuard.");
            }
            return set.allows(DefaultFlag.LIGHTNING);
        }
        return true;
    }

    public boolean canStrikeLightning(Player player, Location loc, Material material, Enchantment ench) { //Check if WorldGuard allows lightning
        if (wg != null) {
            World world = loc.getWorld();
            RegionManager regionManager = wg.getRegionManager(world);
            ApplicableRegionSet set = regionManager.getApplicableRegions(loc);
            if (!set.allows(DefaultFlag.LIGHTNING)) {
                String packed = EnchantMoreListener.packEnchItem(material, ench);
                if (verboseLogger()) {
                    this.getLogger().info("Effect " + material.name()  + " + " + ench + " = " + packed + " blocked by WorldGuard.");
                }
                player.sendMessage(ChatColor.GOLD + "[EnchantMore] " + ChatColor.RED + "Effect " + material.name() + " + " + ench + " = " + packed + " blocked by WorldGuard.");
            }
            return set.allows(DefaultFlag.LIGHTNING);
        }
        return true;
    }

    public boolean canExplode(Player player, Block block, Material material, Enchantment ench) { //Check if WorldGuard allows an explosion at a location
        if (wg != null) {
            World world = block.getWorld();
            Location loc = block.getLocation();
            RegionManager regionManager = wg.getRegionManager(world);
            ApplicableRegionSet set = regionManager.getApplicableRegions(loc);
            if (!(set.allows(DefaultFlag.OTHER_EXPLOSION))) {
                String packed = EnchantMoreListener.packEnchItem(material, ench);
                if (verboseLogger()) {
                    this.getLogger().info("Effect " + material.name() + " + " + ench + " = " + packed + " blocked by WorldGuard.");
                }
                player.sendMessage(ChatColor.GOLD + "[EnchantMore] " + ChatColor.RED + "Effect " +material.name() +" + " + ench + " = " + packed + " blocked by WorldGuard.");
            }
            return set.allows(DefaultFlag.OTHER_EXPLOSION);
        }
        return true;
    }

    public boolean canExplode(Player player, Location loc, Material material, Enchantment ench) { //Check if WorldGuard allows an explosion at a location
        if (wg != null) {
            World world = loc.getWorld();
            RegionManager regionManager = wg.getRegionManager(world);
            ApplicableRegionSet set = regionManager.getApplicableRegions(loc);
            if (!(set.allows(DefaultFlag.OTHER_EXPLOSION))) {
                String packed = EnchantMoreListener.packEnchItem(material, ench);
                if (verboseLogger()) {
                    this.getLogger().info("Effect " + material.name() + " + " + ench + " = " + packed + " blocked by WorldGuard.");
                }
                player.sendMessage(ChatColor.GOLD + "[EnchantMore] " + ChatColor.RED + "Effect " + material.name() + " + " + ench + " = " + packed + " blocked by WorldGuard.");
            }
            return set.allows(DefaultFlag.OTHER_EXPLOSION);
        }
        return true;
    }

    public boolean canDropItem(Player player, Block block, Material material, Enchantment ench) { //Check if WorldGuard allows dropping an item at a block's location
        if (wg != null) {
            World world = block.getWorld();
            Location loc = block.getLocation();
            RegionManager regionManager = wg.getRegionManager(world);
            ApplicableRegionSet set = regionManager.getApplicableRegions(loc);
            if (!(set.allows(DefaultFlag.ITEM_DROP))) {
                String packed = EnchantMoreListener.packEnchItem(material, ench);
                if (verboseLogger()) {
                    this.getLogger().info("Effect " + material.name() + " + " + ench + " = " + packed + " blocked by WorldGuard.");
                }
                player.sendMessage(ChatColor.GOLD + "[EnchantMore] " + ChatColor.RED + "Effect " + material.name() + " + " + ench + " = " + packed + " blocked by WorldGuard.");
            }
            return set.allows(DefaultFlag.ITEM_DROP);
        }
        return true;
    }

    public boolean canDropItem(Player player, Location loc, Material material, Enchantment ench) { //Check if WorldGuard allows dropping an item at a location
        if (wg != null) {
            World world = loc.getWorld();
            RegionManager regionManager = wg.getRegionManager(world);
            ApplicableRegionSet set = regionManager.getApplicableRegions(loc);
            if (!(set.allows(DefaultFlag.ITEM_DROP))) {
                String packed = EnchantMoreListener.packEnchItem(material, ench);
                if (verboseLogger()) {
                    this.getLogger().info("Effect " + material.name() + " + " + ench + " = " + packed + " blocked by WorldGuard.");
                }
                player.sendMessage(ChatColor.GOLD + "[EnchantMore] " + ChatColor.RED + "Effect " + material.name() + " + " + ench + " = " + packed + " blocked by WorldGuard.");
            }
            return set.allows(DefaultFlag.ITEM_DROP);
        }
        return true;
    }

    public boolean canBuild(Player player, Location loc, Material material, Enchantment ench) {
        if (wg != null) {
            if (loc == null) {
                return true;
            }
            if (!wg.canBuild(player, loc)) {
                String packed = EnchantMoreListener.packEnchItem(material, ench);
                if (verboseLogger()) {
                    this.getLogger().info("Effect " + material.name() +  " + " + ench + " = " + packed + " blocked by WorldGuard.");
                }
                player.sendMessage(ChatColor.GOLD + "[EnchantMore] " + ChatColor.RED + "Effect " + material.name() + " + " + ench + " = " + packed + " blocked by WorldGuard.");
            }
            return wg.canBuild(player, loc);
        }
        return true;
    }

    public boolean canBuild(Player player, Block block,Material material, Enchantment ench) {
        if (wg != null) {
            if (block == null) {
                return true;
            }
            if (!wg.canBuild(player, block)) {
                String packed = EnchantMoreListener.packEnchItem(material, ench);
                if (verboseLogger()) {
                    this.getLogger().info("Effect " + material.name() + " + " + ench + " = " + packed + " blocked by WorldGuard.");
                }
                player.sendMessage(ChatColor.GOLD + "[EnchantMore] " + ChatColor.RED + "Effect " +material.name() + " + " + ench + " = " + packed + " blocked by WorldGuard.");
            }
            return wg.canBuild(player, block);
        }
        return true;
    }

    public boolean canBuild(Player player, Block block) {
        if (wg != null) {
            if (block == null) {
                return true;
            }
            return wg.canBuild(player, block);
        }
        return true;
    }

    public boolean canBuild(Player player, Block block, String msg) {
        if (wg != null) {
            if (block == null) {
                return true;
            }
            if (!wg.canBuild(player, block)) {
                this.getLogger().info(msg);
            }
            return wg.canBuild(player, block);
        }
        return true;
    }

    public boolean safeSetBlock(Player player, Block block, Material type, Material material, Enchantment ench) {
        if (!canBuild(player, block, material, ench)) {
            return false;
        }
        block.setType(type);
        return true;
    }

    public boolean safeSetBlock(Player player, Block block, Material type) {
        if (!canBuild(player, block)) {
            return false;
        }
        block.setType(type);
        return true;
    }
}
