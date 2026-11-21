package cl.triskeledu.aprendoia.backend.config;

import cl.triskeledu.aprendoia.backend.model.Point;
import cl.triskeledu.aprendoia.backend.repository.PointRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoData {
    @Bean CommandLineRunner seedPoints(PointRepository repository){return args->{
        if(repository.count()==0){
            repository.save(new Point("Plaza de Armas","Centro histórico de Santiago, rodeado por edificios patrimoniales y espacios de encuentro.",-33.4372,-70.6506,"TRISKEL-PLAZA-ARMAS",10));
            repository.save(new Point("Cerro Santa Lucía","Cerro urbano con paseos, jardines y miradores sobre el centro de Santiago.",-33.4408,-70.6447,"TRISKEL-SANTA-LUCIA",10));
            repository.save(new Point("Parque Forestal","Parque arbolado junto al río Mapocho, cercano a museos y espacios culturales.",-33.4348,-70.6390,"TRISKEL-PARQUE-FORESTAL",10));
            repository.save(new Point("Palacio de La Moneda","Edificio histórico y sede del gobierno de Chile, frente a la Plaza de la Constitución.",-33.4429,-70.6539,"TRISKEL-LA-MONEDA",10));
        }
    };}
}
