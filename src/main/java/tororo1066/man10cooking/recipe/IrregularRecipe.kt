package tororo1066.man10cooking.recipe

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import tororo1066.man10cooking.data.ingredient.AbstractIngredient

class IrregularRecipe : AbstractRecipe() {

    var irregularName = ""

    override fun getResult(items: List<AbstractIngredient>): ItemStack? {
        if (irregularName.isBlank())return null
        val newItems = items.filter { it.irregularTypeName.isNotBlank() }.sortedBy { it.irregularPriority }
        val find = newItems.find { it.irregularTypeName == irregularName }?:return null
        if (newItems.indexOf(find) != 0)return null
        return resultItem
    }

    override fun loadFromYml(yml: YamlConfiguration): AbstractRecipe {
        irregularName = yml.getString("irregularName","")!!
        return this
    }
}