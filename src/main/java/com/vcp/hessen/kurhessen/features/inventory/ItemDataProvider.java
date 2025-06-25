package com.vcp.hessen.kurhessen.features.inventory;

import com.vaadin.flow.data.provider.hierarchy.AbstractHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vcp.hessen.kurhessen.features.inventory.data.Item;
import com.vcp.hessen.kurhessen.features.inventory.data.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

// Implement the Data Provider for TreeGrid
@Service
@Slf4j
public class ItemDataProvider extends AbstractHierarchicalDataProvider<Item, Void> {

    private final ItemRepository itemRepository;

    public ItemDataProvider(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    // Determines if the item has any children
    @Override
    @Transactional(readOnly = true)
    public boolean hasChildren(@NotNull Item item) {
//        Hibernate.initialize(item);
        return itemRepository.countChildElementsOfItem(item) > 0;  // Check if the item has children in the Set<Item>
    }

    // Fetch the root items (those with no parent/container)
    @Override
    @Transactional(readOnly = true)
    public int getChildCount(@NotNull HierarchicalQuery<Item, Void> query) {
        if (query.getParentOptional().isEmpty()) {
            // Root level
            return itemRepository.findItemsWithoutContainer().size();
        } else {
            // Child level
            Item parent = query.getParentOptional().get();
            return itemRepository.countChildElementsOfItem(parent);
        }
    }

    // Fetch the children of a given parent item
    @Override
    @Transactional(readOnly = true)
    public Stream<Item> fetchChildren(@NotNull HierarchicalQuery<Item, Void> query) {
        if (query.getParentOptional().isEmpty()) {
            // Fetch root items if there is no parent
            return itemRepository.findItemsWithoutContainer().stream();
        } else {
            // Fetch children for the given parent item
            return itemRepository.findItemsByContainer(query.getParent()).stream();
        }
    }

    @Override
    public boolean isInMemory() {
        return false;  // We are fetching data from a database, so not in-memory.
    }
}
