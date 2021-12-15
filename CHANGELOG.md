# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Fixed
- [#228]Typo of metaphysics config file. You may need to redo some configurations.

## [1.16.5-0.7.6r] - 2021-12-14

### Fixed
- Crashes with newer minecolonies versions.

### Added
- Transmitter integration for mekanism

## [1.18-0.7.7.1r] - 2021-12-10

### Fixed
- Fixed a critical dupe bug with the ME Bridge.

## [1.18-0.7.7r] - 2021-12-10
# 1.18 port
This is the 1.18 port of Advanced Peripherals.
This version comes with applied energistics 2 and curios integration.

## [1.17.1-0.7.6r] - 2021-12-03

# This is the last 1.17 version of Advanced Peripherals.
We will now work on a 1.18 port. Supported is 1.16 and 1.18. Join our discord to read why.
Rest in piece 1.17!

### Fixed
Added compatibility with the latest CC version.

## [1.17.1-0.7.5.1r] - 2021-11-26

### Added
- Added back me bridge and colony integrator recipe.
- Added back colony integrator pocket upgrade.

## [1.17.1-0.7.5r] - 2021-11-26

### Attention
We changed our config system. So if you want to update the mod in your pack, don't forget to use the new files.
We also recommend you to delete the old one, no one needs crap.

This is the port of 0.7.4r, 0.7.4.1b and 0.7.5r as 1.17 version.

### Fixed
- Config typos and descriptions
- A FormattedMessage formatting mistake.
- [#203]Fixed Inventory Manager api, slot argument behaviour is now correct.
- [#219]Fixed that the inventory manager sees slot 0 as nil.

### Added
- Added back AE2 and MineColonies integration.
- [[#224]](https://github.com/Seniorendi/AdvancedPeripherals/pull/224) Added Korean language by mindy15963
- [#223]Added consumption of the ME Bridge to the config.
- [#76]Added noteblock integration
- [#217]Added `getItemInHand`, `getFreeSlot`, `isSpaceAvailable` and `getEmptySpace` to the inventory manager.
- Added support for armor items. You can use the slots 100-103 to access armor items.
- Added more information to the `getPlayerPos` function. (Configurable)
- A new config system. You can find every config in `gamePath/config/AdvancedPeripherals/xxx.toml`
  De divided the config files in 4 different ones. General, World, Peripherals and Metaphysics.
- [#210]Added a new config value, `villagerStructureWeight`. Can be used to change the weight of the structures of our villager.

## [1.16.5-0.7.4.1b] - 2021-11-15

### Attention
We changed our config system. So if you want to update the mod in your pack, don't forget to use the new files.
We also recommend you to delete the old one, no one needs crap.

### Added
- A new config system. You can find every config in `gamePath/config/AdvancedPeripherals/xxx.toml`
  De divided the config files in 4 different ones. General, World, Peripherals and Metaphysics.
- [#210]Added a new config value, `villagerStructureWeight`. Can be used to change the weight of the structures of our villager.

## [1.16.5-0.7.4r] - 2021-11-14

### Fixed

- A FormattedMessage formatting mistake.
- [#203]Fixed Inventory Manager api, slot argument behaviour is now correct.
- [#219]Fixed that the inventory manager sees slot 0 as nil.
- Fixed `getMaxMana` of the mana pool integration.

### Added

- [#76]Added noteblock integration
- [#217]Added `getItemInHand`, `getFreeSlot`, `isSpaceAvailable` and `getEmptySpace` to the inventory manager.
- Added support for armor items. You can use the slots 100-103 to access armor items.
- Added more information to the `getPlayerPos` function. (Configurable)
- Added `getManaNeeded` to the mana pool integration
- [#186] Added draconic evolution integration for the reactor and the energy core.

## [1.17.1-0.7.3r] - 2021-10-13

### Removed

- Removed `listCraftableItems()` from the RSBridge because of some issues

### Added

- `getMaxItemDiskStorage()` to the RS Bridge
- `getMaxFluidDiskStorage()` to the RS Bridge
- `getMaxItemExternalStorage()` to the RS Bridge
- `getMaxFluidExternalStorage()` to the RS Bridge
- `getPattern()` to the RS Bridge
- `isItemCraftable()` to the RS Bridge
- `isItemCraftable()` to the ME Bridge
- Mekanism dynamic tank integration
- french translation

### Fixed

- [#177]Fixed that items after removing it from a chest using the inventory manager does not stack anymore.
- [#194]Fixed error spamming when using `getRequests()` from the colony integrator
- [#203]Fixed Inventory Manager api, slot argument behaviour is now correct.

## [1.17.1-0.7.2r] - 2021-09-06

### Fixed

- `lookAtBlock` now correctly shows tags

## [1.16.5-0.7.1r] - 2021-08-23

### Fixed

- chunky turtle related issues
- RS and ME Bridge related issues
- redstone integrator block updates
- peripheral name of the player detector turtle
- Compatibility with CC:T 1.98.2
