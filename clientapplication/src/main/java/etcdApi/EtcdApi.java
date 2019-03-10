package etcdApi;

import io.etcd.jetcd.*;
import io.etcd.jetcd.api.LeaseGrantRequest;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.lease.LeaseGrantResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.lease.LeaseRevokeResponse;
import io.etcd.jetcd.lease.LeaseTimeToLiveResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.LeaseOption;
import io.etcd.jetcd.options.PutOption;
import io.grpc.stub.StreamObserver;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class EtcdApi
{
    private final String endpoint = "http://192.168.0.28:2379";
    private KV kvClient;

    public EtcdApi()
    {
        Client client = Client.builder().endpoints(endpoint).build();
        kvClient = client.getKVClient();
    }

    public String getValue(String key)
    {
        ByteSequence byteKey = ByteSequence.from(key, Charset.defaultCharset());

        try
        {
            GetResponse res = kvClient.get(byteKey).get();
            String value = res.getKvs().get(0).getValue().toString(Charset.defaultCharset());

            return value;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }


    public List<String> getAvailableServices()
    {
        List<String> list = new LinkedList<>();

        try
        {
            ByteSequence key = ByteSequence.from("\0", Charset.defaultCharset());

            GetOption option = GetOption.newBuilder()
                    .withSortField(GetOption.SortTarget.KEY)
                    .withSortOrder(GetOption.SortOrder.ASCEND)
                    .withRange(key)
                    .build();

            GetResponse response = kvClient.get(key, option).get();
            for (KeyValue kv : response.getKvs())
                list.add(kv.getKey().toString(Charset.defaultCharset()));

            return list;
        }
        catch (Exception e)
        {
            System.err.println("Error while retrieving available services: " + e.getMessage());
            return null;
        }
    }
}
