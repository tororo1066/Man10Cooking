package tororo1066.man10cooking.inventory

import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import tororo1066.man10cooking.Man10Cooking
import tororo1066.man10cooking.Man10Cooking.Companion.sendPrefixMsg
import tororo1066.man10cooking.data.IngredientCategory
import tororo1066.man10cooking.data.ingredient.AbstractIngredient
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.otherClass.StrExcludeFileIllegalCharacter
import tororo1066.tororopluginapi.sInventory.SInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import tororo1066.tororopluginapi.sItem.SItem
import java.util.function.Consumer
import java.util.function.Function

abstract class AbstractCreateIngredientMenu(name: String): SInventory(SJavaPlugin.plugin, name, 4) {

    override var savePlaceItems = true
    var category: IngredientCategory? = null
    var weight = 1
    abstract val onConfirm: Function<Triple<ItemStack, IngredientCategory, Player>,AbstractIngredient>

    override fun renderMenu(p: Player): Boolean {
        Man10Cooking.fillBackGround(this, p)

        removeItem(13)
        setItems(listOf(4,12,14,22), SInventoryItem(Material.LIME_STAINED_GLASS_PANE)
            .setDisplayName(" ")
            .setCanClick(false))
        setItem(30, SInventoryItem(Material.PURPLE_STAINED_GLASS_PANE)
            .setDisplayName("§dカテゴリを設定(作成)する")
            .addLore("§7現在の値: ${category?.internalName}")
            .setCanClick(false).setClickEvent {
                val inv = IngredientCategoryEditMenu(true)
                inv.onSelect = Consumer {
                    category = it
                }
                moveChildInventory(inv, p)
            })
        setItem(32,createInputItem(
            SItem(Material.ORANGE_STAINED_GLASS_PANE)
            .setDisplayName("§6重みを設定する")
            .addLore("§7現在の値: $weight"),Int::class.java) { int, _ ->
            weight = int
            allRenderMenu(p)
        })
        setItem(31, createInputItem(SItem(Material.RED_STAINED_GLASS_PANE)
            .setDisplayName("§c決定"),StrExcludeFileIllegalCharacter::class.java,
            "§a素材名を入力してください(/<素材名>)", true) { str, _ ->
            val item = getItem(13)?:return@createInputItem
            if (category == null){
                p.sendPrefixMsg(SStr("&cカテゴリが設定されていません"))
                return@createInputItem
            }

            val result = onConfirm.apply(Triple(item,category!!,p))
            result.internalName = str.string
            result.weight = weight

            SJavaPlugin.sConfig.saveConfig(result.writeYml(YamlConfiguration()),"ingredients/${result.javaClass.simpleName}/${result.internalName}")
            Man10Cooking.ingredients[result.internalName] = result
            p.sendPrefixMsg(SStr("§a作成しました"))

        })

        return true
    }
}