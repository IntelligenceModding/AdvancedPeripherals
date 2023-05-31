# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed
- Change the argument of `isItemCrafting` to an item filter table

### Fixed
- [#434] `getItem` throwing NullPointerException if the item does not exist in the me system
- [#444] Cardinal directions aren't working for some of our peripherals - Thanks to @zyxkad !
- [#436] fluid stacks returned by the me or rs bridge are missing some information like the display name, fingerprint or nbt values
- [#448] Wrong return values of the `getInputFluid/getOutputFluid` functions of the create basin integration - Thanks to @zyxkad
- [#439] Fixed wrong calculation of the player position which leads to false results of some functions of the player detector - Thanks to @zyxkad
- [#454] Fixed cache blocking of the rs bridge which leads to a disability to remove items from the rs system
- [#456] Fixed a bug which leads to crashes when trying to get the happiness of the citizens - Thanks to @Einhornyordle!
- Fixed `writeTable` function of the storage peripheral
- Fixed a bug where the item of a filter gets ignored if nbt values are defined
- [#425] Fixed patchouli urls
- [#463] Fix create integration for create 0.5.1a
- [#464] Fixed that the inventory manager causes items to stop stacking

### Added
- [#445] Added the peripheral name to the `playerClick` event of the player detector - Thanks to @zyxkad!
- [#467] Added playerJoin, playerLeave, playerChangedDimension events on Player Detector - Thanks to @michele-grifa!
- Increase max range of the radius of sphere operations

## [1.19.2-0.7.27r] - 2023-04-15

### Fixed
- [#433] Fixed that items will be exported regardless if the target can accept the items - RS and ME Bridge
- Fixed return type of `removeItem` in the inventory manager

## [1.19.2-0.7.26r] - 2023-04-14

### Added
- [#429]Add back support for ae2 things
- Added powah integration - thanks to Sabry Chasseray!
- Added some checks to the inventory manager

### Fixed
- Fixed that the rs bridge does not export items if the item is distributed between multiple storages - thanks to Rudy Gambelini!
- [#427]Fixed that nbt values of items that are moved get deleted in the me bridge.
- [#430]Add `craftFluid` to the me bridge
- Fixed chatbox event calling when typing

### Changed
- [#432]Disable curio slot
- Disable fuel consumption of the pocket computer - can be enabled in the config

## [1.19.2-0.7.25r] - 2023-03-15

### Added
- [#416]Add the crafting job to the crafting cpu object - me bridge

### Fixed
- [#416]Try to parse the `nbt` argument as a table if parsing it as a string fails. Adds the ability to use the output of our functions like `listItems` or `getItem` as item filter argument.
- [#417]Disable power consumption if powered peripherals are disabled in the configuration
- [#423]Fixed that some functions of the me and rs bridge ignore or increasing the count they want to export

## [1.19.2-0.7.24b] - 2023-03-01

### This is the backport of the latest 1.19.3 version to 1.19.2. To see what everything changed, check the [changelog](https://docs.intelligence-modding.de/changelogs/0.7.24r/)

## [1.19.3-0.7.24b] - 2023-02-12

### Fixed
- [#411] Fixed nil return values of `getItem` - RS Bridge

## [1.19.3-0.7.23b] - 2023-02-11

### Breaking Changes
- This update changes the way how we transfer items. This fixes a lot of bugs but also changes some functions. This can break some scripts you might use. Please refer to the [changelog](https://docs.intelligence-modding.de/changelogs/0.7.24r/)

### Added
- Added optional arguments to specify which crafting cpu you want to use - Thanks to Michele Grifa! - ME Bridge
- Added `getTotalItemStorage`, `getTotalFluidStorage`, `getUsedItemStorage`, `getUsedFluidStorage`, `getAvailableItemStorage`, `getAvailableFluidStorage` - Thanks to Michele Grifa! - ME Bridge
- Added `listCells` function - Thanks to Michele Grifa! - ME Bridge
- Added methods to import/export fluid from/to the ME System - Thanks to Michele Grifa!
- Added support for shears to the `digBlock` function of automata turtles
- [#397] The RS, ME Bridge and the inventory manager are now able to ignore NBT values if none is specified
- Added support for offhand items to the inventory manager, the slot is 36.

### Fixed
- [#349] Fixed create fluid tank integration with multiblocks
- [#356] Fixed LuaException if the player detector is moved around in the inventory
- [#398] Fix wrong return types of basin integration
- [#384] Fix long execution times of some redstone integrator functions
- [#393] Check if items can be extracted before inserting, fixes dupe bugs - RS and ME Bridge
- Particles of our blocks
- [#378] Clear canvas if a player switches worlds/servers
- [#393, #361, #371. #407, #406, #408] Fixed several issues with the inventory system. Like ignored NBT values, deleted NBT values, ignored armor, dupe bugs or wrong transmitted items.

## [1.19.2-0.7.22b] - 2022-12-02

### Added
- Added `getRadiation` to the environment detector

### Fixed
- Fixed chat box event
- [#352] Fixed RS bridge crashing the game when connecting it to a cable
- [#355] Don't allow negative numbers for the energy detector

## [1.19.2-0.7.21b] - 2022-10-04

### Added
- Added `isUnderRaid` to the colony integrator

### Fixed
- Fixed language entries
- Fixed rs bridge and colony integrator recipe

## [1.19.2-0.7.20a] - 2022-09-26

### Known Issues
- Missing language entries

### Fixed
- [#341] Fixed `isThunder` - environment detector
- [#347] Fixed crash on server startup

## [1.19.2-0.7.20a] - 2022-09-24

### Fixed
- [#341] Fixed `isThunder` - environment detector
- [#347] Fix crash on server startup due to client side classes

## [1.19.2-0.7.19a] - 2022-09-18

This version supports Create, AE2, Refined Storage, Minecolonies, Botania and Patchouli

### Changed
- Removed `getDimensionProvider`, `getDimensionPath` and `getDimensionPaN` but added `getDimension` which just returns the location of the dimension. As Example `minecraft:nether` or `galacticraft:moon` - environment detector

### Removed
- Disabled the AR Goggles since we want to rewrite the entire system without destroying backwards compatibility in the next 1.19 versions

## [1.18.2-0.7.19r] - 2022-09-06

Bump min forge version to 40.1.52

### Fixed
- [#339, #338] Fixed that commands that are being run in the console are throwing exceptions 

## [1.18.2-0.7.18r] - 2022-09-02

### Fixed
- [#334] Fixed AP turtles and pocket computers appearing in all creative tabs

## [1.18.2-0.7.17r] - 2022-09-01

### Added
- [#342] Added `buildingName`, `targetLevel` and `workOrderType` to `getWorkOrders` - colony integrator
- Added `isCraftable` to fluid stacks - me bridge
- [#313] The chat box `chat` event now listens for the `say` command
- [#317] Added inventory manager nbt support
- [#286] Language updates

### Fixed
- [#174] Fixed client crash when quick moving stacks in the inventory manager
- [#308] Fixed NPE for `getOwner` - inventory manager
- [#281] Fixed wrong amount of pulled items in the inventory manager
- [#280] Fixed `inCoords` functions - player detector
- [#221] Fixed NPE when rs bridge is not connected to a network
- [#321] Fixed that items are voided when importing items into a full me system
- [#319] Fixed `getRequests` - colony integrator
- Fixed that work orders do not return a location
- Fixed console spam and not working table values of buildings - colony integrator

### Changed
- [#323] Improved return values of `craftItem` - me bridge

## [1.18.2-0.7.16b] - 2022-07-18

### Added
- [#314] Added `getItemInOffHand` to the inventory manager
- Added create integration
- Added botania integration

### Fixed
- Village structures *again*
- [#315] Improved performance for `isItemCraftable` - RS Bridge
- [#304] Fix duplicated entries with `listItems` - RS Bridge
- [#270] Added `isCraftable` to fluids and items - RS Bridge

## [1.18.2-0.7.15b] - 2022-05-21

### Fixed
- Minecolonies integration for 1.0.754-ALPHA and newer
- Village structures
- [#297] Fixed tags to fix some RS Bridge functions (Thanks to rayrvg!)

## [1.18.2-0.7.14b] - 2022-04-28

### Fixed
- [#294] `isRequests` does return also true when a sub-ingredient is being crafted (MeBridge, thanks to LauJosefsen!)
- [#293] `isItemCraftable` Does not return true, if an item is craftable, but the amount in the ME system is 0 (MeBridge, thanks to LauJosefsen!)
- [#293] `listCraftableItems` Does not return items that are indeed craftable, but the amount is 0 (MeBridge, thanks to LauJosefsen!)
- [#293] `isItemCrafting` Gives lua error (Java null pointer exception) if amount is 0 (MeBridge, thanks to LauJosefsen!)
- [#295] Fixed scanner height for new world heights (Thanks to JensenJ!)
- [#287] Fixed crash with the chunky turtle throwing an IllegalArgumentException
- [#283] Fixed several issues caused by tags and items (Thanks to AIUbi!)
- Fixed Me Bridge *again*

## [1.18.2-0.7.13a] - 2022-04-07

### Added
- 1.18.2 support

### Fixed
- ME Bridge does work again

## [1.18.1-0.7.12b] - 2022-03-04

Added language updates from crowdin

### Fixed
- Fixed `chat` event
- Fixed compass turtle
- Fixed and improved language files

## [1.18.1-0.7.11b] - 2022-02-15

Small update but with a quite important fix.
I'll fix the chat event in the next update.

### Fixed
- The chunky turtle does now work as intentional

## [1.18.1-0.7.10b] - 2022-02-10

Due to the high amount of minor bugs, AP is now marked as beta.

### Changed
- [#239] Decreased base energy consumption of ME and RS bridge

### Added
- [#261] Ported RsBridge peripheral from 1.16 to 1.18 (Thanks to CanadianBaconBoi)
- [#261] Re-Added `listCraftableItems` (Thanks to CanadianBaconBoi)
- [#263] Added ids to AR goggles (Thanks to ThatGravyBoat)
- [#241] Added `getPlayerRotationZ` and `getPlayerRotationX` to the AR goggles

## [1.18.1-0.7.9r] - 2022-01-26

We'll start adding more mod integrations in the next major 1.18 version.

### Changed
- [#247] We changed the textures of our blocks. Every block is now rotatable and the front texture of every block is now marked with a light aqua box.

### Added
- [#237] Added `sendFormattedMessageToPlayer` to the chat box
- [#237] Added the ability to format prefixes with the chat box
- [#237] UUID's can now be used for `ToPlayer` functions in the chat box.
- [#237] Added a range parameter to the chat box(Parameter 5)
- [#190] The shape and color of the brackets of the chat box can now be changed

### Fixed
- [#228] Typo of metaphysics config file. You may need to redo some configurations.
- [#189] Fixed that the player detector ignores config values.
- Fixed the key `status` of the table of the function `getResearch` from the minecolonies integration.
- Fixed `getBlockData` of the block reader peripheral
- Fixed not dropping blocks
- Fixed several recipes
- [#257] Incompatibility with newer forge versions

## [1.16.5-0.7.7r] - 2022-01-20

### Removed
- Removed mekanism integration. The latest mekanism version does now support computercraft.
- Removed draconic evolution integration.

### Changed
- [#247] We changed the textures of our blocks. Every block is now rotatable and the front texture of every block is now marked with a light aqua box.

### Added
- [#237] Added `sendFormattedMessageToPlayer` to the chat box
- [#237] Added the ability to format prefixes with the chat box
- [#237] UUID's can now be used for `ToPlayer` functions in the chat box.
- [#237] Added a range parameter to the chat box(Parameter 5)
- [#190] The shape and color of the brackets of the chat box can now be changed

### Fixed
- [#228] Typo of metaphysics config file. You may need to redo some configurations.
- [#189] Fixed that the player detector ignores config values.
- Fixed the key `status` of the table of the function `getResearch` from the minecolonies integration.

## [1.18.1-0.7.8.1r] - 2021-12-24

### Fixed
- Fixed incompatibility with ae2 10.0.0-beta.1

## [1.18.1-0.7.8r] - 2021-12-18

### Added
- Minecolonies integration

### Fixed
- [#228]Typo of metaphysics config file. You may need to redo some configurations.
- [#235]Fixed blurry HUD when opening the journey map full screen.
- [#236]Fixed patchouli loading

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
### Changed
- [#247] We changed the textures of our blocks. Every block is now rotatable and the front texture of every block is now marked with a light aqua box.

### Added
- [#237] Added `sendFormattedMessageToPlayer` to the chat box
- [#237] Added the ability to format prefixes with the chat box
- [#237] UUID's can now be used for `ToPlayer` functions in the chat box.
- [#237] Added a range parameter to the chat box(Parameter 5)
- [#190] The shape and color of the brackets of the chat box can now be changed

### Fixed
- [#228] Typo of metaphysics config file. You may need to redo some configurations.
- [#189] Fixed that the player detector ignores config values.
- Fixed the key `status` of the table of the function `getResearch` from the minecolonies integration.

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
