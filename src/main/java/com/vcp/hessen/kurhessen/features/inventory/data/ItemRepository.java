package com.vcp.hessen.kurhessen.features.inventory.data;


import com.vcp.hessen.kurhessen.data.Tribe;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID>, JpaSpecificationExecutor<Item> {

    @NotNull
    Optional<Item> findById(@NotNull UUID id);

    @Query("SELECT i FROM Item i WHERE i.container IS NULL")
    @Transactional(readOnly = true)
    List<Item> findItemsWithoutContainer();

    // Find child items for a specific container
    @Query("SELECT i FROM Item i WHERE i.container = :container")
    @Transactional(readOnly = true)
    List<Item> findItemsByContainer(Item container);

    @Query("SELECT count(i.id) FROM Item i where i.container = :item")
    int countChildElementsOfItem(Item item);

    @Query("SELECT i.tags FROM Item i WHERE i.tribe.id = :tribeId")
    Set<String> getAllAvailableTags(long tribeId);

}
