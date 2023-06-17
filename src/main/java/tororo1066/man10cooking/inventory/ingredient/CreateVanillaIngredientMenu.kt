package tororo1066.man10cooking.inventory.ingredient

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import tororo1066.man10cooking.data.IngredientCategory
import tororo1066.man10cooking.data.ingredient.AbstractIngredient
import tororo1066.man10cooking.data.ingredient.VanillaIngredient
import tororo1066.man10cooking.inventory.ingredient.AbstractCreateIngredientMenu
import java.util.function.Function

class CreateVanillaIngredientMenu(isEdit: Boolean, nowData: AbstractIngredient? = null
) : AbstractCreateIngredientMenu("CreateVanillaIngredient", isEdit, nowData) {

    override val onConfirm: Function<Triple<ItemStack, IngredientCategory, Player>,
            AbstractIngredient> = Function {
        val data = VanillaIngredient()
        data.category = it.second
        data.material = it.first.type
        data.infoItemStack = ItemStack(it.first.type)
        return@Function data
    }
}