package net.theevilreaper.dungeon.data.floor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FloorProviderTest {

    private FloorProvider floorProvider;

    @BeforeEach
    void init() {
        this.floorProvider = new FloorProvider(null);
    }

    @Test
    void testAddModel() {
        assertTrue(this.floorProvider.getFloors().isEmpty());
        this.floorProvider.addFloor(FloorTestModel.getModel());
        assertEquals(1, this.floorProvider.getFloors().size());
    }

    @Test
    void testRemoveModel() {
        assertTrue(this.floorProvider.getFloors().isEmpty());
        Floor floor = Floor.builder().setFloorID(12).build();

        for (int i = 0; i < 5; i++) {
            this.floorProvider.addFloor(FloorTestModel.getModel(i, null));
        }

        this.floorProvider.addFloor(floor);

        assertFalse(this.floorProvider.getFloors().isEmpty());
        assertEquals(6, this.floorProvider.getFloors().size());

        this.floorProvider.removeFloor(floor);

        assertEquals(5, this.floorProvider.getFloors().size());
    }

    @Test
    void testGetByName() {
        assertTrue(this.floorProvider.getFloors().isEmpty());
        for (int i = 0; i < 3; i++) {
            this.floorProvider.addFloor(FloorTestModel.getModel(i, null));
        }

        UUID floorUUID = UUID.randomUUID();
        Floor testFloor = FloorTestModel.getModel(2323, "TestFloor", floorUUID);

        this.floorProvider.addFloor(testFloor);
        assertNull(this.floorProvider.getFloor("Floor"));
        assertNotNull(this.floorProvider.getFloor("TestFloor"));

        var floor = this.floorProvider.getFloorById(floorUUID);
        assertNotNull(floor);
        assertEquals(testFloor, floor);
    }

    @Test
    void testGetByID() {
        assertTrue(this.floorProvider.getFloors().isEmpty());
        for (int i = 0; i < 3; i++) {
            this.floorProvider.addFloor(FloorTestModel.getModel(i, null));
        }
        assertNull(this.floorProvider.getFloorById(UUID.randomUUID()));
    }
}
