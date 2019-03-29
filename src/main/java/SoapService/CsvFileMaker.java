package SoapService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class CsvFileMaker {


    public void makeFile(File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        Random random = new Random();
        while (file.length() < 1e+9) {
        //while (file.length() < 10*1024*1024) {
            for (int i = 1; i < 10000; i++) {
                writer.append(Integer.toString(random.nextInt(2147483646)));
                writer.append(',');
            }
        }
        writer.flush();
        writer.close();
    }


    @PostConstruct
    public void chekDataFilesIsExist() {

        for (int i = 0; i < 21; i++) {
            File tempFile = new File("datafile" + i + ".csv");
            if (!tempFile.exists()) {
                try { makeFile(tempFile);}
                catch (IOException e) { e.printStackTrace(); }
                                        }

                                        }
    }
}
