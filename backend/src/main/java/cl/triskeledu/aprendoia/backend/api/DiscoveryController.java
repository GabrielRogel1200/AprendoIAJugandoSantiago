package cl.triskeledu.aprendoia.backend.api;

import cl.triskeledu.aprendoia.backend.model.Discovery;
import cl.triskeledu.aprendoia.backend.service.PointService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/discoveries")
public class DiscoveryController {
    private final PointService service;
    public DiscoveryController(PointService service){this.service=service;}
    @PostMapping("/scan") public ScanResponse scan(@Valid @RequestBody ScanRequest input){return service.scan(input);}
    @GetMapping("/player/{playerId}") public List<DiscoveryView> history(@PathVariable Long playerId){
        return service.playerDiscoveries(playerId).stream().map(d->new DiscoveryView(d.getPoint().getId(),d.getPoint().getTitle(),d.getScannedAt())).toList();
    }
    public record DiscoveryView(Long pointId,String title,Instant scannedAt){}
}
