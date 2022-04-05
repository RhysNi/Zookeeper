package lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/4/5 12:18 下午
 */
public class WatchCallBack implements Watcher, AsyncCallback.StringCallback, AsyncCallback.Children2Callback, AsyncCallback.StatCallback {
    ZooKeeper zk;
    String threadName;
    CountDownLatch cdl = new CountDownLatch(1);
    String pathName;

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                zk.getChildren("/", false, this, "sda");
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
    }

    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        if (name != null) {
            System.out.println(threadName + "node：" + name);
            pathName = name;
            zk.getChildren("/", false, this, "sda");
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
        // System.out.println(threadName + "locks..");
        // for (String child : children) {
        //     System.out.println(child);
        // }

        Collections.sort(children);
        int i = children.indexOf(pathName.substring(1));
        if (i == 0) {
            System.out.println(threadName + " is first");
            cdl.countDown();
        } else {
            zk.exists("/" + children.get(i - 1), this, this, "sda");
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {

    }

    public void tryLock() {
        try {
            zk.create("/lock", threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, this, "abc");
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void unLock() {
        try {
            zk.delete(pathName, -1);
            System.out.println(threadName+" over work..");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }


}
