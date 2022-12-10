package br.com.aliancamedicor.auth.core;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import br.com.aliancamedicor.auth.domain.Usuario;
import lombok.Getter;

@Getter
public class AuthUser extends User {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2094010971499823455L;
	private String fullName;
	private Long userId;
	private boolean expirado;
	
	public AuthUser(Usuario usuario, Collection<? extends GrantedAuthority> authorities) {
		super(String.valueOf(usuario.getId()), usuario.getSenha(), authorities);
		this.fullName = usuario.getPessoa().getNome();
		this.userId = usuario.getId();
		this.expirado = usuario.isExpirado();
	}

	
}
