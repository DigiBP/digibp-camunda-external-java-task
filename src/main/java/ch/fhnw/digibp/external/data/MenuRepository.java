/*
 * Copyright (c) 2019. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.fhnw.digibp.external.data;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MenuRepository{
    private List<String> menuItems = new ArrayList<>();

    public String getMenuItems() {
        return String.join(", ",  menuItems);
    }

    public void setMenuItem(String menuItem) {
        if(!menuItem.isEmpty())
            menuItems.add(menuItem);
    }
}
