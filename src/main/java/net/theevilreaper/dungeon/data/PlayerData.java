package net.theevilreaper.dungeon.data;

import net.theevilreaper.dungeon.inventory.region.search.SearchOption;
import org.jetbrains.annotations.NotNull;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since
 **/

public class PlayerData {

    private SearchOption searchOption;

    public PlayerData() {
        this.searchOption = null;
    }

    public void setSearchOption(SearchOption searchOption) {
        this.searchOption = searchOption;
    }

    public @NotNull SearchOption getSearchOption() {
        return searchOption;
    }
}
