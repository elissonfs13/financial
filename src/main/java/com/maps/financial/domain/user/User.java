package com.maps.financial.domain.user;

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.maps.financial.domain.account.Account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter
@AllArgsConstructor @NoArgsConstructor @Builder
public class User implements UserDetails {
	
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@Basic(optional = false)
    @Column(name = "username", nullable = false)
    private String username;
	
	@JsonIgnore
	@Basic(optional = false)
	@Column(name = "password", nullable = false)
    private String password;
	
	@Enumerated(EnumType.STRING)
    @Basic(optional = false)
    @Column(name = "job_function", nullable = false)
    private JobFunction jobFunction;
	
	@Setter
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
	private Account account;
	
	@Transient
    private Collection<? extends GrantedAuthority> authorities;
	
	public Long getAccountId() {
		return this.account.getId();
	}
	
	public boolean isAdmin() {
		return JobFunction.ADMIN.equals(this.jobFunction);
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
		return true;
	}
	
	

}
