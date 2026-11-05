package kh.edu.istad.iam.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "permissions")
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String permission;

    private String description;

    @ManyToMany(mappedBy = "permissions")
    private List<User> users;

    @ManyToMany(mappedBy = "permissions")
    private List<Role> roles;

}
