package com.redditclone.service;

import static com.redditclone.util.Constants.ACTIVATION_EMAIL;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.redditclone.dto.AuthenticationResponse;
import com.redditclone.dto.LoginRequest;
import com.redditclone.dto.RefreshTokenRequest;
import com.redditclone.dto.RegisterRequest;
import com.redditclone.exception.SpringRedditException;
import com.redditclone.modelentity.NotificationEmail;
import com.redditclone.modelentity.RefreshToken;
import com.redditclone.modelentity.User;
import com.redditclone.modelentity.VerificationToken;
import com.redditclone.repository.UserRepository;
import com.redditclone.repository.VerificationTokenRepository;
import com.redditclone.security.JwtProvider;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final MailService mailService;
	private final VerificationTokenRepository verificationTokenRepository;
	private final MailContentBuilder mailContentBuilder;
	private final AuthenticationManager authenticationManager;
	private final JwtProvider jwtProvider;
	private final RefreshTokenService refreshTokenService;

	@Transactional
	public void signup(RegisterRequest registerRequest) {
		User user = new User();
		user.setUsername(registerRequest.getUsername());
		user.setEmail(registerRequest.getEmail());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setCreated(Instant.now());
		user.setEnabled(false);

		userRepository.save(user);

		String token = generateVerificationToken(user);
		String message = "Thank you for signing up to Spring Reddit, please click on the below url to activate your account : "
				+ ACTIVATION_EMAIL + "/" + token + " ";
		mailService.sendMail(new NotificationEmail("Please Activate your account", user.getEmail(), message));

	}

	private String generateVerificationToken(User user) {
		String token = UUID.randomUUID().toString();
		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken(token);
		verificationToken.setUser(user);
		verificationTokenRepository.save(verificationToken);
		return token;
	}

	public void verifyAccount(String token) {

		Optional<VerificationToken> verificationTokenOptional = verificationTokenRepository.findByToken(token);
		verificationTokenOptional.orElseThrow(() -> new SpringRedditException("Invalid Token"));
		fetchUserAndEnable(verificationTokenOptional.get());

	}

	@Transactional
	private void fetchUserAndEnable(VerificationToken verificationToken) {

		String username = verificationToken.getUser().getUsername();
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new SpringRedditException("User Not Found with id - " + username));
		user.setEnabled(true);
		userRepository.save(user);

	}

	public AuthenticationResponse login(LoginRequest loginRequest) {

		Authentication authenticate = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authenticate);
		String token = jwtProvider.generateToken(authenticate);

		return AuthenticationResponse.builder().authenticationToken(token)
				.refreshToken(refreshTokenService.generateRefreshToken(getCurrentUser()).getToken())
				.expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
				.username(loginRequest.getUsername()).build();
	}

	@Transactional(readOnly = true)
	public User getCurrentUser() {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		return userRepository.findByUsername(principal.getUsername())
				.orElseThrow(() -> new SpringRedditException("User name not found - " + principal.getUsername()));
	}

	public AuthenticationResponse refreshToken(@Valid RefreshTokenRequest refreshTokenRequest) {

		RefreshToken refToken = refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
		System.out.println("Validating Username with refresh token");

		if (refToken.getUser().getUsername().equals(refreshTokenRequest.getUsername())) {

			String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
			AuthenticationResponse authResponse = AuthenticationResponse.builder().authenticationToken(token)
					.refreshToken(refreshTokenRequest.getRefreshToken())
					.expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
					.username(refreshTokenRequest.getUsername()).build();
			return authResponse;
		}

		else {
			throw new SpringRedditException(
					"Refresh Token does not match the provided username - " + refreshTokenRequest.getUsername());

		}

	}

	public boolean isLoggedIn() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
	}

}
