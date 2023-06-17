package tororo1066.man10cooking.inventory.ingredient

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import tororo1066.man10cooking.CookingCommand
import tororo1066.man10cooking.Man10Cooking
import tororo1066.man10cooking.Man10Cooking.Companion.sendPrefixMsg
import tororo1066.man10cooking.data.IngredientCategory
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.defaultMenus.LargeSInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import java.io.File

class IngredientEditMenu: LargeSInventory("IngredientEdit") {

    override fun renderMenu(p: Player): Boolean {
        val items = ArrayList<SInventoryItem>()
        Man10Cooking.ingredients.values.forEach {
            val item = SInventoryItem(it.infoItemStack?:ItemStack(Material.BOOK))
                .addLore(it.internalName)
                .addLore("§dタイプ: ${it.javaClass.simpleName}")
                .addLore("§a重み: ${it.weight}")
                .addLore("§cイレギュラータイプ: ${it.irregularTypeName}")
                .addLore("§6イレギュラー優先度: ${it.irregularPriority}")
                .addLore("§4シフト右クリックで削除")
                .setCanClick(false)
                .setClickEvent { e ->
                    when(e.click) {
                        ClickType.LEFT -> {
                            val clazz = Class.forName("tororo1066.man10cooking.inventory.ingredient.Create${it.javaClass.simpleName}Menu")
                            val editMenu = clazz.getConstructor(Boolean::class.java, ItemStack::class.java, String::class.java, IngredientCategory::class.java)
                                .newInstance(true, it.infoItemStack, it.internalName, it.category) as AbstractCreateIngredientMenu

                            moveChildInventory(editMenu, p)
                        }
                        ClickType.SHIFT_RIGHT -> {
                            File(SJavaPlugin.plugin.dataFolder.path + "/${it.file}.yml").delete()
                            Man10Cooking.ingredients.remove(it.internalName)
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