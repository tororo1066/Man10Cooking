package tororo1066.man10cooking.inventory

import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import tororo1066.man10cooking.CookingCommand
import tororo1066.man10cooking.Man10Cooking
import tororo1066.man10cooking.Man10Cooking.Companion.sendPrefixMsg
import tororo1066.man10cooking.data.IngredientCategory
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.defaultMenus.LargeSInventory
import tororo1066.tororopluginapi.otherClass.StrExcludeFileIllegalCharacter
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import tororo1066.tororopluginapi.sItem.SItem
import java.io.File
import java.util.function.Consumer

class IngredientCategoryEditMenu(private val selectable: Boolean) : LargeSInventory("IngredientCategoryEdit") {

    var onSelect: Consumer<IngredientCategory>? = null

    override fun renderMenu(p: Player): Boolean {
        val items = ArrayList<SInventoryItem>()
        Man10Cooking.ingredientCategories.values.forEach {
            val item = SInventoryItem(Material.BOOK)
                .setDisplayName(it.internalName)
                .addLore(if (selectable) "§a左クリックで選択" else "")
                .addLore("§dメインの材料にする: ${it.mainFood}(右クリックで変更)")
                .setCanClick(false)
                .setClickEvent { e ->
                    when(e.click) {
                        ClickType.LEFT -> {
                            if (!selectable)return@setClickEvent
                            onSelect?.accept(it)
                        }
                        ClickType.RIGHT -> {
                            val config = SJavaPlugin.sConfig.getConfig("categories/${it.internalName}")?:return@setClickEvent
                            config.set("mainFood",!it.mainFood)
                            SJavaPlugin.sConfig.saveConfig(config,"categories/${it.internalName}")
                            it.mainFood = !it.mainFood
                        }
                        ClickType.SHIFT_RIGHT -> {
                            File(SJavaPlugin.plugin.dataFolder.path + "/categories/${it.internalName}.yml")
                                .delete()
                            Man10Cooking.ingredientCategories.remove(it.internalName)
                            p.sendPrefixMsg(SStr("&a削除しました"))
                            Man10Cooking.reloadPluginConfig()
                            CookingCommand()
                        }

                        else -> {}
                    }

                    allRenderMenu(p)

                }
            items.add(item)
        }

        items.add(createInputItem(SItem(Material.EMERALD_BLOCK)
            .setDisplayName("§a新しく追加する"),StrExcludeFileIllegalCharacter::class.java,
            "/<内部名>", { str, _ ->
                if (Man10Cooking.ingredientCategories.containsKey(str.string)){
                    p.sendPrefixMsg(SStr("&c既に存在します"))
                    return@createInputItem
                }

                SJavaPlugin.sConfig.saveConfig(YamlConfiguration(),"categories/${str.string}")
                Man10Cooking.ingredientCategories[str.string] = IngredientCategory(str.string, true)
                p.sendPrefixMsg(SStr("&a作成しました"))

            },{
              "§cファイル名に使えない文字を使わないでください"
            }, listOf(),false))

        setResourceItems(items)
        return true
    }
}