package trollprotectplugin.trollprotectplugin

import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockCanBuildEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.plugin.java.JavaPlugin

class TPP : JavaPlugin(),Listener {

    private val prefix = "[§0§lT§c§lPP§f] "
    private var mode = true

    override fun onEnable() {
        server.pluginManager.registerEvents(this,this)
        getCommand("tpp")?.setExecutor(this)
        saveDefaultConfig()
        server.logger.info("TrollProtectPlugin is Enabled!")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player)return true
        if (!sender.hasPermission("admin")){
            sender.sendMessage("$prefix§cあなたはこのコマンドを実行する権限がありません！")
            return true
        }
        if (args.isEmpty()){
            sender.sendMessage("§c===================TrollProtectPlugin===================")
            sender.sendMessage("§c/tpp (on OR off) このプラグインを有効/無効にします")
            sender.sendMessage("§c/tpp set (Material) (Y) おけるブロックの高さの制限を追加できます")
            sender.sendMessage("§c/tpp reload configをreloadします")
            sender.sendMessage("§c===================TrollProtectPlugin===================")
            return true

        }
        when(args[0]){

            "on"->{
                if (args.size != 1){
                    sender.sendMessage(prefix + "args.sizeが非正規です！")
                    return true
                }
                if (mode){
                    sender.sendMessage(prefix + "TPPは稼働中です！")
                    return true
                }
                mode = true
                sender.sendMessage(prefix + "TPPがonに設定されました")
                return true
            }

            "off"->{
                if (args.size != 1){
                    sender.sendMessage(prefix + "args.sizeが非正規です！")
                    return true
                }
                if (!mode){
                    sender.sendMessage(prefix + "TPPは未稼働中です！")
                    return true
                }
                mode = false
                sender.sendMessage(prefix + "TPPがoffに設定されました")
                return true
            }

            "set"->{
                if (args.size != 3){
                    sender.sendMessage(prefix + "args.sizeが非正規です！")
                    return true
                }
                val n : Int
                try {
                    n = args[2].toInt()
                }catch (e : NumberFormatException){
                    sender.sendMessage(prefix + "args[2]には数字を指定してください！")
                    return true
                }
                val l = config.getStringList("blocklist")
                l.add("${args[1]}:$n")
                config.set("blocklist",l)
                sender.sendMessage(prefix + "設定が完了しました")
                sender.sendMessage("$prefix/tpp reloadで設定を更新してください")
            }

            "reset"->{
                val l = config.getStringList("blocklist")
                l.clear()
                config.set("blocklist",l)
            }

            "reload"->{
                saveConfig()
                sender.sendMessage(prefix + "configのreloadが完了しました")
                return true
            }
        }

        return true
    }

    @EventHandler
    fun put(e : BlockCanBuildEvent){
        if (!mode)return
        if (!e.isBuildable)return
        val material = e.material.name
        for (c in 0..config.getStringList("blocklist").size.minus(1)){
            val r = config.getStringList("blocklist")[c].split(":")
            if (r[0] == material && r[1].toInt() < e.block.y){
                e.player?.sendMessage(prefix + "§c${r[0]}はY${r[1]}より下にしか置けません！")
                e.isBuildable = false
            }

        }

    }

    @EventHandler
    fun putbucket(e : PlayerBucketEmptyEvent){
        if (!mode)return
        if (e.bucket == Material.MILK_BUCKET)return
        val material = e.bucket.name
        for (c in 0..config.getStringList("blocklist").size.minus(1)){
            val r = config.getStringList("blocklist")[c].split(":")
            if (r[0] == material && r[1].toInt() < e.block.y){
                e.player.sendMessage(prefix + "§c${r[0]}はY${r[1]}より下にしか置けません！")
                e.isCancelled = true
            }

        }
    }



}