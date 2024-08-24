package dev.undefinedteam.gensh1n.system.commands.cmds;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.undefinedteam.gensh1n.gui.overlay.Notifications;
import dev.undefinedteam.gensh1n.system.commands.Command;
import net.minecraft.command.CommandSource;

public class NotificationCommand extends Command {
    public NotificationCommand() {
        super("noti", "", "n");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("a").then(argument("info", StringArgumentType.greedyString()).executes(context -> {
            var text = StringArgumentType.getString(context, "info");
            Notifications.INSTANCE.push(text, Notifications.Type.INFO, 3000);
            return SUCCESS;
        })));

        builder.then(literal("b").then(argument("warn", StringArgumentType.greedyString()).executes(context -> {
            var text = StringArgumentType.getString(context, "warn");
            Notifications.INSTANCE.push(text, Notifications.Type.WARN, 3000);
            return SUCCESS;
        })));
        builder.then(literal("c").then(argument("error", StringArgumentType.greedyString()).executes(context -> {
            var text = StringArgumentType.getString(context, "error");
            Notifications.INSTANCE.push(text, Notifications.Type.ERROR, 3000);
            return SUCCESS;
        })));
        builder.then(literal("d").then(argument("idk", StringArgumentType.greedyString()).executes(context -> {
            var text = StringArgumentType.getString(context, "idk");
            Notifications.INSTANCE.push(text, Notifications.Type.IDK, 3000);
            return SUCCESS;
        })));

    }
}
