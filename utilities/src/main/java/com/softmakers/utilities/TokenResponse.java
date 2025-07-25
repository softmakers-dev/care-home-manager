package com.softmakers.utilities;

public class TokenResponse {

    private String type;
    private String token;
    private String accessToken;
    private String refreshToken;

    public TokenResponse(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
