package de.srendi.advancedperipherals.common.setup;

public final class CCEvents {
    // Fires when a chat message appears within a chatbox's detection range.
    // by: chatbox
    //
    // "chat", uuid: string | nil, name: string, message: string, isHidden: boolean, encodedUtf8Message: string
    // uuid: Message sender's UUID. `nil` if triggered from a /say command
    // name: Message sender's name. `[say]` if triggered from a /say command
    // message: The chat message
    // isHidden: Whether or not the message is privately sent to chatboxes
    // encodedUtf8Message: The encoded chat message from utf8
    public static final String CHAT = "chat";

    // Fires when a player pressed the hotkey while equipping a smartglasses with hotkey module.
    // by: hotkey_module
    //
    // "glasses_key_pressed", keybind: string, pressed_duration: number
    // keybind: Pressed key's ID
    // pressed_duration: The duration the key pressed down. in ms
    public static final String GLASSES_KEY_PRESSED = "glasses_key_pressed";

    // Fires when a player clicked the block reader with an item in hand.
    // by: block_reader
    //
    // "item_click", peripheral_name: string, item: table
    // peripheral_name: The player detector's name
    // item: Item's data
    public static final String ITEM_CLICK = "item_click";

    // Fires when a player closed keyboard while equipping a smartglasses with keyboard module.
    // by: keyboard_module
    //
    // "keyboard_close"
    public static final String KEYBOARD_CLOSE = "keyboard_close";

    // Fires when a player opened keyboard while equipping a smartglasses with keyboard module.
    // by: keyboard_module
    //
    // "keyboard_open"
    public static final String KEYBOARD_OPEN = "keyboard_open";

    // Fires when a player resized their screen while equipping a smartglasses with overlay module.
    // by: overlay_module
    //
    // "overlay_resize"
    public static final String OVERLAY_RESIZE = "overlay_resize";

    // Fires when a player changed its dimension
    // by: player_detector
    //
    // "player_changed_dimension", uuid: string, name: string, fromDimension: string, toDimension: string
    // uuid: Player's UUID
    // name: Player's name
    // fromDimension: The dimension ID where the player from
    // toDimension: The dimension ID where the player currently at
    public static final String PLAYER_CHANGED_DIMENSION = "player_changed_dimension";

    // Fires when a player clicked the player detector
    // by: player_detector
    //
    // "player_click", peripheral_name: string, uuid: string, name: string, dimension: string
    // peripheral_name: The player detector's name
    // uuid: Player's UUID
    // name: Player's name
    public static final String PLAYER_CLICK = "player_click";

    // Fires when a player died
    // by: player_detector
    //
    // "player_death", uuid: string, name: string, damageSource: string
    // uuid: Player's UUID
    // name: Player's name
    // damageSource: The damage source ID
    public static final String PLAYER_DEATH = "player_death";

    // Fires when a player used an interaction button
    // by: keyboard_module
    //
    // "player_interaction", button: number, block_state: table | nil, entity_uuid: string | nil
    // button: The button being held down
    // block_state: Looking block, if exists
    // entity_uuid: Looking entity, if exists
    public static final String PLAYER_INTERACTION = "player_interaction";

    // Fires when a player joined the game
    // by: player_detector
    //
    // "player_join", uuid: string, name: string, dimension: string
    // uuid: Player's UUID
    // name: Player's name
    // dimension: The dimension ID where the player currently at
    public static final String PLAYER_JOIN = "player_join";

    // Fires when a player left the game
    // by: player_detector
    //
    // "player_leave", uuid: string, name: string, dimension: string
    // uuid: Player's UUID
    // name: Player's name
    // dimension: The dimension ID where the player currently at
    public static final String PLAYER_LEAVE = "player_leave";

    // Fires when a player pressed a mouse button while keyboard module is capturing mouse
    // by: keyboard_module
    //
    // "player_mouse_click", button: number
    // button: The mouse button which clicked down
    public static final String PLAYER_MOUSE_CLICK = "player_mouse_click";

    // Fires when a player moved mouse while keyboard module is capturing mouse
    // by: keyboard_module
    //
    // "player_mouse_move", dx: number, dy: number
    // dx: Horizontal mouse movement
    // dy: Vertical mouse movement
    public static final String PLAYER_MOUSE_MOVE = "player_mouse_move";

    // Fires when a player scrolled mouse while keyboard module is capturing mouse
    // by: keyboard_module
    //
    // "player_mouse_scroll", dy: number, dx: number
    // dy: Vertical mouse scroll amount
    // dx: Horizontal mouse scroll amount
    public static final String PLAYER_MOUSE_SCROLL = "player_mouse_scroll";

    // Fires when a player released a mouse button while keyboard module is capturing mouse
    // by: keyboard_module
    //
    // "player_mouse_up", button: number
    // button: The mouse button which released up
    public static final String PLAYER_MOUSE_UP = "player_mouse_up";

    // Fires when a turtle detected a portal to cross
    // by: end_automata_core
    //
    // "portal_prepare", data: table
    // data: {
    //     name: string = Target dimension ID
    //     pos: Position = Target position
    //     facing: string = Facing after teleported
    //     costs: string = Teleport costs
    //     canSpawn: string = Whether or not the teleport is legal
    //     shipId: string = Transport ID
    // }
    public static final String PORTAL_PREPARE = "portal_prepare";

    // Fires when a turtle failed to find a portal to cross
    // by: end_automata_core
    //
    // "portal_prepare_failed", shipId: number, error: string
    // shipId: the turtle's transport ID
    public static final String PORTAL_PREPARE_FAILED = "portal_prepare_failed";

    // Fires when a turtle captured a creature on its saddle
    // by: saddle_upgrade
    //
    // "saddle_capture"
    public static final String SADDLE_CAPTURE = "saddle_capture";

    // Fires when a player pressed/released movement key while riding on a saddle turtle
    // by: saddle_upgrade
    //
    // "saddle_control", direction: string, pressed: boolean
    // direction: The direction of player's movement key
    // pressed: true if the player pressed down the movement key, or false if released it
    public static final String SADDLE_CONTROL = "saddle_control";

    // Fires when a turtle released a creature on its saddle
    // by: saddle_upgrade
    //
    // "saddle_release"
    public static final String SADDLE_RELEASE = "saddle_release";

    // Fires when a module added to the smartglasses
    // by: smartglasses
    //
    // "glasses_module"
    // id: The module ID
    public static final String GLASSES_MODULE = "glasses_module";

    // Fires when a module removed from the smartglasses
    // by: smartglasses
    //
    // "glasses_module_detach"
    // id: The module ID
    public static final String GLASSES_MODULE_DETACH = "glasses_module_detach";

    private CCEvents() {}
}
