# RedLog

A redstone logger for Minecraft 1.15, implemented as an extension to [fabric-carpet].

[fabric-carpet]: https://github.com/gnembon/fabric-carpet

## What does it do?

RedLog is a Fabric mod that logs events in the world to your chat. It's used through Carpet's `/log` command, with filters (wip) configurable through the `/redlog` command.

Currently supported events:

- `block36` Block 36 creation/deletion (ie. blocks being moved by pistons)
- `blockEvents` Block event execution (eg. pistons starting to move)
- `blockUpdates` Block/neighbor updates - anything that's detected by a BUD
- `stateUpdates` State/observer updates - anything that's detected by an observer
- `tileTicks` Tile tick execution (eg. repeaters, comparators, observers, etc. powering/depowering)

## How do I filter events?

Sometimes, the volume of events you get from the logger is simply too much. This is especially the case for the noisier loggers like block updates. In this case, you can employ the help of the filter system. Currently, you can only filter by block name, but this will change in the near future to allow much more complex filters.

Filters are configured through the `/redlog` command, which has the same syntax as `/log`. For example, to configure RedLog to only log block events for sticky pistons, you could run the command `/redlog blockEvents sticky_piston`. To make this permanent across restarts, you would run `/redlog setDefault blockEvents sticky_piston`.

## How do I get it?

Currently, RedLog is in the early stages of development. If you want to test it out, you'll need to compile it yourself. To do so, install a JDK, then clone or download the repository and run `./gradlew remapJar`. The resulting JAR can be found in `build/libs`.
