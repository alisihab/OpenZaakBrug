package nl.haarlem.translations.zdstozgw.requesthandler.impl.logging;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZdsRequestResponseCycleRepository extends JpaRepository<ZdsRequestResponseCycle, Long> {
}