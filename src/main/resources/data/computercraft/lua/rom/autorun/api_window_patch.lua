
local window_create = window.create

local expect = dofile("rom/modules/main/cc/expect.lua").expect

local tHex = {
    [colors.white] = "0",
    [colors.orange] = "1",
    [colors.magenta] = "2",
    [colors.lightBlue] = "3",
    [colors.yellow] = "4",
    [colors.lime] = "5",
    [colors.pink] = "6",
    [colors.gray] = "7",
    [colors.lightGray] = "8",
    [colors.cyan] = "9",
    [colors.purple] = "a",
    [colors.blue] = "b",
    [colors.brown] = "c",
    [colors.green] = "d",
    [colors.red] = "e",
    [colors.black] = "f",
}

local string_rep = string.rep

--- patch window.create
function window.create(parent, nX, nY, nWidth, nHeight, bStartVisible)
    expect(1, parent, "table")
    expect(2, nX, "number")
    expect(3, nY, "number")
    expect(4, nWidth, "number")
    expect(5, nHeight, "number")
    expect(6, bStartVisible, "boolean", "nil")

    if parent == term then
        error("term is not a recommended window parent, try term.current() instead", 2)
    end

    local tPalette = {}
    do
        for i = 0, 15 do
            local c = 2 ^ i
            tPalette[c] = { parent.getPaletteColour(c) }
        end
    end

    local function updatePalette()
        for k, v in pairs(tPalette) do
            parent.setPaletteColour(k, v[1], v[2], v[3], v[4])
        end
    end

    local w = window_create(parent, nX, nY, nWidth, nHeight, bStartVisible)

    --- patch window.setPaletteColour
    function w.setPaletteColour(colour, r, g, b, a)
        expect(1, colour, "number")

        if tHex[colour] == nil then
            error("Invalid color (got " .. colour .. ")" , 2)
        end

        expect(2, r, "number", "nil")
        expect(3, g, "number", "nil")
        expect(4, b, "number", "nil")
        expect(5, a, "number", "nil")
        local tCol
        if g == nil then
            tCol = { colours.unpackRGB(r), 1 }
            tPalette[colour] = tCol
        elseif b == nil then
            tCol = { colours.unpackRGB(r), g }
            tPalette[colour] = tCol
        else
            tCol = tPalette[colour]
            tCol[1] = r
            tCol[2] = g
            tCol[3] = b
            tCol[4] = a == nil and 1 or a
        end

        if bVisible then
            return parent.setPaletteColour(colour, tCol[1], tCol[2], tCol[3], tCol[4])
        end
    end

    w.setPaletteColor = w.setPaletteColour

    --- patch window.getPaletteColour
    function w.getPaletteColour(colour)
        expect(1, colour, "number")
        if tHex[colour] == nil then
            error("Invalid color (got " .. colour .. ")" , 2)
        end
        local tCol = tPalette[colour]
        return tCol[1], tCol[2], tCol[3], tCol[4]
    end

    w.getPaletteColor = w.getPaletteColour

    local window_redraw = w.redraw
    --- patch window.redraw
    function w.redraw()
        if w.isVisible() then
            window_redraw()
            updatePalette()
        end
    end

    return w
end
