package com.culturespot.culturespotdomain.auth.entity;

import com.culturespot.culturespotdomain.auth.enums.OAuthProvider;
import com.culturespot.culturespotdomain.auth.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends Base {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(unique = true, nullable = false)
  private String username;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OAuthProvider oAuthProvider;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserRole userRoles;

  @Builder
  private User(String email, String password, String username, OAuthProvider oAuthProvider, UserRole userRoles) {
    this.email = email;
    this.password = password;
    this.username = username;
    this.oAuthProvider = oAuthProvider;
    this.userRoles = userRoles;
  }

//  public void changePassword(String newPassword) {
//    this.password = newPassword;
//  }
}
