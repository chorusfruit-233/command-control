package cc.commandcontrol;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.List;

public final class PaperShellDialogPresenter implements ShellDialogPresenter {
    private static final String COMMAND_INPUT_KEY = "command";

    private final JavaPlugin plugin;
    private final ShellCommandSubmissionHandler submissionHandler;

    public PaperShellDialogPresenter(JavaPlugin plugin, ShellCommandSubmissionHandler submissionHandler) {
        this.plugin = plugin;
        this.submissionHandler = submissionHandler;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void showInput(Player player) {
        player.showDialog(Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("CommandControl Shell"))
                        .body(List.of(DialogBody.plainMessage(Component.text("Enter a Linux shell command to run on the server host."), 320)))
                        .inputs(List.of(DialogInput.text(COMMAND_INPUT_KEY, Component.text("Command"))
                                .width(320)
                                .maxLength(2048)
                                .multiline(TextDialogInput.MultilineOptions.create(8, 120))
                                .build()))
                        .canCloseWithEscape(true)
                        .pause(false)
                        .afterAction(DialogBase.DialogAfterAction.CLOSE)
                        .build())
                .type(DialogType.confirmation(executeButton(), cancelButton()))));
    }

    @Override
    public void showResult(Player player, ShellCommandPresentation presentation) {
        player.showDialog(Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text(presentation.title()))
                        .body(List.of(DialogBody.plainMessage(Component.text(presentation.bodyText()), 420)))
                        .canCloseWithEscape(true)
                        .pause(false)
                        .afterAction(DialogBase.DialogAfterAction.CLOSE)
                        .build())
                .type(DialogType.notice())));
    }

    private ActionButton executeButton() {
        return ActionButton.builder(Component.text("Execute"))
                .width(150)
                .action(DialogAction.customClick((response, audience) -> {
                    if (!(audience instanceof Player player)) {
                        return;
                    }
                    String command = response.getText(COMMAND_INPUT_KEY);
                    Bukkit.getScheduler().runTask(plugin, () -> submissionHandler.submit(player, command));
                }, ClickCallback.Options.builder()
                        .uses(1)
                        .lifetime(Duration.ofMinutes(5))
                        .build()))
                .build();
    }

    private ActionButton cancelButton() {
        return ActionButton.builder(Component.text("Cancel"))
                .width(150)
                .action(DialogAction.customClick((response, audience) -> {
                }, ClickCallback.Options.builder()
                        .uses(1)
                        .lifetime(Duration.ofMinutes(5))
                        .build()))
                .build();
    }
}
