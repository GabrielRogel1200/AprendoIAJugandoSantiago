package cl.triskeledu.aprendoia.backend.service;

import cl.triskeledu.aprendoia.backend.api.*;
import cl.triskeledu.aprendoia.backend.model.*;
import cl.triskeledu.aprendoia.backend.repository.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class PointService {
    private final PointRepository points;
    private final DiscoveryRepository discoveries;
    public PointService(PointRepository points,DiscoveryRepository discoveries){this.points=points;this.discoveries=discoveries;}

    @Transactional(readOnly=true)
    public List<Point> all(){return points.findAll();}

    @Transactional(readOnly=true)
    public Point one(Long id){return points.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Lugar no encontrado"));}

    @Transactional
    public Point create(PointInput request){
        if(points.findByQrPayload(request.qrPayload().trim()).isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT,"QR ya registrado");
        return points.save(new Point(request.title().trim(),request.description().trim(),request.latitude(),request.longitude(),request.qrPayload().trim(),request.points()));
    }

    @Transactional
    public Point update(Long id,PointInput request){
        Point p=one(id);
        points.findByQrPayload(request.qrPayload().trim()).ifPresent(existing->{
            if(!existing.getId().equals(id)) throw new ResponseStatusException(HttpStatus.CONFLICT,"QR ya registrado");
        });
        p.setTitle(request.title().trim());p.setDescription(request.description().trim());
        p.setLatitude(request.latitude());p.setLongitude(request.longitude());
        p.setQrPayload(request.qrPayload().trim());p.setPoints(request.points());
        return p;
    }

    @Transactional
    public void remove(Long id){
        one(id);
        discoveries.deleteByPointId(id);
        points.deleteById(id);
    }

    @Transactional
    public ScanResponse scan(ScanRequest request){
        Point point=points.lockByQrPayload(request.qrPayload().trim())
            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"QR no registrado"));
        if(discoveries.existsByPlayerIdAndPointId(request.playerId(),point.getId())){
            return new ScanResponse(point.getId(),point.getTitle(),0,false,"Este lugar ya fue descubierto por este jugador");
        }
        discoveries.saveAndFlush(new Discovery(request.playerId(),point));
        return new ScanResponse(point.getId(),point.getTitle(),point.getPoints(),true,"¡Descubrimiento registrado!");
    }
    @Transactional(readOnly=true)
    public List<Discovery> playerDiscoveries(Long playerId){return discoveries.findByPlayerIdOrderByScannedAtDesc(playerId);}
}
