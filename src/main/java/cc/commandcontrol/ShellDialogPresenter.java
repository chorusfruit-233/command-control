package cc.commandcontrol;

import org.bukkit.entity.Player;

public interface ShellDialogPresenter {
    ShellDialogPresenter UNAVAILABLE = new ShellDialogPresenter() {
        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public void showInput(Player player) {
        }

        @Override
        public void showResult(Player player, ShellCommandPresentation presentation) {
        }
    };

    boolean isAvailable();

    void showInput(Player player);

    void showResult(Player player, ShellCommandPresentation presentation);
}
