package tororo1066.man10cooking.inventory

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import tororo1066.man10cooking.data.IngredientCategory
import tororo1066.man10cooking.data.ingredient.AbstractIngredient
import tororo1066.man10cooking.data.ingredient.UniqueIngredient
import java.util.function.Function

class CreateUniqueIngredientMenu : AbstractCreateIngredientMenu("CreateUniqueIngredient") {

    override val onConfirm: Function<Triple<ItemStack, IngredientCategory, Player>,
            AbstractIngredient> = Function {
        val data = UniqueIngredient()
        data.category = it.second
        data.icon = it.first

        return@Function data
    }
}