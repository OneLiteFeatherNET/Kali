package net.theevilreaper.dungeon.data.floor;

import net.minestom.server.item.Material;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FloorBuilderTest {

    @Test
    void testFloorCreation() {
        var floor = Floor.builder().setName("Test").setExternalName("ExternalTest").setMaterial(Material.ACACIA_BUTTON).build();
        assertNotNull(floor);
        assertEquals("Test", floor.getName());
        assertEquals("ExternalTest", floor.getExternalName());
        assertEquals(Material.ACACIA_BUTTON, floor.getMaterial());
        assertNotEquals(UUID.randomUUID(), floor.getUUID());
    }
}
