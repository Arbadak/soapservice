package SoapService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/**
 * Класс проверющий наличие файлов с даннми и в случаее их отсуствия выполняющий их генерацию.
 */

@Component
public class CsvFileMaker {
    /**
     * Метод генерирует текстовый файл содержащий последовательность чисел от 0 до IntegerMax, длинной 1GB разделленных запятыми.
     *
     * @param file - имя генерируемого файла
     * @throws IOException
     */

    public void makeFile(File file) throws IOException {
        final int sizeOfFile=1024*1024*1024;
        FileWriter writer = new FileWriter(file);
        Random random = new Random();
        while (file.length() < sizeOfFile) {
            for (int i = 1; i < 10000; i++) {
                writer.append(Integer.toString(random.nextInt(2147483646)));
                writer.append(',');
            }
        }
        writer.flush();
        writer.close();
    }

    /**
     * Метод проверяющий наличей файлов с данными, в случае отсутсвия файлов будет вызван метод генерации файлов
     * В связи с ограничениями наложенными в задании на длинну таблицы filenames, имена для имени файлов используется 3 символа
     */

    @PostConstruct
    public void chekDataFilesIsExist() {

        for (int i = 0; i < 21; i++) {
            File tempFile = new File("d" + i);
            if (!tempFile.exists()) {
                try {
                    makeFile(tempFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
