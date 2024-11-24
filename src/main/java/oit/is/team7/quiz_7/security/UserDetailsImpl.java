package oit.is.team7.quiz_7.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsImpl implements UserDetails {
  private static final long serialVersionUID = 1L;
  private String username;
  private String password;
  private Collection<GrantedAuthority> authorities;
  private boolean available;

  public UserDetailsImpl(String username, String password, Collection<GrantedAuthority> authorities,
      boolean available) {
    this.username = username;
    this.password = password;
    this.authorities = authorities;
    this.available = available;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return this.available;
  }
}
