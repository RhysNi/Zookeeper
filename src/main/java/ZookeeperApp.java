
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/3/30 11:23 下午
 */
public class ZookeeperApp {
    //ZK有session概念，没有连接池概念
    //watch:观察，回调
    public static void main(String[] args) throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper("120.25.253.51:2181,1.15.184.231:2181,101.133.157.40:2181", 3000, event -> {
            //事件状态
            Watcher.Event.KeeperState state = event.getState();
            //事件类型
            Watcher.Event.EventType type = event.getType();
            //事件路径
            String path = event.getPath();
            System.out.println("new ZK Watch: " + event.toString());

            switch (state) {
                case Disconnected:
                    break;
                case SyncConnected:
                    System.out.println("CONNECTED");
                    countDownLatch.countDown();
                    break;
                case AuthFailed:
                    break;
                case ConnectedReadOnly:
                    break;
                case SaslAuthenticated:
                    break;
                case Expired:
                    break;
                case Closed:
                    break;
            }

            switch (type) {
                case None:
                    break;
                case NodeCreated:
                    break;
                case NodeDeleted:
                    break;
                case NodeDataChanged:
                    break;
                case NodeChildrenChanged:
                    break;
                case DataWatchRemoved:
                    break;
                case ChildWatchRemoved:
                    break;
                case PersistentWatchRemoved:
                    break;
            }
        }, null);
        countDownLatch.await();
        ZooKeeper.States state = zooKeeper.getState();
        switch (state) {
            case CONNECTING:
                System.out.println("正在连接中...");
                break;
            case ASSOCIATING:
                break;
            case CONNECTED:
                System.out.println("连接成功~");
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                break;
            case AUTH_FAILED:
                break;
            case NOT_CONNECTED:
                break;
        }

        String s = zooKeeper.create("/rhys", "test".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        Stat stat = new Stat();
        byte[] data = zooKeeper.getData("/rhys", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("getData Watch: " + event.toString());
                try {
                    //true default Watch 被重新注册
                    zooKeeper.getData("/rhys", this, stat);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, stat);

        System.out.println(new String(data));

        Stat stat1 = zooKeeper.setData("/rhys", "newTest1".getBytes(), 0);
        Stat stat2 = zooKeeper.setData("/rhys", "newTest2".getBytes(), stat1.getVersion());

        //回调
        System.out.println("== async == start ==");
        zooKeeper.getData("/rhys", false, (rc, path, ctx, data1, stat3) -> {
            System.out.println("== async == call == back ==");
            System.out.println(ctx.toString());
            System.out.println(new String(data1));
        },"aaa");
        System.out.println("== async == over ==");


        Thread.sleep(22222222);
    }
}
