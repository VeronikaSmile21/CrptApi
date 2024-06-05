package org.example;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class CrptApiTest {

    @Test
    public void test10RequestPerSecond() {
        // 10 request per second
        int threads_count = 10;
        int max_requests = 100;

        long started = System.currentTimeMillis();
        CrptApi crptApi = new CrptApi(TimeUnit.SECONDS, threads_count);
        CrptApi.Document document = new CrptApi.Document(
                "1234567",
                "doc-id-12313",
                "draft",
                true,
                "456456456"
        );
        String signature = "";
        for ( int i = 0; i < max_requests; i++ ) {
            crptApi.createDocument(document, signature);
        }

        long ended = System.currentTimeMillis();

        long totalRunTime = ended - started;
        System.out.println("Total run time: " + totalRunTime);
        Assert.assertTrue( totalRunTime > 9000);
        Assert.assertTrue( totalRunTime < 12000);
    }
}
