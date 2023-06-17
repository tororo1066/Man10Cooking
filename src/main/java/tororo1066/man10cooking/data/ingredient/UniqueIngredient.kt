package tororo1066.man10cooking.data

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.persistence.PersistentDataType
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.sItem.SItem
import java.io.File

class UniqueIngredient: AbstractIngredient() {

    lateinit var icon: SItem

    override fun loadFromYml(file: File): AbstractIngredient {
        val yml = YamlConfiguration.loadConfiguration(file)
        internalName = file.nameWithoutExtension
        icon = SItem(yml.getItemStack("icon")!!)
            .setCustomData(SJavaPlugin.plugin, "ingredient",
                PersistentDataType.STRING,internalName)

        return this
    }
}