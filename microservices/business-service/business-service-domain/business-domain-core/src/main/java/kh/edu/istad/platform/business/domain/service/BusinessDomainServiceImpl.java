package kh.edu.istad.platform.business.domain.service;

import kh.edu.istad.platform.business.domain.entity.Business;
import kh.edu.istad.platform.business.domain.event.BusinessRegisteredEvent;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class BusinessDomainServiceImpl implements BusinessDomainService {

    @Override
    public BusinessRegisteredEvent validateAndInitiateBusiness(Business business) {
        business.validateBusiness();
        business.initializeBusiness();
        return new BusinessRegisteredEvent(business, ZonedDateTime.now(ZoneId.of("UTC")));
    }
}
