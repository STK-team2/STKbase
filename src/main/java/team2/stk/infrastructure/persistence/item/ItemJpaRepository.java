package team2.stk.infrastructure.persistence.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team2.stk.domain.item.Item;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemJpaRepository extends JpaRepository<Item, UUID> {

    @Query("SELECT i FROM Item i WHERE i.deletedAt IS NULL")
    List<Item> findAllActive();

    @Query("SELECT i FROM Item i WHERE i.id = :id AND i.deletedAt IS NULL")
    Optional<Item> findByIdActive(@Param("id") UUID id);

    @Query("SELECT i FROM Item i WHERE i.itemCode = :itemCode AND i.deletedAt IS NULL")
    Optional<Item> findByItemCodeActive(@Param("itemCode") String itemCode);

    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Item i WHERE i.itemCode = :itemCode AND i.deletedAt IS NULL")
    boolean existsByItemCodeActive(@Param("itemCode") String itemCode);

    @Query("SELECT i FROM Item i WHERE i.deletedAt IS NULL AND " +
           "(LOWER(i.itemCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(i.itemName) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Item> searchActive(@Param("query") String query);
}