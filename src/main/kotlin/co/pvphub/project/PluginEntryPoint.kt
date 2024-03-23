package co.pvphub.project

import co.pvphub.core.Core
import com.mattmx.ktgui.GuiManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class PluginEntryPoint : JavaPlugin() {
    private lateinit var core: Core

    override fun onEnable() {
        instance = this
        core = Bukkit.getPluginManager().getPlugin("PvPHub-Core") as Core?
            ?: throw Error("PvPHub-Core couldn't be found.")

        core.withMongoConnection(this)
            .withRedisConnection(this)

        // Delete if not needed
        saveDefaultConfig()

        GuiManager.init(this)

        // TODO: Your code.
    }

    companion object {
        private lateinit var instance: PluginEntryPoint
        fun get() = instance
    }

}