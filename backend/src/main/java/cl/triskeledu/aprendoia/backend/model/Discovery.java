package cl.triskeledu.aprendoia.backend.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="discoveries", uniqueConstraints=@UniqueConstraint(columnNames={"player_id","point_id"}))
public class Discovery {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="player_id", nullable=false)
    private Long playerId;
    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="point_id", nullable=false)
    private Point point;
    @Column(nullable=false)
    private Instant scannedAt;
    protected Discovery() {}
    public Discovery(Long playerId,Point point){this.playerId=playerId;this.point=point;this.scannedAt=Instant.now();}
    public Long getId(){return id;}
    public Long getPlayerId(){return playerId;}
    public Point getPoint(){return point;}
    public Instant getScannedAt(){return scannedAt;}
}
