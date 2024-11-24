package oit.is.team7.quiz_7.security;

import java.util.Collection;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import oit.is.team7.quiz_7.model.UserAccount;
import oit.is.team7.quiz_7.model.UserAccountMapper;

@Component
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  UserAccountMapper userAccountMapper;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // System.err.println("--- Entry ---");
    try {
      UserAccount authedAccount = userAccountMapper.selectUserAccountByUsername(username);
      if (authedAccount == null)
        throw new UsernameNotFoundException("No hit UserAccount. ");
      authedAccount.setRoles(userAccountMapper.selectUserAccountRolesById(authedAccount.getId()));

      String pass = authedAccount.getPass();
      ArrayList<String> roles = authedAccount.getRoles();
      Collection<GrantedAuthority> authorities = new ArrayList<>();
      for (int i = 0; i < roles.size(); i++) {
        authorities.add(new SimpleGrantedAuthority(roles.get(i)));
      }
      boolean available = authedAccount.isAvailable();

      return new UserDetailsImpl(username, pass, authorities, available);
    } catch (Exception e) {
      throw new UsernameNotFoundException("user not found.", e);
    }
  }
}
