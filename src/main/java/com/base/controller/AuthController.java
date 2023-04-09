package com.base.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.base.config.TokenUtil;
import com.base.request.LoginRequest;
import com.base.response.LoginResponse;
import com.base.service.AuthDetailsService;

import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.http.HttpServletRequest;


@RestController
@CrossOrigin
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TokenUtil tokenUtil;

	@Autowired
	private AuthDetailsService userDetailsService;
	
	@RequestMapping(value = "/api/auth/login", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest authenticationRequest) throws Exception {

		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

		
		final UserDetails userDetails = userDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());

		final String token = tokenUtil.generateToken(userDetails);

		String tokenId = tokenUtil.getTokenIdFromToken(token);
		System.out.println("TOEKN ID : " + tokenId);
		return ResponseEntity.ok(new LoginResponse(token));
	}
	
	private void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			System.err.println(e);
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			System.err.println(e);
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
	
	public Map<String, Object> getMapFromIoJsonwebtokenClaims(DefaultClaims claims) {
		Map<String, Object> expectedMap = new HashMap<String, Object>();
		for (Entry<String, Object> entry : claims.entrySet()) {
			expectedMap.put(entry.getKey(), entry.getValue());
		}
		return expectedMap;
	}
	
	@RequestMapping(value = "/api/refresh-token", method = RequestMethod.GET)
	public ResponseEntity<?> createRefreshToken(HttpServletRequest request) throws Exception {
		
		DefaultClaims claims = (DefaultClaims) request.getAttribute("claims");

		Map<String, Object> expectedMap = getMapFromIoJsonwebtokenClaims(claims);
		String token = tokenUtil.doGenerateToken(expectedMap, expectedMap.get("sub").toString());
		return ResponseEntity.ok(new LoginResponse(token));
	}
	
	@GetMapping(value = "/api/details")
	public ResponseEntity<?> getDetails() throws Exception {

		return ResponseEntity.ok("SUCCESS");
	}

}
