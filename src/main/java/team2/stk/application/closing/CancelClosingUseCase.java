package team2.stk.application.closing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.stk.domain.closing.Closing;
import team2.stk.domain.closing.exception.ClosingNotFoundException;
import team2.stk.infrastructure.persistence.closing.ClosingRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CancelClosingUseCase {

    private final ClosingRepository closingRepository;

    @Transactional
    public void execute(UUID closingId) {
        Closing closing = closingRepository.findById(closingId)
                .orElseThrow(() -> new ClosingNotFoundException(closingId.toString()));

        closing.cancel();
        closingRepository.save(closing);
    }
}