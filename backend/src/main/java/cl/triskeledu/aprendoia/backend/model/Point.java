package cl.triskeledu.aprendoia.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "places", uniqueConstraints = @UniqueConstraint(columnNames = "qr_payload"))
public class Point {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String title;
    @Column(nullable = false, length = 3000)
    private String description;
    @Column(nullable = false)
    private double latitude;
    @Column(nullable = false)
    private double longitude;
    @Column(name = "qr_payload", nullable = false, unique = true, length = 200)
    private String qrPayload;
    @Column(nullable = false)
    private int points = 10;

    protected Point() {}
    public Point(String title,String description,double latitude,double longitude,String qrPayload,int points){
        this.title=title;this.description=description;this.latitude=latitude;this.longitude=longitude;this.qrPayload=qrPayload;this.points=points;
    }
    public Long getId(){return id;}
    public String getTitle(){return title;}
    public void setTitle(String title){this.title=title;}
    public String getDescription(){return description;}
    public void setDescription(String description){this.description=description;}
    public double getLatitude(){return latitude;}
    public void setLatitude(double latitude){this.latitude=latitude;}
    public double getLongitude(){return longitude;}
    public void setLongitude(double longitude){this.longitude=longitude;}
    public String getQrPayload(){return qrPayload;}
    public void setQrPayload(String qrPayload){this.qrPayload=qrPayload;}
    public int getPoints(){return points;}
    public void setPoints(int points){this.points=points;}
}
