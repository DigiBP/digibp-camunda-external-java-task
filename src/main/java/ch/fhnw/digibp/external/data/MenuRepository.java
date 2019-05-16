/*
 * Copyright (c) 2019. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.fhnw.digibp.external.data;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MenuRepository {
    private Map<String, List<String>> menuItemsDB = new HashMap<>();

    public String getMenuItems(String id) {
        id = Objects.toString(id, "");
        if(menuItemsDB.containsKey(id)){
            return String.join(", ", menuItemsDB.get(id));
        }
        return "";
    }

    public void setMenuItem(String id, String menuItem) {
        id = Objects.toString(id, "");
        if(!menuItemsDB.containsKey(id)){
            menuItemsDB.put(id, new ArrayList<>());
        }
        if(!menuItem.isEmpty())
            menuItemsDB.get(id).add(menuItem);
    }

    public void flushMenuItems(String id) {
        id = Objects.toString(id, "");
        if(menuItemsDB.containsKey(id)){
            menuItemsDB.get(id).clear();
        }
    }
}