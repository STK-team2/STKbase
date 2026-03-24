package team2.stk.infrastructure.persistence.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team2.stk.domain.item.Item;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final ItemJpaRepository itemJpaRepository;

    public List<Item> findAllActive() {
        return itemJpaRepository.findAllActive();
    }

    public Optional<Item> findByIdActive(UUID id) {
        return itemJpaRepository.findByIdActive(id);
    }

    public Optional<Item> findByItemCodeActive(String itemCode) {
        return itemJpaRepository.findByItemCodeActive(itemCode);
    }

    public boolean existsByItemCodeActive(String itemCode) {
        return itemJpaRepository.existsByItemCodeActive(itemCode);
    }

    public List<Item> searchActive(String query) {
        return itemJpaRepository.searchActive(query);
    }

    public Item save(Item item) {
        return itemJpaRepository.save(item);
    }
}