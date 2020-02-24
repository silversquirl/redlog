# RedLog

A redstone logger for Minecraft 1.15, implemented as an extension to [fabric-carpet].

[fabric-carpet]: https://github.com/gnembon/fabric-carpet

## What does it do?

RedLog is a Fabric mod that logs events in the world to your chat. It's used through Carpet's `/log` command.

Currently supported events:

- `block36` Block 36 creation/deletion (ie. blocks being moved by pistons)
- `blockEvents` Block event execution (eg. pistons starting to move)
- `blockUpdates` Block/neighbor updates - anything that's detected by a BUD
- `stateUpdates` State/observer updates - anything that's detected by an observer
- `tileTicks` Tile tick execution (eg. repeaters, comparators, observers, etc. powering/depowering)

## How do I filter events?

Sometimes, the volume of events you get from the logger is simply too much. This is especially the case for the noisier loggers like block updates. In this case, you can employ the help of the filter system.

A filters is a scarpet expression that has the following variables defined:

- `_` The block that the event is at
- `x`, `y`, `z` The coordinates of that block (also obtainable as a triple through `pos(_)`)
- `info` The stringified information of the event, as shown in the logger output

Filters are configured through logger options. For example, to configure RedLog to only log block events, but only for sticky pistons, you could run the command `/log blockEvents _ == 'sticky_piston'`, or to log only block updates at blocks with X coordinate 0, `/log blockUpdates x == 0`.

## How do I get it?

Currently, RedLog is in the early stages of development, but if you want to test it out you can check the [releases tab] to download the latest build.

Alternatively, to get the very latest ~~bugs~~ features, you can compile it yourself. To do so, install a JDK, then clone or download the repository and run `./gradlew remapJar`. The resulting JAR can be found in `build/libs`.

[releases tab]: https://github.com/vktec/redlog/releases
