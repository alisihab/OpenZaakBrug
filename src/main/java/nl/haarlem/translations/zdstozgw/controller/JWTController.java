package nl.haarlem.translations.zdstozgw.controller;

import nl.haarlem.translations.zdstozgw.translation.zgw.client.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JWTController {

    @Autowired
    private JWTService jwtService;

    @Value("${nl.haarlem.transtlations.zdstozgw.enableJWTEntpoint}")
    private boolean enabled;

    @GetMapping("/jwt")
    public String JWT(){
        String jwt = "This endpoint is dissabled. Enable by setting: nl.haarlem.transtlations.zdstozgw.enableJWTEntpoint = true";
        if(enabled){

            jwt = jwtService.getJWT();
        }
        return jwt;
    }

}
