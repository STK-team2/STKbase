package team2.stk.infrastructure.persistence.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team2.stk.domain.user.User;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final UserJpaRepository userJpaRepository;

    public java.util.List<User> findAll() {
        return userJpaRepository.findAll();
    }

    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    public User save(User user) {
        return userJpaRepository.save(user);
    }
}