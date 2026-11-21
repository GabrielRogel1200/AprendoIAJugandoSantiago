package cl.triskeledu.aprendoia.backend.repository;

import cl.triskeledu.aprendoia.backend.model.Discovery;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiscoveryRepository extends JpaRepository<Discovery,Long> {
    boolean existsByPlayerIdAndPointId(Long playerId,Long pointId);
    List<Discovery> findByPlayerIdOrderByScannedAtDesc(Long playerId);
    void deleteByPointId(Long pointId);
}
