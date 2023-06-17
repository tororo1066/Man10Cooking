package tororo1066.man10cooking.inventory.recipe

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import tororo1066.man10cooking.CookingCommand
import tororo1066.man10cooking.Man10Cooking
import tororo1066.man10cooking.Man10Cooking.Companion.sendPrefixMsg
import tororo1066.man10cooking.data.IngredientCategory
import tororo1066.man10cooking.data.ingredient.AbstractIngredient
import tororo1066.man10cooking.inventory.IngredientCategoryEditMenu
import tororo1066.man10cooking.recipe.AbstractRecipe
import tororo1066.man10cooking.recipe.UniqueRecipe
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.defaultMenus.LargeSInventory
import tororo1066.tororopluginapi.defaultMenus.SingleItemInventory
import tororo1066.tororopluginapi.otherClass.PlusInt
import tororo1066.tororopluginapi.otherClass.StrExcludeFileIllegalCharacter
import tororo1066.tororopluginapi.sInventory.SInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import tororo1066.tororopluginapi.sItem.SItem
import java.util.function.BiConsumer
import java.util.function.Consumer

class CreateIrregularRecipeMenu(val isEdit: Boolean) : SInventory(SJavaPlugin.plugin, "CreateUniqueRecipe", 1) {

    override var savePlaceItems = true
    var irregularName = ""

    var result: ItemStack? = null

    init {
        setOnClick {
            it.isCancelled = true
        }
    }
    override fun renderMenu(p: Player): Boolean {
        Man10Cooking.fillBackGround(this, p)

        setItem(1, createInputItem(SItem(Material.BOOK)
            .setDisplayName("§aイレギュラータイプを設定する")
            .addLore("§d現在の値: $irregularName"), String::class.java, "§d/<文字>") { str, _ ->
            irregularName = str
        })

        setItem(4, SInventoryItem(Material.CRAFTING_TABLE)
            .setDisplayName("§6完成品を指定する")
            .addLore("§d現在の値: §r${if (result != null) {
                if (result!!.itemMeta.displayName == "") result!!.type.name else result!!.itemMeta.displayName
            } else "null"}")
            .setCanClick(false).setClickEvent {
                val inv = SingleItemInventory(SJavaPlugin.plugin,"完成品を指定する")
                inv.onConfirm = Consumer {
                    result = it
                    p.closeInventory()
                }
                result?.let { inv.nowItem = it }

                moveChildInventory(inv, p)
            })

        setItem(7, createInputItem(SItem(Material.RED_STAINED_GLASS_PANE)
            .setDisplayName("§c決定"), StrExcludeFileIllegalCharacter::class.java, "§d/<内部名>") { str, _ ->
            if (irregularName.isBlank()){
                p.sendPrefixMsg(SStr("&cイレギュラータイプを指定してください"))
                return@createInputItem
            }

            if (Man10Cooking.recipes.containsKey(str.string)){
                val data = Man10Cooking.recipes[str.string]!!
                if (!SJavaPlugin.sConfig.exists("recipes/${data.file}") || !isEdit){
                    p.sendPrefixMsg(SStr("§cその内部名は${data.file}に存在しています！"))
                    return@createInputItem
                }
            }

            if (result == null){
                p.sendPrefixMsg(SStr("&c完成品を指定してください"))
                return@createInputItem
            }

            val yml = YamlConfiguration()
            yml.set("type","IrregularRecipe")
            yml.set("irregularName",irregularName)
            yml.set("result",result!!)

            SJavaPlugin.sConfig.saveConfig(yml, "recipes/IrregularRecipe/${str.string}")

            p.sendPrefixMsg(SStr("§a作成しました"))
            Man10Cooking.reloadPluginConfig()
            CookingCommand()

            Bukkit.getScheduler().runTask(SJavaPlugin.plugin, Runnable {
                p.closeInventory()
            })
        })

        return true
    }

}