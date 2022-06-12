package unrestrictedenchantments.unrestrictedenchantments.Handlers;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import unrestrictedenchantments.unrestrictedenchantments.UnrestrictedEnchantments;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnvilHandler implements Listener {
    FileConfiguration config;
    public AnvilHandler(UnrestrictedEnchantments plugin, FileConfiguration cfg) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        config = cfg;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void anvilCost(PrepareAnvilEvent event){
        AnvilInventory inv = event.getInventory();
        final @Nullable ItemStack item0 = inv.getItem(0);
        final @Nullable ItemStack item1 = inv.getItem(1);
        final @Nullable ItemStack item2 = inv.getItem(2);
        if (item0 != null && item1 != null && item2 != null){
            if (item1.getType() != Material.ENCHANTED_BOOK){
                return;
            }
            if (inv.getItem(2) != null){
                if (item0.getType() == Material.ENCHANTED_BOOK){
                    return;
                }
                inv.setRepairCost(item2.getEnchantments().size());
            }
        }


    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAnvil(PrepareAnvilEvent event) {

        if (!(event.getView().getPlayer() instanceof Player player)) {
            return;
        }

        AnvilInventory inv = event.getInventory();
        ItemStack base = inv.getItem(0);
        ItemStack repair = inv.getItem(1);

        if (base == null || repair == null) {
            return;
        }
        if (repair.getItemMeta() == null) {
            return;
        }
        if (base.getAmount() != 1){
            return;
        }
        if (repair.getType().equals(Material.ENCHANTED_BOOK) && !base.getType().equals(Material.ENCHANTED_BOOK)) {
            ItemStack result = base.clone();
            EnchantmentStorageMeta enchants = (EnchantmentStorageMeta)repair.getItemMeta();
            // gets a List<String> of the enchants on the base item (item in slot 1)
            List<String> baseEnchs = new ArrayList<>();
            for(Enchantment baseEnch : base.getEnchantments().keySet()){
                String tempStrBase = baseEnch.getKey().toString().replace("minecraft:","");
                baseEnchs.add(tempStrBase);
            }
            // this bullshit is to load the stuff from the config to make it work with the code I already had before I learned I cant store maps in the config
            ConfigurationSection section = config.getConfigurationSection("ConflictingEnchants");
            Map<String, List<String>> readmap = new HashMap<>();
            assert section != null;
            for (String key : section.getKeys(false)){
                List<String> things = section.getStringList(key);
                readmap.put(key, things);
            }

            // the main logic of adding enchants to the resulting item
            // starts by looping through the stored enchantments in repair item
            for (Enchantment ench : enchants.getStoredEnchants().keySet()) {
                // removes the "minecraft:" from the enchantment name when converting it to a string
                String workingEnch = ench.getKey().toString().replace("minecraft:", "");
                boolean toAdd = true;
                // loops through the list of enchants in the base item
                for (String str : baseEnchs){
                    if (readmap.containsKey(str)){
                        if (readmap.get(str).contains(workingEnch)){
                            toAdd = false;
                        }
                    }
                }
                // adds enchantment
                if (toAdd){
                    // if the enchants are the same level
                    if (base.getEnchantmentLevel(ench) == enchants.getStoredEnchantLevel(ench)){
                        if (base.getEnchantmentLevel(ench) < ench.getMaxLevel()){
                            result.addUnsafeEnchantment(ench, enchants.getStoredEnchantLevel(ench) + 1);
                        }
                    }
                    // if the enchant on the base item is less than the one on the repair
                    else if (base.getEnchantmentLevel(ench) < enchants.getStoredEnchantLevel(ench)){
                        result.addUnsafeEnchantment(ench, enchants.getStoredEnchantLevel(ench));
                    }
                }

            }
            event.setResult(result);
        }
        player.updateInventory();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if (e.getRawSlot() != 2 || !(e.getWhoClicked() instanceof Player player))
            return;
        if (e.getView().getType() != InventoryType.ANVIL)
            return;

        AnvilInventory anvilInventory;
        try
        {
            anvilInventory = (AnvilInventory) e.getInventory();
        }
        catch (ClassCastException exception)
        {
            Bukkit.getLogger().info("Some error with unrestricted enchantments:");
            exception.printStackTrace();
            return;
        }

        final @Nullable ItemStack item0 = anvilInventory.getItem(0);
        final @Nullable ItemStack item1 = anvilInventory.getItem(1);
        final @Nullable ItemStack item2 = anvilInventory.getItem(2);

        if (item0 != null && item1 != null && item2 != null && item1.getType() == Material.ENCHANTED_BOOK && item0.getType() != Material.ENCHANTED_BOOK){
            if (item2.getEnchantments().isEmpty()){
                return;
            }

            if (player.getLevel() >= item2.getEnchantments().size()){
                if (!giveItemToPlayer(player, item2, e.isShiftClick()))
                    return;

                anvilInventory.clear();
                player.giveExpLevels(-item2.getEnchantments().size());
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 0.75F, 1);
            }
            else{
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Not enough levels! You need " + item2.getEnchantments().size()));
            }
        }
    }

    @CheckReturnValue
    protected boolean giveItemToPlayer(final Player player, final ItemStack item, final boolean direct)
    {
        if (direct)
        {
            // If the player's inventory is full, don't do anything.
            if (player.getInventory().firstEmpty() == -1)
                return false;
            player.getInventory().addItem(item);
        }
        else
            player.setItemOnCursor(item);
        return true;
    }
}
