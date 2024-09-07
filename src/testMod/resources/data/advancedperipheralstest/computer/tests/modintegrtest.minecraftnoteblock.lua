---
--- Advanced Peripherals tests for the Minecraft integration on Note Blocks
--- Covers `playNote`, `getNote`, `changeNoteBy`, `changeNote`
---

test.eq("note_block", peripheral.getType("left"), "Peripheral should be noteBlock")
noteBlock = peripheral.wrap("left")
test.assert(noteBlock, "Peripheral not found")

test.eq(4, noteBlock.getNote(), "Note should be 4")
test.eq(24, noteBlock.changeNoteBy(24), "Note should be 24 after setting it to 24")
test.eq(24, noteBlock.getNote(), "Note should be 24")
test.eq(0, noteBlock.changeNote(), "Note should be 0 after cycling it")
test.eq(0, noteBlock.getNote(), "Note should be 0")
test.eq(1, noteBlock.changeNote(), "Note should be 1 after cycling it")
test.eq(1, noteBlock.getNote(), "Note should be 1")
noteBlock.playNote()

-- this note block has a block above it, so it should not play a note
test.eq("noteBlock", peripheral.getType("right"), "Peripheral should be noteBlock")
silentNoteBlock = peripheral.wrap("right")
test.assert(silentNoteBlock, "Peripheral not found")

silentNoteBlock.playNote()