package spoon.support.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import spoon.member.domain.CurrentUser;

import java.util.Collection;
import java.util.Collections;

public class LoginUser implements UserDetails {

    @Getter
    @Setter
    private CurrentUser user;

    private Collection<GrantedAuthority> roles;

    LoginUser(CurrentUser user) {
        this.user = user;
        roles = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().toString()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getUserid();
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
        return this.user.isEnabled();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoginUser loginUser = (LoginUser) o;

        return user != null ? user.equals(loginUser.user) : loginUser.user == null;
    }

    @Override
    public int hashCode() {
        return user != null ? user.hashCode() : 0;
    }

}
