package SoapService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.stereotype.Component;
import test.tinkoff.producing.Result;

@Component
public class FilesPoolRepo {


    public Result findFileContainNumber(Integer number) throws FileNotFoundException, InterruptedException {
        Result result = new Result();
        result.setFileName(findFiles(number).toString());

        return result;
    }

    private Set<String> findFiles(Integer desiredValue) throws InterruptedException {
        Set<String> filesContainingDesignatedNumberSet = new ConcurrentSkipListSet<>();
        ExecutorService service = Executors.newFixedThreadPool(4);
        CountDownLatch countDownLatch = new CountDownLatch(20);
        //int j = 0;
        //while (j < 21) {
        for (int j=0;j<21;j++){
                 service.submit(new Thread(new FindFileThread(new File("datafile" + j + ".csv"), desiredValue, filesContainingDesignatedNumberSet,countDownLatch)));
        }
        countDownLatch.await();
        System.out.println("Search finished!!!!");
        System.out.println(filesContainingDesignatedNumberSet.toString());

        return filesContainingDesignatedNumberSet;
    }

}

class FindFileThread implements Runnable {
    private File file;
    private int desiredValue;
    private Set filesContainingDesignatedNumberSet;
    private CountDownLatch countDownLatch;

    public FindFileThread(File file, int desiredValue, Set filesContainingDesignatedNumberSet, CountDownLatch cd) {
        this.file = file;
        this.desiredValue = desiredValue;
        this.filesContainingDesignatedNumberSet = filesContainingDesignatedNumberSet;
        this.countDownLatch = cd;
    }

    @Override
    public void run() {
        System.out.println();
        System.out.println("********************* Thread " + file.getName() + " STARTED**************");
        Scanner scn = null;
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(file));
            scn = new Scanner(is);
            scn.useDelimiter(",");
            while (scn.hasNext()) {
                if (scn.nextInt() == desiredValue) {
                    filesContainingDesignatedNumberSet.add(file.getName());
                    System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^Thread: " + file.getName() + " ! VALUE FОUND!!!!!!!!!!!!!");
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            scn.close();
        }
        System.out.println("###################Thread :" + file.getName() + " stopped ##############");
        this.countDownLatch.countDown();
    }



}

