# Redlog

[![Discord](https://img.shields.io/badge/chat%20on-discord-7289DA?logo=discord)](https://discord.gg/zEnfMVJqe6)

Redstone event logger for Minecraft 1.16.

## What does it do?

Redlog is a Fabric mod that logs events in the world to your chat.
It is used through the `/rlog` command.

Currently supported events:

- `b36` - Block 36 creation/deletion (ie. blocks being moved by pistons)
- `bev` - Block event execution (eg. pistons starting to move)
- `bup` - Block/neighbor updates - anything that's detected by a BUD
- `sup` - State/observer updates - anything that's detected by an observer
- `tic` - Tile tick execution (eg. repeaters, comparators, observers, etc. powering/depowering)

## Usage

Redlog filters events based on a per-player list of rules.
Whenever an event occurs, each rule in the list is checked, and the last matching rule is used.
If the last match is a `pass` rule, the event is displayed in the player's chat.
If the last match is a `block` rule, or no rule matches the event, it is silently discarded.
Rules are managed using the `/rlog` command:

- Allow events using `/rlog pass ...`
- Block events using `/rlog block ...`
- List all rules using `/rlog show`
- Delete a rule using `/rlog del <index>`
- Clear the ruleset using `/rlog clear`

## Rules

A rule is a boolean expression, similar to those found in most programming languages.
Variables are available depending on the type of event:

- `all` - always truthy (all events)
- `time` - time at which event occurred, in game ticks (all events)
- `block` - name of block at which event occurred (events related to blocks)
- `x`, `y`, `z` - coordinates of block at which event occurred (all events)
- `cx`, `cz` - coordinates of chunk at which event occurred (chunk ticket events)
- `type` - ticket type (chunk ticket events)
- `level` - ticket level (chunk ticket events)
- `old`, `new` - old and new chunk load level (chunk ticket events)

In addition, a variable with a truthy value is available, named after the type of the event.
If a filter uses a variable that does not exist for the event type, the filter will not match that event.

Some example filters:

```
/rlog pass bev
/rlog block (x > 10 || z > 7 || x <= 0 || z < -10) && y != 3
/rlog pass tic && (x = 3 || y = 7)
```

## How do I get it?

Currently, Redlog is in the early stages of development, but if you want to test it out you can check the [releases tab] to download the latest build.

Alternatively, to get the very latest ~~bugs~~ features, you can compile it yourself.
To do so, install a JDK, then clone or download the repository and run `./gradlew remapJar`.
The resulting JAR can be found in `build/libs`.

[releases tab]: https://github.com/vktec/redlog/releases
