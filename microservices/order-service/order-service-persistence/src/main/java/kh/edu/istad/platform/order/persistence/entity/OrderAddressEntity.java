package kh.edu.istad.platform.order.persistence.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class OrderAddressEntity {
    private String street;
    private String postalCode;
    private String city;
}
