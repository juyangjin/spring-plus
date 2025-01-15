package org.example.expert.domain.auth.dto.response;

import lombok.Getter;

@Getter
public class SigninResponse {

    private final String nickname;
    private final String bearerToken;

    public SigninResponse(String nickName, String bearerToken) {
        this.nickname = nickName;
        this.bearerToken = bearerToken;
    }
}
