package NSU.PetHost.ContentService.proto;

import io.grpc.stub.StreamObserver;

public class JWTServiceImpl extends JWTServiceGrpc.JWTServiceImplBase {

    @Override
    public void check (CheckJWT.JWTRequest request,
                       StreamObserver<CheckJWT.JWTResponse> response) {

        //TODO: в другой сервис

    }

}
