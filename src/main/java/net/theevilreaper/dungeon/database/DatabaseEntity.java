package net.theevilreaper.dungeon.database;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The class includes some methods which can be called on an entity from the database.
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public interface DatabaseEntity<T> {

    /**
     * Inserts a new object from T into the database.
     *
     * @param model The model to add
     */
    void insert(@NotNull T model);

    /**
     * Deletes a given model from the database.
     */
    void delete(@NotNull T model);

    /**
     * Updates a given model in the database.
     *
     * @param model The document to update
     */
    void update(@NotNull T model);

    /**
     * Returns a list with all T
     * @return a list which contains the elements
     */
    List<T> getAllEntries();
}