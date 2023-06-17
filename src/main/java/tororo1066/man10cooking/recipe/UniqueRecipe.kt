package tororo1066.man10cooking.recipe

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import tororo1066.man10cooking.Man10Cooking
import tororo1066.man10cooking.data.IngredientCategory
import tororo1066.man10cooking.data.ingredient.AbstractIngredient

class UniqueRecipe : AbstractRecipe() {

    val ingredients = ArrayList<AbstractIngredient>()
    val ingredientCategories = ArrayList<Pair<IngredientCategory,Int>>()
    var allowOverIngredientWeight = false


    override fun getResult(items: List<AbstractIngredient>): ItemStack? {
        val cloneIngredients = ingredients.toMutableList()
        val cloneIngredientCategories = ingredientCategories.toMutableList()
        items.forEach {
            if (cloneIngredients.remove(it)){
                return@forEach
            }

            for (category in cloneIngredientCategories){
                if (category.first != it.category)continue
                if (allowOverIngredientWeight){
                    if (category.second <= it.weight){
                        cloneIngredientCategories.remove(category)
                        return@forEach
                    }
                } else {
                    if (category.second == it.weight){
                        cloneIngredientCategories.remove(category)
                        return@forEach
                    }
                }
            }
            return null
        }

        return if (cloneIngredients.isEmpty() && cloneIngredientCategories.isEmpty()){
            resultItem
        } else null
    }

    override fun loadFromYml(yml: YamlConfiguration): AbstractRecipe {
        yml.getStringList("ingredients").forEach {
            ingredients.add(Man10Cooking.ingredients[it]?:return@forEach)
        }
        yml.getStringList("categories").forEach {
            val split = it.split(",")
            ingredientCategories.add(Pair(Man10Cooking.ingredientCategories[split[0]]?:return@forEach,
                split[1].toInt()))
        }
        allowOverIngredientWeight = yml.getBoolean("allowOverIngredientWeight",false)
        return this
    }
}