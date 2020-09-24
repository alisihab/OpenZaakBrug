package nl.haarlem.translations.zdstozgw.requesthandler.impl.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Data;

@Service
@Data
public class RequestResponseCycleService {

	private final RequestResponseCycleRepository requestResponseCycleRepository;

	private final ZgwRequestResponseCycleRepository interimRequestResponseCycleRepository;

	@Autowired
	public RequestResponseCycleService(RequestResponseCycleRepository requestResponseCycleRepository,
			ZgwRequestResponseCycleRepository interimRequestResponseCycleRepository) {
		this.requestResponseCycleRepository = requestResponseCycleRepository;
		this.interimRequestResponseCycleRepository = interimRequestResponseCycleRepository;
	}

	public RequestResponseCycle save(RequestResponseCycle requestResponseCycle) {
		return this.requestResponseCycleRepository.save(requestResponseCycle);
	}

	public ZgwRequestResponseCycle add(ZgwRequestResponseCycle interimRequestResponseCycle) {
		return this.interimRequestResponseCycleRepository.save(interimRequestResponseCycle);
	}

}
