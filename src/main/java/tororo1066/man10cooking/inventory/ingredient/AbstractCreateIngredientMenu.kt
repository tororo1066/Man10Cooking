package tororo1066.man10cooking.inventory.ingredient

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import tororo1066.man10cooking.CookingCommand
import tororo1066.man10cooking.Man10Cooking
import tororo1066.man10cooking.Man10Cooking.Companion.sendPrefixMsg
import tororo1066.man10cooking.data.IngredientCategory
import tororo1066.man10cooking.data.ingredient.AbstractIngredient
import tororo1066.man10cooking.inventory.IngredientCategoryEditMenu
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.otherClass.StrExcludeFileIllegalCharacter
import tororo1066.tororopluginapi.sInventory.SInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import tororo1066.tororopluginapi.sItem.SItem
import java.util.function.Consumer
import java.util.function.Function

abstract class AbstractCreateIngredientMenu(name: String, val isEdit: Boolean, val nowItem: ItemStack?, val nowName: String?, nowCategory: IngredientCategory?): SInventory(SJavaPlugin.plugin, name, 4) {

    override var savePlaceItems = true
    var category: IngredientCategory? = null
    var weight = 1
    abstract val onConfirm: Function<Triple<ItemStack, IngredientCategory, Player>,AbstractIngredient>
    var irregularType = ""
    var irregularPriority = Int.MAX_VALUE

    init {
        category = nowCategory
    }

    override fun renderMenu(p: Player): Boolean {
        Man10Cooking.fillBackGround(this, p)

        removeItem(13)
        if (nowItem != null){
            setItem(13,nowItem)
        }

        setItems(listOf(4,12,14,22), SInventoryItem(Material.LIME_STAINED_GLASS_PANE)
            .setDisplayName(" ")
            .setCanClick(false))
        setItem(29, SInventoryItem(Material.PURPLE_STAINED_GLASS_PANE)
            .setDisplayName("§dカテゴリを設定(作成)する")
            .addLore("§7現在の値: ${category?.internalName}")
            .setCanClick(false).setClickEvent {
                val inv = IngredientCategoryEditMenu(true)
                inv.onSelect = Consumer {
                    category = it
                    p.closeInventory()
                }
                moveChildInventory(inv, p)
            })
        setItem(30,createInputItem(
            SItem(Material.ORANGE_STAINED_GLASS_PANE)
            .setDisplayName("§6重みを設定する")
            .addLore("§7現在の値: $weight"),Int::class.java) { int, _ ->
            weight = int
            allRenderMenu(p)
        })

        setItem(32, createInputItem(SItem(Material.WHITE_STAINED_GLASS_PANE)
            .setDisplayName("§cイレギュラータイプを設定する")
            .addLore("§d現在の値: $irregularType"),String::class.java, "/<イレギュラータイプ>") { str, _ ->
            irregularType = str
        })

        setItem(33, createInputItem(SItem(Material.BLUE_STAINED_GLASS_PANE)
            .setDisplayName("§cイレギュラーの優先度を設定する")
            .addLore("§d現在の値: $irregularPriority"),Int::class.java, "/<優先度>") { int, _ ->
            irregularPriority = int
        })

        setItem(31, createInputItem(SItem(Material.RED_STAINED_GLASS_PANE)
            .setDisplayName("§c決定"),StrExcludeFileIllegalCharacter::class.java,
            "§d/<内部名>") { str, _ ->
            val item = getItem(13)?:return@createInputItem
            if (category == null){
                p.sendPrefixMsg(SStr("&cカテゴリが設定されていません"))
                return@createInputItem
            }
            if (Man10Cooking.ingredients.containsKey(str.string)){
                val data = Man10Cooking.ingredients[str.string]!!
                if (!SJavaPlugin.sConfig.exists("ingredients/${data.file}") || !isEdit){
                    p.sendPrefixMsg(SStr("§cその内部名は${data.file}に存在しています！"))
                    return@createInputItem
                }
            }

            item.amount = 1
            val result = onConfirm.apply(Triple(item,category!!,p))
            result.internalName = str.string
            result.weight = weight
            result.irregularTypeName = irregularType
            result.irregularPriority = irregularPriority

            SJavaPlugin.sConfig.saveConfig(result.writeYml(YamlConfiguration()),"ingredients/${result.javaClass.simpleName}/${result.internalName}")

            p.sendPrefixMsg(SStr("§a作成しました"))
            Man10Cooking.reloadPluginConfig()
            CookingCommand()
            Bukkit.getScheduler().runTask(SJavaPlugin.plugin, Runnable {
                p.closeInventory()
            })
        })

        if (isEdit){
            setItem(31, SInventoryItem(Material.RED_STAINED_GLASS_PANE)
                .setDisplayName("§c決定").setCanClick(false).setClickEvent {
                    val item = getItem(13)
                    if (item == null){
                        p.sendPrefixMsg(SStr("&cアイテムをセットしてください"))
                        return@setClickEvent
                    }
                    if (category == null){
                        p.sendPrefixMsg(SStr("&cカテゴリが設定されていません"))
                        return@setClickEvent
                    }

                    item.amount = 1
                    val result = onConfirm.apply(Triple(item,category!!,p))
                    result.internalName = nowName!!
                    result.weight = weight
                    result.irregularTypeName = irregularType
                    result.irregularPriority = irregularPriority

                    SJavaPlugin.sConfig.saveConfig(result.writeYml(YamlConfiguration()),"ingredients/${result.javaClass.simpleName}/${result.internalName}")

                    p.sendPrefixMsg(SStr("§a上書き保存しました"))
                    Man10Cooking.reloadPluginConfig()
                    CookingCommand()
                    Bukkit.getScheduler().runTask(SJavaPlugin.plugin, Runnable {
                        p.closeInventory()
                    })
                })
        }

        return true
    }
}