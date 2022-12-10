package br.com.aliancamedicor.auth.domain;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tbsistema")
public class Sistema {
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String descricao;
	private String versao;

	@ManyToMany
	@JoinTable(name = "tbpessoa_sistema", 
		joinColumns = @JoinColumn(name = "sistema_id"), 
		inverseJoinColumns = @JoinColumn(name = "pessoa_id"))
	private Set<Pessoa> pessoas;

}
