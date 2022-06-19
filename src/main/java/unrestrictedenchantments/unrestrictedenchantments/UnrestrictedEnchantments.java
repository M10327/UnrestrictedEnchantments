package unrestrictedenchantments.unrestrictedenchantments;

import org.bukkit.plugin.java.JavaPlugin;
import unrestrictedenchantments.unrestrictedenchantments.Handlers.*;

public final class UnrestrictedEnchantments extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        new AnvilHandler(this, getConfig());
        new BowHandler(this, getConfig());
        new TridentHandler(this, getConfig());
        new MeleeHandler(this, getConfig());
        new FishingHandler(this, getConfig());
        // Objects.requireNonNull(getConfig().getConfigurationSection("EnabledEnchantments")).getBoolean("SweepingProjectiles")

    }

    @Override
    public void onDisable() {

    }

}
