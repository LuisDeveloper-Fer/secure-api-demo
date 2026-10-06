package dev.portfolio.api;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.*;
@RestController @RequestMapping("/api") public class SecureController {
 @GetMapping("/me") public Map<String,Object> me(@AuthenticationPrincipal Jwt jwt){return Map.of("subject",jwt.getSubject(),"roles",Optional.ofNullable(jwt.getClaimAsStringList("roles")).orElse(List.of()),"issuer",jwt.getIssuer().toString());}
 @GetMapping("/accounts/{owner}") public Map<String,Object> account(@PathVariable String owner,@AuthenticationPrincipal Jwt jwt){
  // Ownership is independent of the role: even admins cannot read a different owner's account here.
  if(!owner.equals(jwt.getSubject()))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
  return Map.of("owner",owner,"currency","PEN","balance","125.00","fictional",true);
 }
 @GetMapping("/reports") public Map<String,Object> reports(){return Map.of("report","Fictional daily operations","transactions",12,"fictional",true);}
}
