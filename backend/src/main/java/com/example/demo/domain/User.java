package com.example.demo.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data

@Entity
@Table(name = "user_entity")
public class User implements Serializable {
    private static final long serialVersionUID = -8120043060660540974L;

    @Id
    @SequenceGenerator( name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    @Column(nullable = false, updatable = false)
    private Long id;
//    @Column(columnDefinition = "varchar(36)", unique = true, nullable= false)
    private UUID publicId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String profileImageUrl;
    private LocalDate joinDate; // creationTimestamp
    private LocalDate lastLoginDate;  // updateTimestamp
    private LocalDate dateOfBirth;
    private boolean isActive;
    private boolean isNotLocked;
    @Setter(AccessLevel.NONE)
    @Transient
    private int age;
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private Set<Role> roles; // ROLE_USER, ROLE_ADMIN, ...
    @Setter(AccessLevel.NONE)
    @Transient
    private Set<Authority> authorities; // delete, update, create, read, ...

    public Set<Authority> getAuthorities(){
        return roles.stream().map(Role::getAuthorities).flatMap(Set::stream).collect(Collectors.toSet());
    }

    public int getAge(){
        if(dateOfBirth == null) return -1;
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

}
