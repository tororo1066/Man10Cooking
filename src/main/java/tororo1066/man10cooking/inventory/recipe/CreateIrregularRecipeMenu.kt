package tororo1066.man10cooking.inventory.recipe

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

class CreateWeightRecipeMenu(val isEdit: Boolean) : SInventory(SJavaPlugin.plugin, "CreateUniqueRecipe", 5) {

    override var savePlaceItems = true
    val craftingSlots = listOf(12,13,14,21,22,23,30,31,32)

    var result: ItemStack? = null

    init {
        setOnClick {
            it.isCancelled = true
        }
    }
    override fun renderMenu(p: Player): Boolean {
        Man10Cooking.fillBackGround(this, p)
        removeItems(craftingSlots)

        setItem(16, SInventoryItem(Material.BOOK)
            .setDisplayName("§dカテゴリを指定する")
            .setCanClick(false).setClickEvent {
                val inv = IngredientCategoryEditMenu(true)
                inv.onSelect = Consumer {
                    Man10Cooking.sInput.sendInputCUI(p, PlusInt::class.java, "§a必要な重みを設定してください(/<自然数>)") { plusInt ->
                        val item = SInventoryItem(Material.BOOK)
                            .addLore("§aカテゴリ: ${it.internalName}")
                            .addLore("§d重み: ${plusInt.get()}")
                            .addLore("§cクリックで削除")
                            .setCustomData(SJavaPlugin.plugin, "category", PersistentDataType.STRING, it.internalName)
                            .setCustomData(SJavaPlugin.plugin, "weight", PersistentDataType.INTEGER, plusInt.get())
                            .setCanClick(false)
                            .setClickEvent {
                                removeItem(it.slot)
                            }
                        for (i in craftingSlots){
                            if (getItem(i) != null)continue
                            setItem(i, item)
                        }
                    }
                }
                moveChildInventory(inv, p)
            })

        setItem(25, SInventoryItem(Material.CRAFTING_TABLE)
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

        setItem(40, createInputItem(SItem(Material.RED_STAINED_GLASS_PANE)
            .setDisplayName("§c決定"), StrExcludeFileIllegalCharacter::class.java, "§d/<内部名>") { str, _ ->
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

            val categories = ArrayList<Pair<IngredientCategory, Int>>()

            craftingSlots.forEach {
                val item = items[it]?:return@forEach
                val categoryData = item.getCustomData(SJavaPlugin.plugin, "category", PersistentDataType.STRING)!!
                val weightData = item.getCustomData(SJavaPlugin.plugin, "weight", PersistentDataType.INTEGER)!!
                categories.add(Pair(Man10Cooking.ingredientCategories[categoryData]!!,weightData))
            }

            val yml = YamlConfiguration()
            yml.set("type","WeightRecipe")
            yml.set("weights",categories.map { "${it.first.internalName},${it.second}" })
            yml.set("result",result!!)

            SJavaPlugin.sConfig.saveConfig(yml, "recipes/WeightRecipe/${str.string}")

            p.sendPrefixMsg(SStr("§a作成しました"))
            Man10Cooking.reloadPluginConfig()
            CookingCommand()
        })

        return true
    }

}