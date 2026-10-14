package kh.edu.istad.platform.order.domain.port.out;

import kh.edu.istad.platform.order.domain.entity.Business;

public interface BusinessRepository {
    Business findBusinessInformation(Business business);
}
