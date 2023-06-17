package tororo1066.man10cooking.recipe

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import tororo1066.man10cooking.data.IngredientCategory
import tororo1066.man10cooking.data.ingredient.AbstractIngredient
import java.io.File

abstract class AbstractRecipe {

    var internalName = ""
    var file = ""
    lateinit var resultItem: ItemStack

    abstract fun getResult(items: List<AbstractIngredient>): ItemStack?

    abstract fun loadFromYml(yml: YamlConfiguration): AbstractRecipe

    companion object{
        fun loadFromYml(file: File): AbstractRecipe? {
            return try {
                val yml = YamlConfiguration.loadConfiguration(file)
                val type = yml.getString("type")!!.replace(".","")
                val clazz = Class.forName("tororo1066.man10cooking.recipe.$type")
                val instance = (clazz.getConstructor().newInstance() as AbstractRecipe)
                instance.internalName = file.nameWithoutExtension
                instance.resultItem = yml.getItemStack("result")!!
                instance.file = "${file.parent.substringBeforeLast("/")}/${file.nameWithoutExtension}"
                instance.loadFromYml(yml)
            } catch (e: Exception) {
                null
            }
        }
    }
}