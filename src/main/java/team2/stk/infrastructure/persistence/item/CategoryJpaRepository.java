package team2.stk.infrastructure.persistence.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team2.stk.domain.item.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryJpaRepository extends JpaRepository<Category, UUID> {

    @Query("SELECT c FROM Category c WHERE c.deletedAt IS NULL ORDER BY c.name")
    List<Category> findAllActive();
}
