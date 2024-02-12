package br.com.erudio.controllers;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.erudio.data.vo.v1.security.AccountCredentialsVo;
import br.com.erudio.services.AuthServices;
import br.com.erudio.util.MediaType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Authentication Endpoint")
@RequestMapping("/auth")
public class AuthController {

	Logger logger = Logger.getLogger(AuthController.class.getName());
	
	
	@Autowired
	AuthServices authServices;
	
	@SuppressWarnings("rawtypes")
	@Operation(summary = "Authenticates a user and returns a token")
	@PostMapping(value = "/signin", consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML}, produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
	public ResponseEntity signin(@RequestBody AccountCredentialsVo data) {
		
		logger.info("Entrou no controller");
		
		if (checkIfParamsIsNotNull(data)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");
		
		var token = authServices.signin(data);
		
		if(token == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");
		
		logger.info("saiu do controller");
		
		return token;

	}
	
	@SuppressWarnings("rawtypes")
	@Operation(summary = "Refresh token for authenticated user and returns a token")
	@PutMapping(value = "/refresh/{username}", consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML}, produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
	public ResponseEntity refreshToken(@PathVariable ("username") String username, @RequestHeader("Authorization") String refreshToken) {
		
		logger.info("Entrou no controller");
		
		if (checkIfParamsNotNull(username, refreshToken)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");
		
		var token = authServices.refreshToken(username,refreshToken);
		
		if(token == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");
		
		logger.info("saiu do controller");
		
		return token;
		
	}

	private boolean checkIfParamsNotNull(String username, String refreshToken) {
		return refreshToken == null || refreshToken.isBlank() 
				|| username == null || username.isBlank();
	}
	
	private boolean checkIfParamsIsNotNull(AccountCredentialsVo data) {
		return data == null || data.getUsername() == null || data.getUsername().isBlank()
				|| data.getPassword() == null || data.getPassword().isBlank();
	}
}
