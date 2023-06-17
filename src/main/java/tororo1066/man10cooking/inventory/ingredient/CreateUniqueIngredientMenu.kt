package tororo1066.man10cooking.inventory.ingredient

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import tororo1066.man10cooking.data.IngredientCategory
import tororo1066.man10cooking.data.ingredient.AbstractIngredient
import tororo1066.man10cooking.data.ingredient.UniqueIngredient
import tororo1066.man10cooking.inventory.ingredient.AbstractCreateIngredientMenu
import tororo1066.tororopluginapi.sItem.SItem
import java.util.function.Function

class CreateUniqueIngredientMenu(isEdit: Boolean, nowData: AbstractIngredient? = null
) : AbstractCreateIngredientMenu("CreateUniqueIngredient", isEdit, nowData) {

    override val onConfirm: Function<Triple<ItemStack, IngredientCategory, Player>,
            AbstractIngredient> = Function {
        val data = UniqueIngredient()
        data.category = it.second
        data.icon = SItem(it.first)
        data.infoItemStack = it.first

        return@Function data
    }
}