package no.nav.familie.ks.oppslag.sts;

class AccessTokenResponse {
    public String access_token;
    public String token_type;
    public Long expires_in;

    public AccessTokenResponse() {
    }

    public AccessTokenResponse(String access_token, String token_type, Long expires_in) {
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
    }

    String getAccess_token() {
        return access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    Long getExpires_in() {
        return expires_in;
    }

    @Override
    public String toString() {
        return String.format("accessToken: %s, tokenType: %s, expiresInn: %s", access_token, token_type, expires_in);
    }
}
