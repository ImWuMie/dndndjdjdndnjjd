package dev.undefinedteam.gensh1n.system.modules.misc
import dev.undefinedteam.gensh1n.events.client.TickEvent
import dev.undefinedteam.gensh1n.events.game.GameLeftEvent
import dev.undefinedteam.gensh1n.settings.Setting
import dev.undefinedteam.gensh1n.settings.SettingGroup
import dev.undefinedteam.gensh1n.system.modules.Categories
import dev.undefinedteam.gensh1n.system.modules.Module
import dev.undefinedteam.gensh1n.utils.RandomUtils
import meteordevelopment.orbit.EventHandler
import java.io.File
import dev.undefinedteam.gensh1n.Client
import dev.undefinedteam.gensh1n.utils.FileUtils.readText

class Spammer : Module(Categories.Misc, "spammer", "Auto send chats"){
    var sgGeneral: SettingGroup = settings.defaultGroup
    var mode: Setting<Mode> = choice(sgGeneral, "mode", Mode.Text)
    var delay: Setting<Int> = intN(sgGeneral, "delay", 10, 0, 300)
    var spamText: Setting<String> = text(sgGeneral, "text", "Yurnu 666 wumie 666")
    var bypassMode: Setting<BypassMode> = choice(sgGeneral, "bypass-mode", BypassMode.None)
    var bypassPrefix: Setting<Boolean> = bool(sgGeneral, "bypass-prefix", false)
    var bypassPrefixLong: Setting<Int> = intN(sgGeneral, "bypass-prefix-long", 3, 1, 10)
    var bypassSuffix: Setting<Boolean> = bool(sgGeneral, "bypass-suffix", false)
    var bypassSuffixLong: Setting<Int> = intN(sgGeneral, "bypass-suffix-long", 3, 1, 10)
    var Spamfile: Setting<String> = text(sgGeneral, "spam-file", "The file name you want(put the file in superspammer !!!!)", "")
    var ticks = 0.0
    var autoDisable: Setting<Boolean> = bool(sgGeneral, "auto-disable", true)
    enum class Mode {
        Text,
        File
    }
    enum class BypassMode {
        Random,
        None}
    var line: Int = 0
    var fileP = fileProcessor(Spamfile.get())
    @EventHandler
    fun onPre(e: TickEvent.Pre) {
        if (fileP == null){
            fileP = fileProcessor(Spamfile.get())
        }
        ticks += if (ticks < delay.get()) 0.2 else 0.0
    }
    fun buildMessage(text: String): String{
        var message: String = text
        if (bypassMode.get() == BypassMode.Random){
            if (message.startsWith("/"))
                return "`"
            var prefix = if(bypassPrefix.get()) RandomUtils.randomString(bypassPrefixLong.get()) else ""
            var suffix = if(bypassSuffix.get()) RandomUtils.randomString(bypassSuffixLong.get()) else ""
            message = prefix + message + suffix

        }
        return message


    }
    @EventHandler
    fun onPost(e: TickEvent.Post) {
        if (mode.get() == Mode.Text) {
            if (ticks.toInt()>= delay.get()) {
                var message = buildMessage(spamText.get())
                if (message == "`"){
                    mc.player?.networkHandler?.sendChatCommand(message)
                } else{
                    mc.player?.networkHandler?.sendChatMessage(message)
                }

                ticks = 0.0
            }
        } else if (mode.get() == Mode.File) {
            if (fileP != null){
                var message = buildMessage(fileP.get(line))
                mc.player?.networkHandler?.sendChatMessage(message)
                if (line < fileP.size){
                    line ++
                } else {
                    toggle()
                }
            }
        }

    }
    fun fileProcessor(fileName: String): ArrayList<String>{
        val filename = fileName + if (!fileName.endsWith(".txt")) ".txt" else ""
        val idk: File = File(Client.FOLDER, "superspammer")
        if (!idk.exists()) idk.mkdirs()

        return readText(File(idk, filename))
    }
    @EventHandler
    fun onDisconnect(e: GameLeftEvent){
        if (autoDisable.get()){
            toggle()
        }
    }

}
