package NSU.PetHost.ContentService.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestGRPCController {

    @GetMapping("grpc")
    public String testGRPC() {
        return "Hello World";
    }
    //eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJQZXJzb24gZGV0YWlscyIsInVzZXJJRCI6MTAxLCJuaWNrbmFtZSI6ImJlbDlzaCIsInJvbGUiOiJVU0VSIiwiaXNzIjoic3ByaW5nLWFwcCIsImV4cCI6MTc0NzA1MTQ1MX0.pWzOOwCHVqiSDv27F7xuSMBlAscJl0DpZgAZrJxYstA

}
