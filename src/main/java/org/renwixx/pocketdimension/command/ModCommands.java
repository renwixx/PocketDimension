package org.renwixx.pocketdimension.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.renwixx.pocketdimension.config.PocketConfig;
import org.renwixx.pocketdimension.util.PermissionHelper;
import org.renwixx.pocketdimension.world.PocketState;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Locale;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ModCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("pocketdimension")
                    .then(literal("reload")
                            .requires(source -> PermissionHelper.hasPermission(source, "pocketdimension.op", 2))
                            .executes(context -> {
                                PocketConfig.loadConfig();
                                org.renwixx.pocketdimension.world.RoomGenerator.clearCache();
                                context.getSource()
                                        .sendFeedback(() -> Text.translatable("pocket_dimension.command.reload.success"), false);
                                return 1;
                            }))
                    .then(literal("search")
                            .requires(source -> PermissionHelper.hasPermission(source, "pocketdimension.op", 2))
                            .then(argument("player", GameProfileArgumentType.gameProfile())
                                    .executes(ModCommands::executeSearch))));
        });
    }

    private static int executeSearch(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var profiles = GameProfileArgumentType.getProfileArgument(context, "player");
        if (profiles.isEmpty()) {
            context.getSource().sendError(Text.translatable("pocket_dimension.command.search.player_offline"));
            return 0;
        }

        PocketState state = PocketState.getServerState(context.getSource().getServer());

        for (var profile : profiles) {
            net.minecraft.util.math.GlobalPos blockLoc = state.getBlockLocation(profile.id());
            PocketState.ExitLocation exitLoc = state.getExitLocation(profile.id());

            if (blockLoc != null) {
                String coords = String.format(Locale.ROOT, "%d %d %d", blockLoc.pos().getX(), blockLoc.pos().getY(),
                        blockLoc.pos().getZ());
                String dimension = blockLoc.dimension().getValue().toString();
                sendLocationFeedback(context, "pocket_dimension.command.search.exact", profile.name(), coords,
                        dimension);
                continue;
            }
            if (exitLoc != null) {
                String coords = String.format(Locale.ROOT, "%.0f %.0f %.0f", exitLoc.x(), exitLoc.y(), exitLoc.z());
                String dimension = exitLoc.dimension().toString();
                sendLocationFeedback(context, "pocket_dimension.command.search.near", profile.name(), coords,
                        dimension);
                continue;
            }

            Integer pocketId = state.getPlayerPocket(profile.id());
            if (pocketId != null) {
                context.getSource().sendFeedback(
                        () -> Text.translatable("pocket_dimension.command.search.unknown", profile.name())
                                .formatted(Formatting.YELLOW),
                        false);
                continue;
            }
            context.getSource()
                    .sendError(Text.translatable("pocket_dimension.command.search.not_found", profile.name()));
        }
        return profiles.size();
    }

    private static void sendLocationFeedback(CommandContext<ServerCommandSource> context, String translationKey,
            String playerName, String coords, String dimension) {
        Text coordText = Text.literal(coords)
                .styled(style -> style
                        .withColor(Formatting.YELLOW)
                        .withClickEvent(
                                new ClickEvent.SuggestCommand("/execute in " + dimension + " run tp @s " + coords))
                        .withHoverEvent(new HoverEvent.ShowText(
                                Text.translatable("pocket_dimension.command.search.hover_teleport"))));
        Text dimText = Text.literal(dimension).formatted(Formatting.YELLOW);
        context.getSource().sendFeedback(
                () -> Text.translatable(translationKey, playerName, coordText, dimText).formatted(Formatting.GREEN),
                false);
    }
}
