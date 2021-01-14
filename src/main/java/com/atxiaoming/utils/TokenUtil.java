package com.atxiaoming.utils;

import com.atxiaoming.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenUtil {

    private static final String TOKEN_SECRET = "privateKey";

    public String createToken(User user) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            // 设置头部信息
            Map<String, Object> header = new HashMap<>(2);
            header.put("Type", "Jwt");
            header.put("alg", "HS256");

            return JWT.create()
                    .withHeader(header)
                    .withClaim("userId", user.getId())
//                    .withClaim("updateAt", user.getUpdateAt())
                    .sign(algorithm);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Integer getIdFromToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            Integer userId = jwt.getClaim("userId").asInt();
            return userId;
        } catch (Exception e){
            return 0;
        }
    }

//    public Timestamp getudateAtFromToken(String token){
//        try {
//            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
//            JWTVerifier verifier = JWT.require(algorithm).build();
//            DecodedJWT jwt = verifier.verify(token);
//            Timestamp updateAt = jwt.getClaim("updateAt").as(Timestamp);
//            return updateAt;
//        } catch (Exception e){
//            return null;
//        }
//    }
}
