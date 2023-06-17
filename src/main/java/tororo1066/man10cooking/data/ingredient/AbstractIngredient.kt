package tororo1066.man10cooking.data.ingredient

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import tororo1066.man10cooking.Man10Cooking
import tororo1066.man10cooking.data.IngredientCategory
import java.io.File

abstract class AbstractIngredient {

    var internalName = ""
    lateinit var category: IngredientCategory
    var weight = 1
    var irregularTypeName = ""
    var irregularPriority = Int.MAX_VALUE
    var file = ""

    var infoItemStack: ItemStack? = null

    abstract fun loadFromYml(yml: YamlConfiguration): AbstractIngredient

    open fun writeYml(yml: YamlConfiguration): YamlConfiguration {
        yml.set("type",javaClass.simpleName)
        yml.set("category",category.internalName)
        yml.set("weight",weight)
        yml.set("irregularTypeName",irregularTypeName)
        yml.set("irregularPriority",irregularPriority)

        return yml
    }

    companion object{
        fun loadFromYml(file: File): AbstractIngredient? {
            return try {
                val yml = YamlConfiguration.loadConfiguration(file)
                val type = yml.getString("type")!!.replace(".","")
                val clazz = Class.forName("tororo1066.man10cooking.data.ingredient.$type")
                val instance = (clazz.getConstructor().newInstance() as AbstractIngredient)
                instance.category = Man10Cooking.ingredientCategories[yml.getString("category")]!!
                instance.internalName = file.nameWithoutExtension
                instance.weight = yml.getInt("weight",1)
                instance.irregularTypeName = yml.getString("irregularTypeName","")!!
                instance.irregularPriority = yml.getInt("irregularPriority",Int.MAX_VALUE)
                instance.file = "${file.parent.substringBeforeLast("/")}/${file.nameWithoutExtension}"
                instance.loadFromYml(yml)
            } catch (e: Exception) {
                null
            }
        }
    }
}