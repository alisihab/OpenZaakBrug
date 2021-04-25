package nl.haarlem.translations.zdstozgw.requesthandler.impl.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestResponseCycle;

@Service
@Data
public class RequestResponseCycleService {

	private final RequestResponseCycleRepository requestResponseCycleRepository;
	private final ZgwRequestResponseCycleRepository zgwRequestResponseCycleRepository;
	private final ZdsRequestResponseCycleRepository zdsRequestResponseCycleRepository;

	@Autowired
	public RequestResponseCycleService(RequestResponseCycleRepository requestResponseCycleRepository,
			ZgwRequestResponseCycleRepository zgwRequestResponseCycleRepository, 
			ZdsRequestResponseCycleRepository zdsRequestResponseCycleRepository) {
		this.requestResponseCycleRepository = requestResponseCycleRepository;
		this.zgwRequestResponseCycleRepository = zgwRequestResponseCycleRepository;
		this.zdsRequestResponseCycleRepository = zdsRequestResponseCycleRepository;
	}

	public RequestResponseCycle save(RequestResponseCycle requestResponseCycle) {
		return this.requestResponseCycleRepository.saveAndFlush(requestResponseCycle);
	}

	public ZgwRequestResponseCycle add(ZgwRequestResponseCycle interimRequestResponseCycle) {
		return this.zgwRequestResponseCycleRepository.saveAndFlush(interimRequestResponseCycle);
	}

	public ZdsRequestResponseCycle add(ZdsRequestResponseCycle interimRequestResponseCycle) {
		return this.zdsRequestResponseCycleRepository.saveAndFlush(interimRequestResponseCycle);
	}	
}
