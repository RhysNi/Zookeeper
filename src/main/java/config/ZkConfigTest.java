package config;


import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.ZkUtil;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/4/4 3:40 下午
 */
public class ZkConfigTest {
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
    public void getConf() {
        WatchCallBack watchCallBack = new WatchCallBack();
        MyConf myConf = new MyConf();

        watchCallBack.setZk(zk);
        watchCallBack.setConf(myConf);
        watchCallBack.aWait();


        while (true) {
            if ("".equals(myConf.getConf())) {
                System.out.println("Lost connection ...");
                watchCallBack.aWait();
            } else {
                System.out.println(myConf.getConf());
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
