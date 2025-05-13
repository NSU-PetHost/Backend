package NSU.PetHost.AuthService.proto;

import NSU.PetHost.AuthService.security.JWTTypes;
import NSU.PetHost.AuthService.security.JWTUtil;
import NSU.PetHost.proto.JWTServiceGrpc;
import com.auth0.jwt.exceptions.JWTVerificationException;
import NSU.PetHost.proto.CheckJWT;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class JWTServiceImpl extends JWTServiceGrpc.JWTServiceImplBase {

    private final JWTUtil jwtUtil;

    @Override
    public void check(CheckJWT.JWTRequest request,
                      StreamObserver<CheckJWT.JWTResponse> response) {

        System.out.println("Check JWT");
        System.out.println("JWTRequest: " + request);

        try {
            jwtUtil.verifyJWT(request.getToken(), JWTTypes.accessToken);
            response.onNext(CheckJWT.JWTResponse
                    .newBuilder()
                    .setCorrectly(true)
                    .build());
            response.onCompleted();
        } catch (JWTVerificationException e) {
            response.onNext(CheckJWT.JWTResponse
                    .newBuilder()
                    .setCorrectly(false)
                    .build());
            response.onCompleted();
        }
    }

}
