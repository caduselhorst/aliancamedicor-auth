package br.com.aliancamedicor.auth.model;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthoritySistema {
	private String id;
	private String nome;
	private List<AuthorityPermissao> permissoes;
	
	public List<String> getGrantedRoles() {
		return permissoes.stream()
				.map(p -> id + "-" + p.getPermissao()).collect(Collectors.toList());
	}
}