package cl.triskeledu.aprendoia.backend.repository;

import cl.triskeledu.aprendoia.backend.model.Point;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface PointRepository extends JpaRepository<Point,Long> {
    Optional<Point> findByQrPayload(String qrPayload);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Point p where p.qrPayload = :qr")
    Optional<Point> lockByQrPayload(@Param("qr") String qr);
}
