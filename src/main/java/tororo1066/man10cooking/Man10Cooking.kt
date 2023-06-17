package tororo1066.man10cooking

import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.HumanEntity
import tororo1066.man10cooking.data.IngredientCategory
import tororo1066.man10cooking.data.ingredient.AbstractIngredient
import tororo1066.man10cooking.recipe.AbstractRecipe
import tororo1066.man10cooking.recipe.UniqueRecipe
import tororo1066.man10cooking.recipe.WeightRecipe
import tororo1066.tororopluginapi.SInput
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.sInventory.SInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import tororo1066.tororopluginapi.utils.sendMessage
import java.util.UUID

class Man10Cooking: SJavaPlugin(UseOption.SConfig) {

    companion object {
        val ingredients = HashMap<String,AbstractIngredient>()
        val ingredientCategories = HashMap<String,IngredientCategory>()

        val recipes = LinkedHashMap<String,AbstractRecipe>()

        var prefix = SStr("§a[§dMan10§6Cooking§a]")
        val darkModePlayers = ArrayList<UUID>()

        lateinit var sInput: SInput

        fun reloadPluginConfig(){
            darkModePlayers.clear()
            ingredients.clear()
            ingredientCategories.clear()
            recipes.clear()

            darkModePlayers.addAll(plugin.config.getStringList("darkModePlayers").map { UUID.fromString(it) })

            sConfig.mkdirs("categories")
            val ingredientCategoryFiles = sConfig.loadAllFiles("categories")
            ingredientCategoryFiles.forEach {
                if (it.extension != "yml")return@forEach
                ingredientCategories[it.nameWithoutExtension] =
                    IngredientCategory.loadFromYml(it)
            }

            sConfig.mkdirs("ingredients")
            val ingredientFiles = sConfig.loadAllFiles("ingredients")
            ingredientFiles.forEach {
                if (it.extension != "yml")return@forEach
                ingredients[it.nameWithoutExtension] =
                    AbstractIngredient.loadFromYml(it)?:return@forEach
            }

            sConfig.mkdirs("recipes")
            val recipeFiles = sConfig.loadAllFiles("recipes")
            val recipeMap = LinkedHashMap<String,AbstractRecipe>()
            recipeFiles.forEach {
                if (it.extension != "yml")return@forEach
                recipeMap[it.nameWithoutExtension] = AbstractRecipe.loadFromYml(it)?:return@forEach
            }


            recipes.putAll(
                recipeMap.entries.sortedBy {
                    when(it.value){
                        is UniqueRecipe->1
                        is WeightRecipe->2
                        else->100
                    }
                }.map { Pair(it.key, it.value) }
            )



        }

        fun fillBackGround(inv: SInventory, p: HumanEntity): SInventory {
            if (darkModePlayers.contains(p.uniqueId)){
                inv.fillItem(SInventoryItem(Material.BLACK_STAINED_GLASS_PANE)
                    .setDisplayName(" ").setCanClick(false))
            } else {
                inv.fillItem(SInventoryItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                    .setDisplayName(" ").setCanClick(false))
            }

            return inv
        }

        fun CommandSender.sendPrefixMsg(sStr: SStr){
            this.sendMessage(prefix + sStr)
        }
    }

    override fun onStart() {
        sInput = SInput(this)
        reloadPluginConfig()
        CookingCommand()
    }
}