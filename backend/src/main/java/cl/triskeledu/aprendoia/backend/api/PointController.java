package cl.triskeledu.aprendoia.backend.api;

import cl.triskeledu.aprendoia.backend.model.Point;
import cl.triskeledu.aprendoia.backend.service.PointService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/points")
public class PointController {
    private final PointService service;
    public PointController(PointService service){this.service=service;}
    @GetMapping public List<Point> list(){return service.all();}
    @GetMapping("/{id}") public Point detail(@PathVariable Long id){return service.one(id);}
    @PostMapping public ResponseEntity<Point> create(@Valid @RequestBody PointInput input){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(input));}
    @PutMapping("/{id}") public Point update(@PathVariable Long id,@Valid @RequestBody PointInput input){return service.update(id,input);}
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){service.remove(id);return ResponseEntity.noContent().build();}
}
