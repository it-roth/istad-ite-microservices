package kh.edu.istad.platform.business.persistence.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "businesses")
public class BusinessDocument {

    @Id
    private String id;
    private String businessName;
    private BusinessContactDocument businessContact;
    private List<ProductDocument> products;
    private boolean active;
}
