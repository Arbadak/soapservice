package SoapService;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import test.tinkoff.producing.FindNumberRequest;
import test.tinkoff.producing.Result;

/**
 * Класс обрабатывающий входящие запросы
 */

@Endpoint
public class FileFindEndpoint {
    private static final String NAMESPACE_URI = "http://tinkoff.test/producing";

    private FilesPoolRepo filesPoolRepo;

    @Autowired
    public FileFindEndpoint(FilesPoolRepo filesPoolRepo) {
        this.filesPoolRepo = filesPoolRepo;
    }

    /**
     *
     * @param request - XML запрос
     * @return Экземпляр обьекта Responce
     * @throws FileNotFoundException - исключение при отсуствии фала с данными
     * @throws InterruptedException - исключение при ошибке потоков
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "findNumberRequest")
    @ResponsePayload
    public Result findFile(@RequestPayload FindNumberRequest request) throws FileNotFoundException, InterruptedException {
        Result response = new Result();
        List<String> resultList = new ArrayList<>();


        resultList = filesPoolRepo.findFiles(request.getNumber());

        if (resultList.contains("file not found error")) {
            response.setCode("02.Result.Error");
            response.setError("Datafile not found");
            filesPoolRepo.addRecordTobase(request.getNumber(), response);
            return response;
        }
        if (resultList.contains("Search execution error")) {
            response.setCode("02.Result.Error");
            response.setError("Search execution error");
            filesPoolRepo.addRecordTobase(request.getNumber(), response);
        }
        if (resultList.size() > 0 && !resultList.contains("Search execution error") && !resultList.contains("file not found error")) {
            response.getFilenames().addAll(resultList);
            response.setCode("00.Result.OK");
            filesPoolRepo.addRecordTobase(request.getNumber(), response);
        } else {
            response.setCode("01.Result.NotFound");
            filesPoolRepo.addRecordTobase(request.getNumber(), response);
        }
        return response;
    }

}