package com.vcp.hessen.kurhessen.features.inventory;

import com.vaadin.flow.data.provider.AbstractDataProvider;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vcp.hessen.kurhessen.features.inventory.data.Item;

public class ItemTreeDataProvider extends TreeDataProvider<Item> {
    public ItemTreeDataProvider(TreeData<Item> treeData) {
        super(treeData);
    }
}
