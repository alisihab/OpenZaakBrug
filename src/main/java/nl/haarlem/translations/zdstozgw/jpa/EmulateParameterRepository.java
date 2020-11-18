package nl.haarlem.translations.zdstozgw.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nl.haarlem.translations.zdstozgw.jpa.model.EmulateParameter;

@Repository
public interface EmulateParameterRepository extends JpaRepository<EmulateParameter, String> {
}