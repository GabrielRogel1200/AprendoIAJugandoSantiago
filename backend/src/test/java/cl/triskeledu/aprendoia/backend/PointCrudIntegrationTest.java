package cl.triskeledu.aprendoia.backend;

import cl.triskeledu.aprendoia.backend.api.PointInput;
import cl.triskeledu.aprendoia.backend.model.Point;
import cl.triskeledu.aprendoia.backend.service.PointService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

/** CRUD real con base H2 de pruebas, sin modificar datos productivos. */
@SpringBootTest
class PointCrudIntegrationTest {
    @Autowired PointService service;

    private PointInput input(String qr, String title) {
        return new PointInput(title,"Descripción de prueba",-33.44,-70.65,qr,10);
    }
    @Test void createReadUpdateDeletePoint() {
        String qr = "CRUD-" + UUID.randomUUID();
        Point created = service.create(input(qr,"Lugar original"));
        Long id = created.getId();
        try {
            assertThat(service.one(id).getTitle()).isEqualTo("Lugar original");
            assertThat(service.all()).extracting(Point::getId).contains(id);
            Point updated = service.update(id,input(qr,"Lugar actualizado"));
            assertThat(updated.getTitle()).isEqualTo("Lugar actualizado");
            assertThat(service.one(id).getTitle()).isEqualTo("Lugar actualizado");
        } finally { service.remove(id); }
        assertThatThrownBy(() -> service.one(id))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(error -> assertThat(((ResponseStatusException)error).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }
    @Test void duplicateQrReturnsConflict() {
        String qr = "DUP-" + UUID.randomUUID();
        Point created = service.create(input(qr,"Lugar A"));
        try {
            assertThatThrownBy(() -> service.create(input(qr,"Lugar B")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException)error).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        } finally { service.remove(created.getId()); }
    }
}
