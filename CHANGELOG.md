# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
## [1.20.1-0.7.41r] - 2024-10-21

### Added
- Wandering Trader config

### Changed
- Save the owner of the memory card to the inventory manager after a player places the card into the manager and clear the card after. Resolves a security issue where players could eventually steal memory cards from other players

### Fixed
- [#660] Fixed that the inventory manager's `getItemInHand` function adds a blank nbt tag to the item
- [#636] Fixed that the automata's `useOnBlock` function will return PASS in some specific case
- [#645] Fixed that the inventory functions for the powah integration would always return nil
- Fixed that some entity operations don't have enough range
- [#642] Fixed that some powah integrations clashes with the generic energy peripheral from CC which disables some functions
- [#640] Fixed null pointer exception when invoking `getName`
- [#662] Use tick time instead of epoch time for our chunk manager, fixes an issue where chunks unload if some ticks are skipped (lag)

## [1.20.1-0.7.40r] - 2024-06-11

### Added
- [Features/#43] Up/down versions of automata block functions using pitch and yaw - Thanks to @zyxkad
- [Features/#9] Added shift sneaking version of `useOnBlock` - Thanks to @zyxkad

### Fixed
- [#582] Reworked the chunky turtle logic to fix several issues with them - Thanks to @zyxkad
- Fixed a crash with the ME Bridge while trying to iterate through storage busses without a connected storage block
- Fixed `isOnEnchantedSoil()` for the mana flower integration
- [#419] Fixed that the automata `digBlock` function does not use breaking directions the right way. Fixes several issues with hammers or other AOE tools. - Thanks to @zyxkad
- [#629] Fixed that placed blocks do not retain their custom block name when destroyed - Thanks to @WiggleWizzard
- [#621] Fixed that the `listCells` function for the ME Bridge does not search in third party drives - Thanks to @michele-grifa
- [#632] Fixed that the chat box allows sending chat messages with negative range - Thanks to @zyxkad
- [#619] Fixed that the chat box can spoof people to click message to run danger commands - Thanks to @zyxkad
- [#617] Fixed that sending invalid nbt data for some mod items through the chat box can cause a client crash - Thanks to @zyxkad
- [#603] Fixed that the note block integration does not trigger allays - Thanks to @zyxkad
- [#599] Fixed that turtle upgrades can't be equipped with custom NBT values - Thanks to @zyxkad
- [#595] Fixed that `scanEntities` does not include living entity fields - Thanks to @zyxkad
- [#581] Fixed concurrency issues with our server worker, fixes a crash when CC is set to use multiple threads - Thanks to @zyxkad

### Changed
- [#588] Changed the misspelled `maxHealth` parameter in the player detector, the misspelled attribute is still included in the table for backwards compatibility - Thanks to @zyxkad
- [#613] Clamp analog output of the `setAnalogOutput` function between 0-15 - Thanks to @zyxkad

## [1.20.1-0.7.39r] - 2024-04-12
### Fixed
- [#578] Re-Added botania integration with a few new functions
- [#559] Fixed the creation of empty nbt tags when inserting items into the ME System - @Thanks to michele-grifa!
- [#577,#570] Fixed that the RS bridge does not show `isCraftable` in `getItem` or `getPattern` - Thanks to @tomprince!
- [#570] Improved performance of ME Bridge's iterating functions by making the functions not quadratic - Thanks to @tomprince!
- [#560] Fixed stacking problems when using `getItems` from the inventory manager
- [#551] Fixed that RS Bridge's `isItemCrafting` is false when the item is not in the system
- [#536] Invalidate the energy detectors in and output energy providers to prevent the energy detector from stopping to transfer energy
- [#575] Added support for more disk cells and portable cells for the ME Bridge - Thanks to @iTrooz

### Added
- [#571] Added `selectionMode` to the output of ME Bridge's `getCraftingCPUs` function - Thanks to @tomprince!
- [#564] Added optional parameter to `getPlayerPos` to specify the amount of decimal places to retrieve the position of - Thanks to @minecraf7771

## [1.20.1-0.7.38r] - 2024-02-13

### Fixed
- [#556] Fixed a server crash when the toast packet got loaded on the server side
- [#558] Fixed support for the latest minecolonies version
- [#552] Fixed that our automata turtles don't allow charging when `need_fuel` in the CC config is set to true
- [#538] Delete old ar goggles curios tag
 
## [1.20.1-0.7.37r] - 2024-02-09

### Added
- Added `isTileEntity` and `getBlockStates` to the Block Reader
- Added `sendToastToPlayer` and `sendFormattedToastToPlayer` to the Chat Box
- Added the health, the respawn position and the air supply to the player of the player detector.

### Fixed
- [#553] Changed the priority of our chat event to prevent `chat message validation failure` issues

### Changed
- Added random error to `getPlayerPos`. That is by default deactivated. Thanks to @eitan3085!
- Make the prefix of chat box messages dyeable with `&` chars

## [1.20.1-0.7.36r] - 2024-01-17

### Fixed
- [#542] Fixed that our description key bind does not work in specific cases and other windows than the vanilla inventory.

### Changed
- Added the Stack to the resource order of the Colony Integrator and add the fingerprint to common item stacks

## [1.20.1-0.7.35b] - 2023-11-24

### Fixed
- [#530] Fixed stray pixel in inventory_manager_gui.png
- Fixed Memory Card tooltip color
- [#524] Fixed not working brackets color change of the Chat Box - Thanks to @zyxkad!
- [#522] Fixed that the index of the argument `range` for the functions `sendMessageToPlayer` and `sendFormattedMessageToPlayer` in the Chat Box is incorrect - Thanks to @zyxkad!

### Added
- [#519] Added Applied Mekanistics support to the ME Bridge - Thanks to @starcatmeow!

### Changed
- [#474] Changed the inventory manager. Merged the NBT and normal variants of the removeItem and addItem functions to one. See documentation for more info
- [#441] Let the ME bridges `isConnected` function only return true when the ME bridge is actually connected to an active ME system

## [1.20.1-0.7.34b] - 2023-11-03

#### This is the neoforge port for 1.20.1. This version works for Minecraft Forge and for NeoForge.

### Fixed
- Fixed integration for Mekanism 10.4

## [1.20.1-0.7.33b] - 2023-10-07
### Fixed
- Fixed that `craftFluid` of the RS Bridge will not work when the target fluid stack is empty
- Fixed the amount of the costs of researches in the colony integrator
- [#505] Fixed a NullPointerException when calling `craftItem` with the ME Bridge while using the CPU argument
- [#503],[#509] Fixed a bug where the imported items to a ME System are imported without the NBT tag(Thanks to @michele-grifa!)
- [#501] Fixed some stack overflow exceptions when moving items around(Independent of the periphal)
- [#511] Fixed the patchouli book

### Changed
- [#512] Changed some debug messages of the ME and RS Bridge

### Added
- [#514] Added a `neededTime` property to the research table of the colony integrator

## [1.20.1-0.7.32b] - 2023-08-06

### Changed
- Changed sorting of our creative tab

### Added
- [#481] Add configurable chunk loading radius to chunky turtle. Thanks to @Einhornyordle!
- [#483] Add Powah's Ender Cell support. Thanks to @Apeopex!
- [#435] Add a `isFluidCrafting` and `isFluidCraftable` function to the ME Bridge
- [#477] Add two configuration values for the chat box. One for a maximum range and one to disallow multidimensional message sending
- [#491] Added support for Storage Busses in calculation of used storage space for the ME Bridge. Thanks to @Michele Grifa!
- [#494] Add the name to `getCraftingCPUs()`
- [#490] Don't divide the amount of the bytes of the DISK drives
- [#485] Add AE2 Addtions support to the ME Bridge

### Fixed
- [#488] Added support with CC:T 1.106x. Thanks to @SirEdvin!
- [#478] Fixed not working Ae2 Things integration
- [#482] Fixed that some of the functions of automata turtles throw NPEs if the functions were called for the first time
- [#487] Fixed a server crash when two ME Bridges are connected to one ME System while items are scheduled for crafting
- [#490] Fixed that `getUsedItemStorage` does not respect AE2Things DISK drives
- [#486] Fixed that the player detector will not detect players in different dimensions


## [1.20.1-0.7.31a] - 2023-06-29

### Added
- Added the minecolonies integration back
- Added the mekanism integration back

## [1.20.1-0.7.30a] - 2023-06-18

Initial port to 1.20.1. Could and probably will include bugs, please report them on our [issue tracker](https://github.com/SirEndii/AdvancedPeripherals/issues/).
Currently disabled integrations are botania, create, mekanism and powah

## [1.19.3-0.7.29r] - 2023-06-01

### Changed
- Change the argument of `isItemCrafting` to an item filter table
- Removed minecolonies integration without removing the block since minecolonies does not exist for 1.19.3
- Removed botania integration since botania does not exist for 1.19.3

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


## [1.19.3-0.7.28r] - 2023-04-15

### Fixed
- [#433] Fixed that items will be exported regardless if the target can accept the items - RS and ME Bridge
- Fixed return type of `removeItem` in the inventory manager

## [1.19.3-0.7.27r] - 2023-04-14

### Added
- [#429]Add back support for ae2 things
- Added some checks to the inventory manager

### Fixed
- Fixed that the rs bridge does not export items if the item is distributed between multiple storages - thanks to Rudy Gambelini!
- [#427]Fixed that nbt values of items that are moved get deleted in the me bridge.
- [#430]Add `craftFluid` to the me bridge
- Fixed chatbox event calling when typing

### Changed
- [#432]Disable curio slot
- Disable fuel consumption of the pocket computer - can be enabled in the config

## [1.19.3-0.7.26r] - 2023-03-15

### Added
- [#416]Add the crafting job to the crafting cpu object - me bridge

### Fixed
- [#416]Try to parse the `nbt` argument as a table if parsing it as a string fails. Adds the ability to use the output of our functions like `listItems` or `getItem` as item filter argument.
- [#417]Disable power consumption if powered peripherals are disabled in the configuration
- [#423]Fixed that some functions of the me and rs bridge ignore or increasing the count they want to export

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
