package tororo1066.man10cooking.inventory

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.persistence.PersistentDataType
import tororo1066.man10cooking.Man10Cooking
import tororo1066.man10cooking.data.ingredient.AbstractIngredient
import tororo1066.man10cooking.data.ingredient.VanillaIngredient
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.sInventory.SInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import tororo1066.tororopluginapi.sItem.SItem

class CookingMenu : SInventory(SJavaPlugin.plugin, "§c料理！", 4) {

    val craftingSlots = listOf(3,4,5,12,13,14,21,22,23)

    init {

        setOnClick {
            it.isCancelled = true
            val item = it.currentItem?:return@setOnClick
            val clicked = it.clickedInventory?:return@setOnClick
            if (clicked !is PlayerInventory && craftingSlots.contains(it.slot)){
                val clone = item.clone()
                item.amount = 0
                if (it.whoClicked.inventory.firstEmpty() == -1){
                    it.whoClicked.world.dropItem(it.whoClicked.location, clone) { dropItem ->
                        dropItem.owner = it.whoClicked.uniqueId
                        dropItem.setCanMobPickup(false)
                    }
                } else {
                    it.whoClicked.inventory.addItem(clone)
                }
                return@setOnClick
            }
            getIngredient(item)?:return@setOnClick
            val clone = item.clone().apply { amount = 1 }

            var placed = false
            for (i in craftingSlots){
                if (getItem(i) == null){
                    inv.setItem(i, clone)
                    placed = true
                    break
                }
            }

            if (!placed)return@setOnClick

            item.amount -= 1
        }

        setOnClose {
            craftingSlots.forEach { slot ->
                val item = getItem(slot)?:return@forEach
                if (it.player.inventory.firstEmpty() == -1){
                    it.player.world.dropItem(it.player.location, item) { dropItem ->
                        dropItem.owner = it.player.uniqueId
                        dropItem.setCanMobPickup(false)
                    }
                } else {
                    it.player.inventory.addItem(item)
                }

            }
        }
    }

    override fun renderMenu(p: Player): Boolean {
        Man10Cooking.fillBackGround(this, p)
        removeItems(craftingSlots)

        setItems(30..32, SInventoryItem(Material.LIME_STAINED_GLASS_PANE)
            .setDisplayName("§a料理を始める！")
            .setCanClick(false)
            .setClickEvent {
                val ingredientItems = craftingSlots.mapNotNull { getItem(it) }.filter { !it.type.isAir }
                    .mapNotNull { getIngredient(it) }
                if (ingredientItems.isEmpty())return@setClickEvent
                for (recipe in Man10Cooking.recipes.values){
                    val result = recipe.getResult(ingredientItems)?:continue
                    removeItems(craftingSlots)
                    setItem(13,result)
                    break
                }
            })

        return true
    }

    private fun getIngredient(itemStack: ItemStack): AbstractIngredient? {
        val clone = SItem(itemStack.clone())
        val data = clone.getCustomData(SJavaPlugin.plugin,"ingredient", PersistentDataType.STRING)
        if (data != null){
            val ingredient = Man10Cooking.ingredients[data]
            if (ingredient != null){
                return ingredient
            }
        }

        val fromMaterial = Man10Cooking.ingredients.entries
            .find { it.value is VanillaIngredient
                    && (it.value as VanillaIngredient).material == itemStack.type }
        if (fromMaterial != null){
            return fromMaterial.value
        }

        return null

    }
}