package tororo1066.man10cooking.recipe

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import tororo1066.man10cooking.Man10Cooking
import tororo1066.man10cooking.data.IngredientCategory
import tororo1066.man10cooking.data.ingredient.AbstractIngredient
import kotlin.math.min

class WeightRecipe: AbstractRecipe() {

    val weights = HashMap<String,Pair<IngredientCategory,Int>>()

    override fun getResult(items: List<AbstractIngredient>): ItemStack? {
        var groupIngredients = items.groupBy { it.category.internalName }
            .entries.sortedByDescending { it.value.sumOf { it.weight } }
        if (groupIngredients.size < 2){
            return null
        }
        var preventWeight = -1
        for ((index,map) in groupIngredients.withIndex()) {
            if (index >= 3)break
            val sum = map.value.sumOf { it.weight }
            if (preventWeight == sum){
                if (index <= 1){
                    preventWeight = sum
                    continue
                }
                return null
            }
            preventWeight = sum
        }
        if (groupIngredients.size > 2){
            groupIngredients = groupIngredients.dropLast(groupIngredients.size - 2)
        }

        val first = groupIngredients[0]
        val second = groupIngredients[1]

        val firstSum = first.value.sumOf { it.weight }
        val secondSum = second.value.sumOf { it.weight }

        var firstNormalized = firstSum
        var secondNormalized = secondSum
        var amount = 1

        for (i in 1..min(firstNormalized,secondNormalized)){
            if (firstSum % i == 0 && secondSum % i == 0){
                firstNormalized = firstSum / i
                secondNormalized = secondSum / i
                amount = i
            }
        }

        val weightsClone = weights.toMutableMap()

        weights.forEach { category ->
            if (first.key == category.key && firstNormalized == category.value.second){
                weightsClone.remove(category.key)
                return@forEach
            }
            if (second.key == category.key && secondNormalized == category.value.second){
                weightsClone.remove(category.key)
                return@forEach
            }
        }

        return if (weightsClone.isEmpty()) {
            resultItem.clone().apply {
                this.amount *= amount
            }
        } else null

    }

    override fun loadFromYml(yml: YamlConfiguration): AbstractRecipe {
        yml.getStringList("weights").forEach {
            val split = it.split(",")
            val category = Man10Cooking.ingredientCategories[split[0]]!!
            weights[category.internalName] = Pair(category,split[1].toInt())
        }

        return this
    }
}