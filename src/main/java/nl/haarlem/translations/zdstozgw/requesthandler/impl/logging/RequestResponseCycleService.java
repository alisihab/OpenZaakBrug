package nl.haarlem.translations.zdstozgw.requesthandler.impl.logging;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Data
public class RequestResponseCycleService {

    private final RequestResponseCycleRepository requestResponseCycleRepository;

    private String sessionUUID;

    @Autowired
    public RequestResponseCycleService(RequestResponseCycleRepository requestResponseCycleRepository){
        this.requestResponseCycleRepository = requestResponseCycleRepository;
    }

    public RequestResponseCycle add(RequestResponseCycle requestResponseCycle){
        return this.requestResponseCycleRepository.save(requestResponseCycle);
    }

}
