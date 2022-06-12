package unrestrictedenchantments.unrestrictedenchantments.Handlers;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import unrestrictedenchantments.unrestrictedenchantments.UnrestrictedEnchantments;
import java.util.Objects;

public class BowHandler implements Listener {
    FileConfiguration config;
    public BowHandler(UnrestrictedEnchantments plugin, FileConfiguration cfg) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        config = cfg;
    }
    Plugin plugin = Bukkit.getPluginManager().getPlugin("UnrestrictedEnchantments");
    @EventHandler(priority = EventPriority.LOW)
    public void entityArrow(org.bukkit.event.entity.EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        if (!(event.getProjectile() instanceof Arrow arrow)){
            return;
        }
        if (event.getBow() == null) {
            return;
        }
        if (event.getBow().getItemMeta() == null) {
            return;
        }
        ItemStack bow = event.getBow();
        // bow stuff
        if (bow.getType().equals(Material.BOW)){

            // check if item has enchantments
            if (bow.getItemMeta().hasEnchants()){
                // piercing enchantment
                if (bow.getItemMeta().getEnchantLevel(Enchantment.PIERCING) >= 1 && Objects.requireNonNull(config.getConfigurationSection("EnabledEnchantments")).getBoolean("PiercingBow")){
                    NBTEditor.set(arrow, (byte) (1 + bow.getItemMeta().getEnchantLevel(Enchantment.PIERCING)), "PierceLevel");
                }
                // multishot
                if (bow.getItemMeta().getEnchantLevel(Enchantment.MULTISHOT) >= 1 && Objects.requireNonNull(config.getConfigurationSection("EnabledEnchantments")).getBoolean("MultishotBow")){
                    double spread = config.getDouble("BowMultishotSpread");
                    for (int i = 1; i <= bow.getItemMeta().getEnchantLevel(Enchantment.MULTISHOT); i++){
                        for (int k = 1; k <= 2; k++){
                            Arrow marrow = (Arrow) event.getEntity().getWorld().spawnEntity(arrow.getLocation(), arrow.getType());
                            Vector vec = arrow.getVelocity();
                            if (k == 1){
                                vec.setY(vec.getY() * (1 + (spread * i)));
                            }
                            else{
                                vec.setY(vec.getY() * (1 - (spread * i)));
                            }
                            marrow.setVelocity(vec);
                            // remaking enchants
                            if (bow.getItemMeta().getEnchantLevel(Enchantment.ARROW_DAMAGE) >= 1){
                                marrow.setMetadata("URE_bow_power", new FixedMetadataValue(plugin, bow.getItemMeta().getEnchantLevel(Enchantment.ARROW_DAMAGE)));
                            }
                            if (bow.getItemMeta().getEnchantLevel(Enchantment.ARROW_FIRE) >= 1){
                                marrow.setFireTicks(9999);
                            }
                            if (bow.getItemMeta().getEnchantLevel(Enchantment.CHANNELING) >= 1){
                                marrow.setMetadata("URE_channeling", new FixedMetadataValue(plugin, true));
                            }
                            if (bow.getItemMeta().getEnchantLevel(Enchantment.MULTISHOT) >= 1){
                                marrow.setMetadata("URE_multishot", new FixedMetadataValue(plugin, true));
                            }
                            if (bow.getItemMeta().getEnchantLevel(Enchantment.SWEEPING_EDGE) >= 1){
                                marrow.setMetadata("URE_sweep", new FixedMetadataValue(plugin, bow.getItemMeta().getEnchantLevel(Enchantment.SWEEPING_EDGE)));
                            }
                            if (bow.getItemMeta().getEnchantLevel(Enchantment.FROST_WALKER) >= 1){
                                marrow.setMetadata("URE_frost", new FixedMetadataValue(plugin, bow.getItemMeta().getEnchantLevel(Enchantment.FROST_WALKER)));
                            }
                        }
                    }
                }
            }
        }

        // crossbow stuff
        if (bow.getType().equals(Material.CROSSBOW)){
            // check if item has enchantments
            if (bow.getItemMeta().hasEnchants()){
                // power enchantment
                if (bow.getItemMeta().getEnchantLevel(Enchantment.ARROW_DAMAGE) >= 1){
                    arrow.setMetadata("URE_crossbow_power", new FixedMetadataValue(plugin, bow.getItemMeta().getEnchantLevel(Enchantment.ARROW_DAMAGE)));
                }
                // flame enchantment
                if (bow.getItemMeta().getEnchantLevel(Enchantment.ARROW_FIRE) >= 1 && Objects.requireNonNull(config.getConfigurationSection("EnabledEnchantments")).getBoolean("FlameCrossbow")){
                    arrow.setFireTicks(9999);
                }

            }

        }

        // enchants for both
        if (bow.getItemMeta().hasEnchants()){
            if (bow.getItemMeta().getEnchantLevel(Enchantment.CHANNELING) >= 1){
                arrow.setMetadata("URE_channeling", new FixedMetadataValue(plugin, true));
            }
            if (bow.getItemMeta().getEnchantLevel(Enchantment.MULTISHOT) >= 1){ // for the no iframes thing
                arrow.setMetadata("URE_multishot", new FixedMetadataValue(plugin, true));
            }
            // sweeping edge enchantment
            if (bow.getItemMeta().getEnchantLevel(Enchantment.SWEEPING_EDGE) >= 1){
                arrow.setMetadata("URE_sweep", new FixedMetadataValue(plugin, bow.getItemMeta().getEnchantLevel(Enchantment.SWEEPING_EDGE)));
            }
            // frost walker
            if (bow.getItemMeta().getEnchantLevel(Enchantment.FROST_WALKER) >= 1){
                arrow.setMetadata("URE_frost", new FixedMetadataValue(plugin, bow.getItemMeta().getEnchantLevel(Enchantment.FROST_WALKER)));
            }
        }

    }

    @EventHandler(priority = EventPriority.LOW)
    public void entityArrowHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow arrow){
            if (arrow.getMetadata("URE_crossbow_power").size() >= 1 && Objects.requireNonNull(config.getConfigurationSection("EnabledEnchantments")).getBoolean("PowerCrossbow")){
                event.setDamage(event.getDamage() * (1.25 + (config.getDouble("CrossbowPowerBase") * (double) arrow.getMetadata("URE_crossbow_power").get(0).asInt())));
            }
            if (arrow.getMetadata("URE_bow_power").size() >= 1){
                event.setDamage(event.getDamage() * (1.25 + (0.25 * (double) arrow.getMetadata("URE_bow_power").get(0).asInt())));
            }
            if (arrow.getMetadata("URE_channeling").size() >= 1 && Objects.requireNonNull(config.getConfigurationSection("EnabledEnchantments")).getBoolean("Channeling")){
                if (!event.isCancelled()){
                    if (arrow.getWorld().isThundering()){
                        arrow.getWorld().spawnEntity(arrow.getLocation(), EntityType.LIGHTNING);
                    }
                }
            }
            if (event.getEntity() instanceof LivingEntity living && (arrow.getMetadata("URE_multishot").size() >= 1) && Objects.requireNonNull(config.getConfigurationSection("EnabledEnchantments")).getBoolean("MultishotNoIframe")){
                living.setNoDamageTicks(0);
            }
        }


    }

    @EventHandler(priority = EventPriority.LOW)
    public void onProjectileHit(ProjectileHitEvent event){
        // sweeping edge and frost walker
        // also covers tridents
        if (event.getEntity().getMetadata("URE_sweep").size() >= 1 && Objects.requireNonNull(config.getConfigurationSection("EnabledEnchantments")).getBoolean("SweepingProjectiles")){
            for (Entity target : event.getEntity().getNearbyEntities(config.getDouble("SweepingRange"), config.getDouble("SweepingRange"), config.getDouble("SweepingRange"))){
                if (target instanceof LivingEntity livingTarget){
                    if (target != event.getHitEntity()){
                        livingTarget.damage(config.getDouble("SweepingDamage") * (double) event.getEntity().getMetadata("URE_sweep").get(0).asInt());
                        target.getWorld().spawnParticle(Particle.SWEEP_ATTACK, target.getLocation().getX(),target.getLocation().getY() + 0.5,target.getLocation().getZ(),
                                3, 0.2,0.2, 0.2,  null);

                        //target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 0.7F);
                    }
                }
            }
        }
        // Frost Walker
        if (!event.isCancelled()){
            if (event.getEntity().getMetadata("URE_frost").size() >= 1 && Objects.requireNonNull(config.getConfigurationSection("EnabledEnchantments")).getBoolean("FrostWalkerProjectile")){
                if (event.getHitEntity() instanceof LivingEntity target){
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 0, false, true));
                }
            }
        }
    }
}
