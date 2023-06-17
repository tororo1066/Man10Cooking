package tororo1066.man10cooking.inventory.recipe

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import tororo1066.man10cooking.CookingCommand
import tororo1066.man10cooking.Man10Cooking
import tororo1066.man10cooking.Man10Cooking.Companion.sendPrefixMsg
import tororo1066.man10cooking.inventory.ingredient.AbstractCreateIngredientMenu
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.defaultMenus.LargeSInventory
import tororo1066.tororopluginapi.sInventory.SInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import java.io.File

class RecipeEditMenu: LargeSInventory("RecipeEdit") {

    override fun renderMenu(p: Player): Boolean {
        val items = ArrayList<SInventoryItem>()
        Man10Cooking.recipes.values.forEach {
            val item = SInventoryItem(it.resultItem)
                .addLore(it.internalName)
                .addLore("§dタイプ: ${it.javaClass.simpleName}")
                .addLore("§4シフト右クリックで削除")
                .setCanClick(false)
                .setClickEvent { e ->
                    when(e.click) {
                        ClickType.LEFT -> {
                            val clazz = Class.forName("tororo1066.man10cooking.inventory.recipe.Create${it.javaClass.simpleName}Menu")
                            val editMenu = clazz.getConstructor(Boolean::class.java, it.javaClass)
                                .newInstance(true, it) as SInventory

                            moveChildInventory(editMenu, p)
                        }
                        ClickType.SHIFT_RIGHT -> {
                            File(SJavaPlugin.plugin.dataFolder.path + "/${it.file}.yml").delete()
                            Man10Cooking.recipes.remove(it.internalName)
                            p.sendPrefixMsg(SStr("&a削除しました"))
                            Man10Cooking.reloadPluginConfig()
                            CookingCommand()
                            allRenderMenu(p)
                        }

                        else -> {}
                    }

                }
            items.add(item)
        }

        setResourceItems(items)
        return true
    }
}