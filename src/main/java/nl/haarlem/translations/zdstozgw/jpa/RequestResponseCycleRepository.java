package nl.haarlem.translations.zdstozgw.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nl.haarlem.translations.zdstozgw.jpa.model.RequestResponseCycle;

@Repository
public interface RequestResponseCycleRepository extends JpaRepository<RequestResponseCycle, Long> {
 	
}
