package nl.haarlem.translations.zdstozgw.requesthandler.impl.logging;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Data
public class RequestResponseCycleService {

    private final RequestResponseCycleRepository requestResponseCycleRepository;

    private final InterimRequestResponseCycleRepository interimRequestResponseCycleRepository;

    private RequestResponseCycle requestResponseCycleSession;

    private String sessionUUID;

    @Autowired
    public RequestResponseCycleService(RequestResponseCycleRepository requestResponseCycleRepository,
                                       InterimRequestResponseCycleRepository interimRequestResponseCycleRepository) {
        this.requestResponseCycleRepository = requestResponseCycleRepository;
        this.interimRequestResponseCycleRepository = interimRequestResponseCycleRepository;
    }

    public RequestResponseCycle save(RequestResponseCycle requestResponseCycle) {
        return this.requestResponseCycleRepository.save(requestResponseCycle);
    }

    public InterimRequestResponseCycle add(InterimRequestResponseCycle interimRequestResponseCycle) {
        return this.interimRequestResponseCycleRepository.save(interimRequestResponseCycle);
    }

}
