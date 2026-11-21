package cl.triskeledu.aprendoia.backend;

import cl.triskeledu.aprendoia.backend.api.*;
import cl.triskeledu.aprendoia.backend.model.Point;
import cl.triskeledu.aprendoia.backend.repository.PointRepository;
import cl.triskeledu.aprendoia.backend.service.PointService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ScanIntegrationTest {
    @Autowired PointService service;
    @Autowired PointRepository points;

    @Test void samePlayerGetsPointsOnlyOnce() {
        Point place=service.create(new PointInput("Lugar prueba","Descripción de prueba",-33.44,-70.65,"TEST-"+java.util.UUID.randomUUID(),10));
        long playerId=987654321L;
        ScanResponse first=service.scan(new ScanRequest(playerId,place.getQrPayload()));
        ScanResponse second=service.scan(new ScanRequest(playerId,place.getQrPayload()));
        assertThat(first.earnedPoints()).isEqualTo(10);
        assertThat(first.firstScan()).isTrue();
        assertThat(second.earnedPoints()).isZero();
        assertThat(second.firstScan()).isFalse();
    }
    @Test void differentPlayersCanDiscoverSamePlace() {
        Point place=service.create(new PointInput("Lugar compartido","Descripción de prueba",-33.44,-70.65,"TEST-"+java.util.UUID.randomUUID(),10));
        assertThat(service.scan(new ScanRequest(701L,place.getQrPayload())).earnedPoints()).isEqualTo(10);
        assertThat(service.scan(new ScanRequest(702L,place.getQrPayload())).earnedPoints()).isEqualTo(10);
    }
}
