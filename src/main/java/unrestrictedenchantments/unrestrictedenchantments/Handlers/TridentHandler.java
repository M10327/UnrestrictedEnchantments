package unrestrictedenchantments.unrestrictedenchantments.Handlers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import unrestrictedenchantments.unrestrictedenchantments.UnrestrictedEnchantments;

import java.util.Objects;

public class TridentHandler implements Listener {
    FileConfiguration config;
    public TridentHandler(UnrestrictedEnchantments plugin, FileConfiguration cfg) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        config = cfg;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Trident trident)) {
            return;
        }
        if (trident.getItem().getItemMeta() == null) {
            return;
        }

        if (trident.getItem().getItemMeta().hasEnchants()){
            // flame enchant
            if (!event.isCancelled()){
                if (trident.getItem().getItemMeta().hasEnchant(Enchantment.ARROW_FIRE) && Objects.requireNonNull(config.getConfigurationSection("EnabledEnchantments")).getBoolean("FlameTrident")){
                    event.getEntity().setFireTicks(80);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void tridentThrow(ProjectileLaunchEvent event){
        if (!(event.getEntity() instanceof Trident trident)) {
            return;
        }
        if (trident.getItem().getItemMeta() == null) {
            return;
        }
        // multishot
        if (trident.getItem().getItemMeta().hasEnchant(Enchantment.MULTISHOT) && Objects.requireNonNull(config.getConfigurationSection("EnabledEnchantments")).getBoolean("MultishotTrident")){
            double spread = config.getDouble("TridentMultishotSpread");
            for (int i = 1; i <= trident.getItem().getItemMeta().getEnchantLevel(Enchantment.MULTISHOT); i++){
                for (int k = 1; k <= 2; k++){
                    Trident mtrident = (Trident) event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), event.getEntity().getType());
                    Vector vec = event.getEntity().getVelocity();
                    if (k == 1){
                        vec.setY(vec.getY() * (1 + (spread * i)));
                    }
                    else{
                        vec.setY(vec.getY() * (1 - (spread * i)));
                    }
                    mtrident.setVelocity(vec);
                    mtrident.setItem(trident.getItem());
                    // adds sweeping tag to additional tridents
                    if (trident.getItem().getItemMeta().getEnchantLevel(Enchantment.SWEEPING_EDGE) >= 1){
                        mtrident.setMetadata("URE_sweep", new FixedMetadataValue(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("UnrestrictedEnchantments")), trident.getItem().getItemMeta().getEnchantLevel(Enchantment.SWEEPING_EDGE)));
                    }
                }
            }
        }
        // sweeping
        // other half is covered in BowHandler cuz lazy
        if (trident.getItem().getItemMeta().getEnchantLevel(Enchantment.SWEEPING_EDGE) >= 1){
            trident.setMetadata("URE_sweep", new FixedMetadataValue(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("UnrestrictedEnchantments")), trident.getItem().getItemMeta().getEnchantLevel(Enchantment.SWEEPING_EDGE)));
        }
        // frost walker
        // also in arrows :shrug:
        if (trident.getItem().getItemMeta().getEnchantLevel(Enchantment.FROST_WALKER) >= 1){
            trident.setMetadata("URE_frost", new FixedMetadataValue(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("UnrestrictedEnchantments")), trident.getItem().getItemMeta().getEnchantLevel(Enchantment.FROST_WALKER)));
        }

    }

}
