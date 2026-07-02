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
- `/cmdctladmin reload` reloads the plugin configuration

## Build

```bash
gradle build
```

The plugin jar is written to `build/libs/command-control-1.0.0.jar`.

## License

GPL-3.0-or-later. See [LICENSE](LICENSE).
