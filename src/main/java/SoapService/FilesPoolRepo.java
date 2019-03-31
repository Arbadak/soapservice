package SoapService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import test.tinkoff.producing.Result;

/**
 * Класс работы с файлами данных
 */
@Component
public class FilesPoolRepo {

    final RecordJPARepo recordJPARepo;

   protected org.slf4j.Logger logger = LoggerFactory.getLogger(FilesPoolRepo.class);


    @Autowired
    public FilesPoolRepo(RecordJPARepo recordJPARepo) {
        this.recordJPARepo = recordJPARepo;
    }

    /**
     * Метод добавляющий запись в БД, создается экземляр сущности со значениями полей обьекта Result
     *
     * !!!!!!!!! согласно ТЗ производится нарушение 1 нормальной формы для сохранения списка файлов содержащих искомое
     * число. Список файлов приводится к строке с запятой в качестве разделителя !!!!!
     *
     * @param number - искомое число из XML запроса
     * @param responce сгенерированный класс ответа
     */
    public void addRecordTobase(Integer number, Result responce) {
        Record record = new Record(responce.getCode(), number, responce.getError());
        String strList = responce.getFilenames().toString();
        strList = strList.replace("[", "")
                .replace("]", "")
                .replace(" ", "");
        record.setFilenames(strList);
        recordJPARepo.save(record);
    }

    /**
     * Метод запускающий потоки поиска искомого числа в файлах данных, запущенные потоки в случае успеха добавляют имена обрабатываемых файлов
     * в SET, в случае возникновения исключения SET очищается и в него записываетя ошибка приведшая к исключению
     * потоки обьеденяюся в пул, завершение потоков контролируется с помощью CountdownLatch
     * @param number - искомое значение
     * @return ArrayList сипско файлов содежащих искомое значение
     */
    public List<String> findFiles(Integer number) {
        final int simultaneousThreads = 4;
        Set<String> filesContainingDesignatedNumberSet = new ConcurrentSkipListSet<>();
        ExecutorService service = Executors.newFixedThreadPool(simultaneousThreads);
        CountDownLatch countDownLatch = new CountDownLatch(20);
        for (int j = 0; j < 21; j++) {
            service.submit(new Thread(new FindFileThread(new File("d" + j), number, filesContainingDesignatedNumberSet, countDownLatch)));
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.warn("Search execution error");
            logger.warn(e.getCause().getMessage());
            filesContainingDesignatedNumberSet.clear();
            filesContainingDesignatedNumberSet.add("Search execution error");
        }
        logger.info("~~~~~~~~~Search finished!~~~~~~~~~~~~~");
        logger.info(filesContainingDesignatedNumberSet.toString());
        return new ArrayList<String>(filesContainingDesignatedNumberSet);
    }
}

/**
 * Класс - поток производящий поиск по файлу
 * в связи с откствуем разбиения файла по строкам и большим обьемом файла
 * для поиска используется класс Scaner с делимитером ","
 */

class FindFileThread implements Runnable {
    private File file;
    private int desiredValue;
    private Set filesContainingDesignatedNumberSet;
    private CountDownLatch countDownLatch;

  protected org.slf4j.Logger logger = LoggerFactory.getLogger(FindFileThread.class);

    public FindFileThread(File file, int desiredValue, Set filesContainingDesignatedNumberSet, CountDownLatch cd) {
        this.file = file;
        this.desiredValue = desiredValue;
        this.filesContainingDesignatedNumberSet = filesContainingDesignatedNumberSet;
        this.countDownLatch = cd;
    }

    /**
     * Метод run() потока.
     */
    @Override
    public void run() {
        logger.info("^^^^^^^^^^^^^^^^^^^^^^ Thread " + file.getName() + " STARTED^^^^^^^^^^^^^^^");
        Scanner scn = null;
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(file));
            scn = new Scanner(is);
            scn.useDelimiter(",");
            while (scn.hasNext()) {
                if (scn.nextInt() == desiredValue) {
                    filesContainingDesignatedNumberSet.add(file.getName());
                    logger.info("+++++++++++++Thread: " + file.getName() + " ! VALUE FОUND!+++++++++++++");
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            filesContainingDesignatedNumberSet.add("file not found error");
            logger.info("!!!!!!!!!!!!!! Thread :" + file.getName() + " file not found  !!!!!!!!!!!!!");
            logger.warn("not found INFO");
            logger.warn(e.getCause().getMessage());
        } finally {
            scn.close();
        }
        logger.info("###################Thread :" + file.getName() + " stopped ##############");
        this.countDownLatch.countDown();
    }
}

