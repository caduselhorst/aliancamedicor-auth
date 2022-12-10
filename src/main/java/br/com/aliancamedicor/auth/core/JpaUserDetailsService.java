package br.com.aliancamedicor.auth.core;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.aliancamedicor.auth.domain.Usuario;
import br.com.aliancamedicor.auth.domain.UsuarioRepository;
import br.com.aliancamedicor.auth.model.AuthorityPermissao;
import br.com.aliancamedicor.auth.model.AuthoritySistema;

@Service
public class JpaUserDetailsService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Usuario usuario = usuarioRepository.findById(Long.parseLong(username))
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
		
		if(usuario.isInativo()) {
			throw new UnauthorizedUserException("Usuário marcado como inativo");
		}
		
		/*
		if(usuario.isExpirado()) {
			throw new UnauthorizedUserException("Usuário com senha expirada. É necessário alterá-la");
		}
		*/

		return new AuthUser(usuario, getAuthorities(usuario));
	}

	private Collection<GrantedAuthority> getAuthorities(Usuario usuario) {
		

		Collection<GrantedAuthority> authorities = usuario.getPerfis().stream()
				.map(perfil -> AuthoritySistema.builder().id(String.valueOf(perfil.getSistema().getId()))
						.nome(perfil.getSistema().getDescricao())
						.permissoes(perfil.getPermissoes().stream()
								.map(perm -> AuthorityPermissao.builder().permissao(perm.getNome()).build())
								.collect(Collectors.toList()))
						.build())
				.flatMap(s -> s.getGrantedRoles().stream())
				.map(granted -> new SimpleGrantedAuthority(granted))
				.collect(Collectors.toList());

		return authorities;
	}

}
