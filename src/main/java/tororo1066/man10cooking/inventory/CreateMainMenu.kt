package tororo1066.man10cooking.inventory

import org.bukkit.Material
import org.bukkit.entity.Player
import tororo1066.man10cooking.Man10Cooking
import tororo1066.man10cooking.inventory.ingredient.CreateUniqueIngredientMenu
import tororo1066.man10cooking.inventory.ingredient.CreateVanillaIngredientMenu
import tororo1066.man10cooking.inventory.ingredient.IngredientEditMenu
import tororo1066.man10cooking.inventory.recipe.*
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.sInventory.SInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem

class CreateMainMenu : SInventory(SJavaPlugin.plugin, "Create",6) {

    override fun renderMenu(p: Player): Boolean {
        Man10Cooking.fillBackGround(this, p)

        setItems(
            listOf(1,46), SInventoryItem(Material.YELLOW_STAINED_GLASS_PANE)
            .setDisplayName("§e素材を作る")
            .setCanClick(false))

        setItem(10, SInventoryItem(Material.IRON_PICKAXE)
            .setDisplayName("§aオリジナルの素材を作る")
            .setCanClick(false)
            .setClickEvent { _ ->
                CreateUniqueIngredientMenu(false,).open(p)
            })

        setItem(19, SInventoryItem(Material.WHEAT)
            .setDisplayName("§bバニラのアイテムを素材として作る")
            .setCanClick(false)
            .setClickEvent { _ ->
                CreateVanillaIngredientMenu(false).open(p)
            })

        setItems(
            listOf(3,48), SInventoryItem(Material.LIME_STAINED_GLASS_PANE)
                .setDisplayName("§eレシピを作る")
                .setCanClick(false))

        setItem(12, SInventoryItem(Material.ENCHANTED_BOOK)
            .setDisplayName("§b特別なレシピを作る")
            .setCanClick(false)
            .setClickEvent { _ ->
                CreateUniqueRecipeMenu(false).open(p)
            })

        setItem(21, SInventoryItem(Material.ANVIL)
            .setDisplayName("§6重みレシピを作る")
            .setCanClick(false)
            .setClickEvent { _ ->
                CreateWeightRecipeMenu(false).open(p)
            })

        setItem(30, SInventoryItem(Material.BARRIER)
            .setDisplayName("§4例外のレシピを作る")
            .setCanClick(false)
            .setClickEvent { _ ->
                CreateIrregularRecipeMenu(false).open(p)
            })

        setItem(39, SInventoryItem(Material.WOODEN_PICKAXE)
            .setDisplayName("§aデフォルトのレシピを作る")
            .setCanClick(false)
            .setClickEvent { _ ->
                CreateDefaultRecipeMenu(false).open(p)
            })

        setItems(listOf(5,50), SInventoryItem(Material.PURPLE_STAINED_GLASS_PANE)
            .setDisplayName("§d一覧を見る/編集する")
            .setCanClick(false))

        setItem(14, SInventoryItem(Material.ENDER_CHEST)
            .setDisplayName("§aカテゴリを管理する")
            .setCanClick(false)
            .setClickEvent { _ ->
                IngredientCategoryEditMenu(false).open(p)
            })

        setItem(23, SInventoryItem(Material.CHEST)
            .setDisplayName("§b素材を管理する")
            .setCanClick(false)
            .setClickEvent { _ ->
                IngredientEditMenu().open(p)
            })

        setItem(32, SInventoryItem(Material.BOOK)
            .setDisplayName("§eレシピを管理する")
            .setCanClick(false)
            .setClickEvent { _ ->
                RecipeEditMenu().open(p)
            })
        return true
    }
}