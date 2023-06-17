package tororo1066.man10cooking.data.ingredient

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.sItem.SItem
import java.io.File

class UniqueIngredient: AbstractIngredient() {

    lateinit var icon: SItem

    override fun loadFromYml(yml: YamlConfiguration): AbstractIngredient {
        val iconItem = SItem(yml.getItemStack("icon")!!)
        icon = iconItem
        infoItemStack = iconItem

        return this
    }

    override fun writeYml(yml: YamlConfiguration): YamlConfiguration {
        super.writeYml(yml)

        yml.set("icon",icon.setCustomData
            (SJavaPlugin.plugin, "ingredient",
            PersistentDataType.STRING,internalName).asItemStack())
        return yml
    }
}