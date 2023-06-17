package tororo1066.man10cooking.recipe

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import tororo1066.man10cooking.data.ingredient.AbstractIngredient

class DefaultRecipe: AbstractRecipe() {

    override fun getResult(items: List<AbstractIngredient>): ItemStack? {
        return resultItem
    }

    override fun loadFromYml(yml: YamlConfiguration): AbstractRecipe {
        return this
    }
}