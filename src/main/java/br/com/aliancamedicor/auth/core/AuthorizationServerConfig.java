package br.com.aliancamedicor.auth.core;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private PasswordEncoder passworEncoder;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserDetailsService userDetailService;
	@Autowired
	private JwtKeyStoreProperties jwtKeyStoreProperties;

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

		clients.inMemory()
			.withClient("cliente-web")
				.secret(passworEncoder.encode("web123"))
				.authorizedGrantTypes("password", "refresh_token")
				.scopes("WRITE", "READ")
				.accessTokenValiditySeconds(6 * 60 * 60)
				.refreshTokenValiditySeconds(30 * 24 * 60 * 60)
			.and()
			.withClient("foodanalytics")
				.secret(passworEncoder.encode("food123"))
				.authorizedGrantTypes("authorization_code")
				.scopes("WRITE", "READ")
				.redirectUris("http://aplicacao-cliente.org.br")
			.and()
			.withClient("faturamento")
				.secret(passworEncoder.encode("faturamento123"))
				.authorizedGrantTypes("client_credentials")
				.scopes("WRITE", "READ")
			.and()
			.withClient("webadmin")
				.authorizedGrantTypes("implicit")
				.scopes("WRITE", "READ")
				.redirectUris("http://aplicacao-cliente.org.br")
			.and()
			.withClient("checktoken")
				.secret(passworEncoder.encode("check123"));

	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security
			//.checkTokenAccess("isAuthenticated()")
			.checkTokenAccess("permitAll()")
			.tokenKeyAccess("permitAll()")
			.allowFormAuthenticationForClients();
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		
		var enhancerChain = new TokenEnhancerChain();
		enhancerChain.setTokenEnhancers(
				Arrays.asList(new JwtCustomClaimsTokenEnhancer(), jwtAccesTokenConverter()));
		
		endpoints
			.authenticationManager(authenticationManager)
			.userDetailsService(userDetailService)
			.reuseRefreshTokens(false)
			.accessTokenConverter(jwtAccesTokenConverter())
			.tokenEnhancer(enhancerChain)
			.approvalStore(approvalStore(endpoints.getTokenStore()))
			.tokenGranter(tokenGranter(endpoints));
	}
	
	private ApprovalStore approvalStore(TokenStore tokenStore) {
		
		var approvalStore = new TokenApprovalStore();
		approvalStore.setTokenStore(tokenStore);
		
		return approvalStore;
	}

	@Bean
	public JwtAccessTokenConverter jwtAccesTokenConverter() {
		var jwtAccessTokenConverter = new JwtAccessTokenConverter();
		//jwtAccessTokenConverter.setSigningKey("dskfjh9r8werwer2k3jh423kjh2jhk2j3h42kjh23jkh23jkh234jkh234kj2h34");
		
		var jksResource = new ClassPathResource(jwtKeyStoreProperties.getPath());
		var keyStorePass = jwtKeyStoreProperties.getPassword();
		var keyPairAlias = jwtKeyStoreProperties.getKeypairAlias();
		
		var keyStoreKeyFactory = new KeyStoreKeyFactory(jksResource, keyStorePass.toCharArray());
		var keyPair = keyStoreKeyFactory.getKeyPair(keyPairAlias);
		
		jwtAccessTokenConverter.setKeyPair(keyPair);
		
		return jwtAccessTokenConverter;
	}
	
	private TokenGranter tokenGranter(AuthorizationServerEndpointsConfigurer endpoints) {
		var pkceAuthorizationCodeTokenGranter = new PkceAuthorizationCodeTokenGranter(endpoints.getTokenServices(),
				endpoints.getAuthorizationCodeServices(), endpoints.getClientDetailsService(),
				endpoints.getOAuth2RequestFactory());

		var granters = Arrays.asList(pkceAuthorizationCodeTokenGranter, endpoints.getTokenGranter());

		return new CompositeTokenGranter(granters);
	}

}
