package nl.haarlem.translations.zdstozgw.jpa;

import nl.haarlem.translations.zdstozgw.jpa.model.EmulateParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmulateParameterRepository extends JpaRepository<EmulateParameter, String> {
}