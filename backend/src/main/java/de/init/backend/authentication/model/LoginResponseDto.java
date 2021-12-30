package de.init.backend.authentication.model;

import de.init.backend.authentication.UserPrinciple;

public class LoginResponseDto extends ResponseDto {

	private static final long serialVersionUID = -2814034453110303915L;
	
	private long id;
	private String username;
	private String token;

	public LoginResponseDto(User user, String token) {
		super(200);
		this.id = user.getId();
		this.username = user.getUsername();
		this.token = token;
	}

	public LoginResponseDto(int status, User user, String token) {
		super(status);
		this.id = user.getId();
		this.username = user.getUsername();
		this.token = token;
	}

	public LoginResponseDto(UserPrinciple user, String token) {
		super(200);
		this.id = user.getId();
		this.username = user.getUsername();
		this.token = token;
	}

	public LoginResponseDto(int status, UserPrinciple user, String token) {
		super(status);
		this.id = user.getId();
		this.username = user.getUsername();
		this.token = token;
	}

	public long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getToken() {
		return token;
	}

}
