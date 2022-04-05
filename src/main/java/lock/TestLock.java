package lock;

import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.ZkUtil;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/4/5 12:53 上午
 */
public class TestLock {
    ZooKeeper zk;

    @Before
    public void conn() {
        zk = ZkUtil.getZk();
    }

    @After
    public void close() {
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void lock() {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                //每个线程都new一个Watcher
                WatchCallBack watchCallBack = new WatchCallBack();
                String threadName = Thread.currentThread().getName();

                watchCallBack.setZk(zk);
                watchCallBack.setThreadName(threadName);

                //各线程抢锁
                watchCallBack.tryLock();

                //各线程抢完锁后doProcess
                System.out.println(threadName+" doProcess...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //释放锁
                watchCallBack.unLock();
            }).start();
        }

        while(true){

        }
    }

}
