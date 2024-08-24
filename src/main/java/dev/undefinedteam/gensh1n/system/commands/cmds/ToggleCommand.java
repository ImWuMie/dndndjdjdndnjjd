package dev.undefinedteam.gensh1n.system.commands.cmds;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.undefinedteam.gensh1n.system.commands.Command;
import dev.undefinedteam.gensh1n.system.commands.args.ModuleArgumentType;
import dev.undefinedteam.gensh1n.system.modules.Module;
import net.minecraft.command.CommandSource;

public class ToggleCommand extends Command {
    public ToggleCommand(){
        super("t", "Toggle the modules", "toggle");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("module", ModuleArgumentType.create()).executes(context -> {
            Module module = context.getArgument("module", Module.class);
            module.toggle();
            return SUCCESS;
        }));
    }
}
