package com.ryanmichela.metadatademo;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 */
public class MetadataDemo extends JavaPlugin
{
    public void onEnable() {
        getServer().getLogger().info("Loading metadata demo...");
        getServer().getOfflinePlayer("deltahat").setMetadata("fromOffline", new FixedMetadataValue(this, true));
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PLACE, new BlockPlaceListener(this), Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, new PlayerStickListener(), Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_BUCKET_FILL, new PlayerListener(this), Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, new PlayerListener(this), Event.Priority.Normal, this);
    }

    public void onDisable() {

    }

    private class BlockPlaceListener extends BlockListener {
        private Plugin plugin;

        public BlockPlaceListener(Plugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public void onBlockPlace(BlockPlaceEvent event) {
            Block b = event.getBlockPlaced();
            Player p = event.getPlayer();
            b.setMetadata("placedBy", new FixedMetadataValue(plugin, p.getName()));
        }
    }

    private class PlayerStickListener extends org.bukkit.event.player.PlayerListener {
        @Override
        public void onPlayerInteract(PlayerInteractEvent event) {
            if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getMaterial() == Material.STICK) {
                Block b = event.getClickedBlock();
                if(b.hasMetadata("placedBy")) {
                    String placedBy = b.getMetadata("placedBy").get(0).asString();
                    event.getPlayer().sendMessage("Block placed by " + placedBy);
                }
            }
        }
    }

    private class PlayerListener extends org.bukkit.event.player.PlayerListener {
        private Plugin plugin;

        public PlayerListener(Plugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public void onPlayerBucketFill(PlayerBucketFillEvent event) {
            Player p = event.getPlayer();
            if(p.hasMetadata("bucketFilled")) {
                p.sendMessage("You have filled a bucket before.");
            } else {
                p.setMetadata("bucketFilled", new FixedMetadataValue(plugin, true));
            }
        }

        @Override
        public void onPlayerJoin(PlayerJoinEvent event) {
            Player p = event.getPlayer();
            if(p.hasMetadata("fromOffline")) {
                p.sendMessage("You have metadata from when you were offline.");
            }
        }
    }
}
