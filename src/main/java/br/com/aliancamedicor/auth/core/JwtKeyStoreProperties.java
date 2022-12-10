package br.com.aliancamedicor.auth.core;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Component
@ConfigurationProperties("aliancamedicor.jwt.keystore")
public class JwtKeyStoreProperties {
	
	private String path;
	private String password;
	private String keypairAlias;
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getKeypairAlias() {
		return keypairAlias;
	}
	public void setKeypairAlias(String keypairAlias) {
		this.keypairAlias = keypairAlias;
	}

}
