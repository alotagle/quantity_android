package com.mobile.dev.quantity.view.dataStorage;

import com.mobile.dev.quantity.model.Producto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing data persistence on ListView for selected items
 * You must empty this data structures after a transaction has been completed
 */
public class SelectedItems {

    /**
     * An array of product items.
     */
    public static List<Producto> ITEMS = new ArrayList<Producto>();

    /**
     * A map of product  items, by ID.
     */
    public static Map<String, Producto> ITEM_MAP = new HashMap<String, Producto>();

    static {
    }

    private static void addItem(Producto item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getId(), item);
    }

    private static void removeItem(Producto item){

        ITEMS.remove(item);
        ITEM_MAP.remove(item.getId());

    }
}
