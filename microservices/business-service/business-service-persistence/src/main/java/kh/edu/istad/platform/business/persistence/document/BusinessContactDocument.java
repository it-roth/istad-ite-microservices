package kh.edu.istad.platform.business.persistence.document;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessContactDocument {
    private String email;
    private String phoneNumber;
}
