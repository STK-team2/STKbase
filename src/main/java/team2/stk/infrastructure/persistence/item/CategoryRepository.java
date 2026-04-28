package team2.stk.infrastructure.persistence.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team2.stk.domain.item.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;

    public List<Category> findAllActive() {
        return categoryJpaRepository.findAllActive();
    }

    public Optional<Category> findById(UUID id) {
        return categoryJpaRepository.findById(id);
    }

    public Category save(Category category) {
        return categoryJpaRepository.save(category);
    }
}
