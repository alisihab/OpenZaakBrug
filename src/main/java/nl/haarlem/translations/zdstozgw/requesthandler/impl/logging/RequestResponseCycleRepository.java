package nl.haarlem.translations.zdstozgw.requesthandler.impl.logging;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nl.haarlem.translations.zdstozgw.requesthandler.RequestResponseCycle;

@Repository
public interface RequestResponseCycleRepository extends JpaRepository<RequestResponseCycle, Long> {
}