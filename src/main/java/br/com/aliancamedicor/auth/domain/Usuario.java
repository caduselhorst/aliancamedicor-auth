package br.com.aliancamedicor.auth.domain;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tbusuario")
public class Usuario {
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String senha;
	@OneToOne
	private Pessoa pessoa;
	@OneToMany
	@JoinTable(name = "tbusuario_perfil",
			joinColumns = @JoinColumn(name = "perfil_id"),
			inverseJoinColumns = @JoinColumn(name = "usuario_id")
	)
	private Set<Perfil> perfis;
	
	private boolean inativo;
	private boolean expirado;

}
