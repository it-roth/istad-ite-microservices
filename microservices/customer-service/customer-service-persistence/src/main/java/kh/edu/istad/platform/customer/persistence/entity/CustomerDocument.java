package kh.edu.istad.platform.customer.persistence.entity;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "customers")
public class CustomerDocument implements Persistable<String> {

    @Id
    private String id;

    private String username;
    private String familyName;
    private String givenName;
    private String email;
    private String phoneNumber;
    private CustomerStatus customerStatus;
    @DBRef
    private CustomerSegmentDocument customerSegmentDocument;

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedBy
    private String modifiedBy;

    @LastModifiedDate
    private Instant modifiedAt;

    @Override
    public boolean isNew() {
        return createdAt == null;
    }
}