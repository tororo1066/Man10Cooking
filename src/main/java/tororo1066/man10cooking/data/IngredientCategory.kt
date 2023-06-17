package tororo1066.man10cooking.data

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class IngredientCategory() {

    constructor(internalName: String, mainFood: Boolean): this() {
        this.internalName = internalName
        this.mainFood = mainFood
    }

    var internalName = ""
    var mainFood = true

    companion object {
        fun loadFromYml(file: File): IngredientCategory {
            val yml = YamlConfiguration.loadConfiguration(file)
            return IngredientCategory(
                file.nameWithoutExtension,
                yml.getBoolean("mainFood", true)
            )
        }
    }
}