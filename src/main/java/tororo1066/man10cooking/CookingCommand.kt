package tororo1066.man10cooking

import tororo1066.man10cooking.Man10Cooking.Companion.sendPrefixMsg
import tororo1066.man10cooking.inventory.CookingMenu
import tororo1066.man10cooking.inventory.CreateMainMenu
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.annotation.SCommandBody
import tororo1066.tororopluginapi.sCommand.SCommand
import tororo1066.tororopluginapi.sCommand.SCommandArg
import tororo1066.tororopluginapi.sCommand.SCommandArgType

class CookingCommand : SCommand("man10cooking",Man10Cooking.prefix.toString()) {

    @SCommandBody
    val cookingMenu = command().setPlayerExecutor {
        CookingMenu().open(it.sender)
    }

    @SCommandBody("man10cooking.op")
    val createMenu = command().addArg(SCommandArg("create"))
        .setPlayerExecutor {
            CreateMainMenu().open(it.sender)
        }

    @SCommandBody
    val darkMode = command().addArg(SCommandArg("darkMode"))
        .addArg(SCommandArg(SCommandArgType.BOOLEAN))
        .setPlayerExecutor {
            val config = SJavaPlugin.plugin.config
            val list = config.getStringList("darkModePlayers")
            if (it.args[1].toBooleanStrict()){
                Man10Cooking.darkModePlayers.add(it.sender.uniqueId)
                list.add(it.sender.uniqueId.toString())
            } else {
                Man10Cooking.darkModePlayers.remove(it.sender.uniqueId)
                list.remove(it.sender.uniqueId.toString())
            }

            config.set("darkModePlayers",list)
            SJavaPlugin.plugin.saveConfig()
            it.sender.sendPrefixMsg(SStr("&a変更しました"))
        }

    @SCommandBody("man10cooking.op")
    val giveUniqueIngredient = command().addArg(SCommandArg("giveIngredient"))
        .addArg(SCommandArg(Man10Cooking.ingredients.keys))
        .setPlayerExecutor {
            val data = Man10Cooking.ingredients[it.args[1]]!!
            it.sender.inventory.addItem(data.infoItemStack?:return@setPlayerExecutor)
            it.sender.sendPrefixMsg(SStr("&a入手しました"))
        }

    @SCommandBody("man10cooking.op")
    val reload = command().addArg(SCommandArg("reload"))
        .setNormalExecutor {
            Man10Cooking.reloadPluginConfig()
            it.sender.sendPrefixMsg(SStr("&aリロードしました"))
        }
}