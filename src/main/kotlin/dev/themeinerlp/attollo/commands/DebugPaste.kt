package dev.themeinerlp.attollo.commands

import dev.themeinerlp.attollo.Attollo
import dev.themeinerlp.attollo.BYTEBIN_BASE_URL
import dev.themeinerlp.plugindebug.BukkitDebugBuilder
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.TabCompleter
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class DebugPaste(
    val attollo: Attollo,
) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size == 1 && args[0].equals("debugpaste", true)) {
            val result =
                BukkitDebugBuilder.builder(BYTEBIN_BASE_URL)
                    .collectLatestSpigotLog()
                    .defaultPaperDebugInformation()
                    .addYAML(attollo.config.saveToString(), "Plugin config")
                    .upload()
            val encodedUrl = URLEncoder.encode(
                result.uploadServer,
                StandardCharsets.UTF_8
            )
            val openUrl = "https://debugpaste.onelitefeather.net/#/${result.code}/$encodedUrl/"
            if (sender is ConsoleCommandSender) {
                sender.sendMessage(
                    MiniMessage.miniMessage()
                        .deserialize("<#05b9ff>[Attollo] <yellow>Debug Paste: $openUrl")
                )
            } else {
                sender.sendMessage(
                    MiniMessage.miniMessage()
                        .deserialize("<#05b9ff>[Attollo] <yellow><click:OPEN_URL:'$openUrl'>Click <u>here</u> to open the debug paste</click>")
                )
            }

        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?,
    ): List<String> {
        return listOf("debugpaste")
    }
}