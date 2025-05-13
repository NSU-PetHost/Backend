package NSU.PetHost.ContentService.Controllers;

import NSU.PetHost.ContentService.security.JWTUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestGRPCController {

    private final JWTUtil jwtUtil;

    @GetMapping("/grpc")
    public HttpEntity<String> testGRPC(@RequestParam String token) {

        if (!jwtUtil.checkToken(token)) {
            return new ResponseEntity<>("Token invalid", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

}
