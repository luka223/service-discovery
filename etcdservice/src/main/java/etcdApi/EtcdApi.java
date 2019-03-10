package etcdApi;

import io.etcd.jetcd.*;
import io.etcd.jetcd.api.LeaseGrantRequest;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.lease.LeaseGrantResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.lease.LeaseRevokeResponse;
import io.etcd.jetcd.lease.LeaseTimeToLiveResponse;
import io.etcd.jetcd.options.LeaseOption;
import io.etcd.jetcd.options.PutOption;
import io.grpc.stub.StreamObserver;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class EtcdApi
{
    private final String endpoint = "http://192.168.0.28:2379";
    private final int keepAliveTime = 20; // seconds to send keepAlive request

    private KV kvClient;
    private Lease leaseClient;

    private String serviceName;
    private String hostname;
    private int port;

    public EtcdApi(String serviceName, String hostname, int port)
    {
        this.serviceName = serviceName;
        this.hostname = hostname;
        this.port = port;

        Client client = Client.builder().endpoints(endpoint).build();
        kvClient = client.getKVClient();
        leaseClient = client.getLeaseClient();
    }

    public List<String> getValues(String key)
    {
        ByteSequence byteKey = ByteSequence.from(key, Charset.defaultCharset());

        try
        {
            GetResponse res = kvClient.get(byteKey).get();

            List<String> list = new ArrayList<>();

            for (KeyValue kv : res.getKvs())
            {
                list.add(kv.getValue().toString(Charset.defaultCharset()));
            }

            return list;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private long grantLease()
    {
        try
        {
            LeaseGrantResponse response = leaseClient.grant(20).get();
            return response.getID();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    private boolean put(String key, String value, long leaseId)
    {
        ByteSequence byteKey = ByteSequence.from(key, Charset.defaultCharset());
        ByteSequence byteValue = ByteSequence.from(value, Charset.defaultCharset());

        try
        {
            PutOption putOption = PutOption.newBuilder().withLeaseId(leaseId).build();
            PutResponse p = kvClient.put(byteKey, byteValue, putOption).get();
            return true;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private void sendKeepAliveRequest()
    {
        long leaseId = grantLease();

        if (leaseId == -1)
            System.err.println("Lease not granted");
        else
            put(serviceName, hostname + ":" + port, leaseId);
    }

    public void keepAlive()
    {
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                sendKeepAliveRequest();
            }
        }, 0, keepAliveTime * 1000 - 3000); // 0 - delay, refreshTime - period
    }


    public Map<String, String> getAllKeyValues()
    {
        return null;
    }
}
