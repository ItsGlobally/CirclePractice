package me.itsglobally.circlePractice.utils;

// ArenaPasteWE6.java

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import org.bukkit.World;

import java.io.File;

public class ArenaPasteWE6 {

    public static void pasteSchematicAt(World bukkitWorld,
                                        String schematicName,
                                        int x, int y, int z, int xoffset, int zoffset,
                                        boolean pasteAir) throws Exception {

        File schematicFile = new File("plugins/WorldEdit/schematics", schematicName + ".schematic");

        if (!schematicFile.exists()) {
            throw new IllegalArgumentException("Schematic file not found: " + schematicFile.getAbsolutePath());
        }

        MCEditSchematicFormat format = (MCEditSchematicFormat) MCEditSchematicFormat.getFormat(schematicFile);
        if (format == null) {
            throw new IllegalArgumentException("Unknown schematic format for: " + schematicFile.getName());
        }

        CuboidClipboard clipboard = format.load(schematicFile);
        EditSession editSession = new EditSession(new BukkitWorld(bukkitWorld), Integer.MAX_VALUE);

        Vector pasteAt = new Vector(x + xoffset, y, z + zoffset).subtract(clipboard.getOffset());

        clipboard.paste(editSession, pasteAt, !pasteAir);
        editSession.flushQueue();
    }

}
