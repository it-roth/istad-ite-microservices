package kh.edu.istad.platform.order.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class OrderAddressEntity {
    @Column(name = "address_id")
    private UUID id;
    private String street;
    private String postalCode;
    private String city;
}
