package tororo1066.man10cooking.inventory

import tororo1066.man10cooking.Man10Cooking
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.sInventory.SInventory

class CreateRecipe : SInventory(SJavaPlugin.plugin, "CreateRecipe",3) {

    override fun renderMenu(): Boolean {
        Man10Cooking.fillBackGround(this)
    }
}