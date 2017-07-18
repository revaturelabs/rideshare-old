package com.revature.rideshare.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class RideshareAuthenticationToken extends AbstractAuthenticationToken {
	
	private static final long serialVersionUID = 3885076944142687221L;
	
	private String principal;
	private UserDetails details;
	
	public RideshareAuthenticationToken(String principal, UserDetails user, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.details = user;
		this.principal = principal;
	}

	@Override
	public Object getCredentials() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}

	@Override
	public void eraseCredentials() {
		// TODO Auto-generated method stub
		super.eraseCredentials();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return super.getName();
	}

	@Override
	public void setDetails(Object details) {
		// TODO Auto-generated method stub
		super.setDetails(details);
	}
	
	

}
