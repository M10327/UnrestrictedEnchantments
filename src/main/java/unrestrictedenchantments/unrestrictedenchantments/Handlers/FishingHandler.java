package unrestrictedenchantments.unrestrictedenchantments.Handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import unrestrictedenchantments.unrestrictedenchantments.UnrestrictedEnchantments;

import java.util.Objects;
import java.util.Random;

public class FishingHandler implements Listener {
    FileConfiguration config;
    public FishingHandler(UnrestrictedEnchantments plugin, FileConfiguration cfg) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        config = cfg;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onFish(PlayerFishEvent event){
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH){
            return;
        }
        ItemStack rod;
        if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.FISHING_ROD){
            rod = event.getPlayer().getInventory().getItemInMainHand();
        }
        else if (event.getPlayer().getInventory().getItemInOffHand().getType() == Material.FISHING_ROD){
            rod = event.getPlayer().getInventory().getItemInOffHand();
        }
        else{
            return;
        }
        if (rod.getItemMeta() == null){
            return;
        }
        if (!(event.getCaught() instanceof Item fish)){
            return;
        }
        ItemStack item = fish.getItemStack();
        if (rod.getItemMeta().hasEnchants()){
            Random rand = new Random();
            // fortune
            if (rod.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS) && Objects.requireNonNull(config.getConfigurationSection("EnabledEnchantments")).getBoolean("FortuneFishing")){
                int level = rod.getItemMeta().getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS);
                for (int i = 1; i <= level; i++){
                    if (item.getType().getMaxStackSize() > item.getAmount()){
                        if (rand.nextBoolean()){
                            item.setAmount(item.getAmount() + 1);
                        }
                    }
                }
            }
        }
    }

}

