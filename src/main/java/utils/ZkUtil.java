package utils;

import config.DefaultWatch;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/4/4 3:54 下午
 */
public class ZkUtil {
    private static ZooKeeper zk;

    public static String ADDRESS = "120.25.253.51:2181,1.15.184.231:2181,101.133.157.40:2181/rzkConfig";

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private static DefaultWatch defaultWatch = new DefaultWatch();

    public static ZooKeeper getZk() {
        try {
            zk = new ZooKeeper(ADDRESS, 1000, defaultWatch);
            defaultWatch.setCountDownLatch(countDownLatch);
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return zk;
    }
}
