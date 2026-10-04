# Changelog

All notable changes to this project will be documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
- \[Breaking Change\] All overlay objects can draw on both in world or on GUI now. Use `gui` field to differ the render space.
- Add Smart Chestplate that may perform various operations via smartglasses (in-dev, not final textures, feedback welcome!)
- We investigated a more efficient way to build Distance Detector, now it will use amethyst instead of diamond!
- Make overlay objects fields update smoother by adding lerp ability.
- Add `smartglasses.getOwner()` to directly get wearer's information without player detector.
- Add `dimension` field to entity info. #834
- Fix some AE2 addons may return `null` FuzzyMode and crash the game. #833
- Fix some BlockEntity may crash client due to FakeLevel.
- Fix potion related item recipes are impossible to craft.
- Fix ME Bridge constantly refreshing disks when an empty AEDiskCell is inserted.
- Fix ME Bridge may not detect AEDiskCell insertion.
- Fix Smart Glasses may eat peripherals, and reset unexpectedly inside curios slots.

## [1.21.1-0.8.1a] - 2026-09-06
- `scanEntities` on envrionment detector now can detect specific type of entities by passing the entity type or tag ID as an arugment.
- `player_interaction` event will return block position along with block states.
- Fix chatbox crash when using `/say` command in server interface.
- Fix villager trades incorrect emerald price.
- Fix ME bridge crash when AE2things is not installed.
- Allow render simple block entity (e.g. chest) with overlay module.
- Fix world crash when ME bridge broke with an unfinished crafting job.

## [1.21.1-0.8.0a] - 2026-08-16
Thanks to @zyxkad and his amazing work in the last year, we were finally able to release AP 0.8 after several years of work.
This includes a bunch of new peripherals, an overhaul to the ME and RS Bridge, our new smart glasses and much more.
Please read the changelogs for the [0.8a release](https://docs.advanced-peripherals.de/0.8/changelogs/0.8/) for more details.