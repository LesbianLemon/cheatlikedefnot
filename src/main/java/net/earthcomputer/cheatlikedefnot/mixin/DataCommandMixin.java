package net.earthcomputer.cheatlikedefnot.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.earthcomputer.cheatlikedefnot.Rules;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(DataCommand.class)
public class DataCommandMixin {
    @ModifyConstant(method = "method_13890", constant = @Constant(intValue = 2))
    private static int modifyMainPermissionLevel(int permissionLevel) {
        return Rules.dataGetCommand ? 0 : permissionLevel;
    }

    @ModifyExpressionValue(
            method = {"register", "addModifyArgument"},
            at = @At(value = "INVOKE", target = "net/minecraft/server/command/CommandManager.literal(Ljava/lang/String;)Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;"),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=merge"))
    )
    private static LiteralArgumentBuilder<ServerCommandSource> modifySubPermissionLevel(LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder) {
        // Make sure to load rules before, since rules might be loaded only after this mixin
        Rules.load();
        if (Rules.dataGetCommand) {
            String subcommandName = literalArgumentBuilder.getLiteral();
            return literalArgumentBuilder.requires(source -> subcommandName.equals("get") || source.hasPermissionLevel(2));
        } else {
            return literalArgumentBuilder;
        }
    }
}
