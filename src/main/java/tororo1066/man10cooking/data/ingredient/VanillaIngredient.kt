package tororo1066.man10cooking.data.ingredient

import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import tororo1066.man10cooking.data.ingredient.AbstractIngredient
import java.io.File

class VanillaIngredient : AbstractIngredient() {

    lateinit var material: Material

    override fun loadFromYml(yml: YamlConfiguration): AbstractIngredient {
        val materialItem = Material.getMaterial(yml.getString("material","STONE")!!.uppercase())!!
        material = materialItem
        infoItemStack = ItemStack(materialItem)
        return this
    }

    override fun writeYml(yml: YamlConfiguration): YamlConfiguration {
        super.writeYml(yml)

        yml.set("material",material.name)
        return yml
    }
}