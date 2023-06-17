package tororo1066.man10cooking.data

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

abstract class AbstractIngredient {

    var internalName = ""
    lateinit var category: IngredientCategory

    abstract fun loadFromYml(file: File): AbstractIngredient

    companion object{
        fun loadFromYml(file: File): AbstractIngredient {
            val yml = YamlConfiguration.loadConfiguration(file)
            val type = yml.getString("type")?:"VanillaIngredient"

        }
    }
}