package SoapService;

import java.io.FileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import test.tinkoff.producing.FindNumberRequest;
import test.tinkoff.producing.FindNumberResponse;
import test.tinkoff.producing.Result;

@Endpoint
public class FileFindEndpoint {
    private static final String NAMESPACE_URI = "http://tinkoff.test/producing";

    private FilesPoolRepo filesPoolRepo;


    @Autowired
    public FileFindEndpoint(FilesPoolRepo filesPoolRepo) {
        this.filesPoolRepo = filesPoolRepo;
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "findNumberRequest")
    @ResponsePayload
    public FindNumberResponse findFile(@RequestPayload FindNumberRequest request) throws FileNotFoundException, InterruptedException {
        FindNumberResponse response = new FindNumberResponse();
        Result result=filesPoolRepo.findFileContainNumber(request.getNumber());
        response.setResult(result);

        return response;
    }
}