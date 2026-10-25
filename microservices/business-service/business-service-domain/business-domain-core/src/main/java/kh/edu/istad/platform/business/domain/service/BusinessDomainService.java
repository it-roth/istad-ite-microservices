package kh.edu.istad.platform.business.domain.service;

import kh.edu.istad.platform.business.domain.entity.Business;
import kh.edu.istad.platform.business.domain.event.BusinessRegisteredEvent;

public interface BusinessDomainService {
    BusinessRegisteredEvent validateAndInitiateBusiness(Business business);
}
