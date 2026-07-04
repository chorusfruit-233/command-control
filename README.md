# CommandControl

CommandControl is a Paper plugin that gives trusted operators a controlled in-game entry point for running server console commands.

## Requirements

- Paper 1.21.11
- Java 21

## Usage

Add trusted players to `config.yml`, then reload the plugin:

```yaml
authorized-players:
  - uuid: "00000000-0000-0000-0000-000000000000"
    name: "ExamplePlayer"
```

Players must satisfy all of these checks to use `/cmdctl`:

- be listed in `authorized-players`
- be OP
- have `commandcontrol.use`

Commands:

- `/cmdctl <command>` runs `<command>` as the server console
- `/cmdctlsh <linux command>` runs `<linux command>` through `/bin/sh -lc` on the server host
- `/cmdctladmin reload` reloads the plugin configuration

Command output sent to the command sender, such as vanilla command echo messages, is forwarded back to the player who ran `/cmdctl`.

Shell commands require the separate `shell-authorized-players` whitelist. Output is returned after the process exits or times out, with configurable line truncation.

Tab completion is available for proxied Minecraft commands and shell commands. Shell completion suggests executables from `PATH` and file paths from the configured working directory.

On Minecraft/Paper versions with Dialog API support, running `/cmdctlsh` with no arguments opens a command input dialog. Shell command results are shown in a dialog window when available, and fall back to chat output on servers without Dialog API support. If packet-inspection plugins such as GrimAC/PacketEvents cannot map newer Dialog packets, leave `shell.dialog-mode` as `auto` or set it to `disabled`.

## Build

```bash
gradle build
```

The plugin jar is written to `build/libs/command-control-1.0.0.jar`.

This project requires Java 21 for compilation. The repository includes `.java-version`, so jenv should select Java 21 automatically when you are in this directory.

## License

GPL-3.0-or-later. See [LICENSE](LICENSE).
