package unrestrictedenchantments.unrestrictedenchantments.Handlers;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import unrestrictedenchantments.unrestrictedenchantments.UnrestrictedEnchantments;

import java.util.Objects;

public class MeleeHandler implements Listener {
    FileConfiguration config;
    public MeleeHandler(UnrestrictedEnchantments plugin, FileConfiguration cfg) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        config = cfg;
    }


    @EventHandler(priority = EventPriority.LOW)
    public void entityDamageByEntity(EntityDamageByEntityEvent event){
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK){
            return;
        }
        if (!(event.getEntity() instanceof LivingEntity target)){
            return;
        }
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (weapon.getItemMeta() != null){
            if (weapon.getItemMeta().hasEnchants()){
                // multishot
                if (weapon.getItemMeta().getEnchantLevel(Enchantment.MULTISHOT) >= 1 && Objects.requireNonNull(config.getConfigurationSection("EnabledEnchantments")).getBoolean("MultishotMelee")){
                    target.setNoDamageTicks(0);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("UnrestrictedEnchantments")), () -> {
                        target.damage(event.getDamage() * config.getDouble("MeleeMultishotDamage"));
                        target.setNoDamageTicks(0);
                        player.playSound(target.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.5F);
                        target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().getX(),target.getLocation().getY() + 1,target.getLocation().getZ(),
                                50, 0.5,0.5, 0.5,  null);
                    }, 11L);
                }
                // frost walker
                if (weapon.getItemMeta().getEnchantLevel(Enchantment.FROST_WALKER) >= 1 && Objects.requireNonNull(config.getConfigurationSection("EnabledEnchantments")).getBoolean("FrostWalkerMelee")){
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 0, false, true));
                }
            }
        }

    }

}
