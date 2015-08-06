import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;

import java.util.Arrays;


/**
 * Created by jamesagius on 8/5/15.
 */
public class Main {

    public static void main(String...args) throws Exception{
        Client client = new TransportClient()
                .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));

        Employee employee = new Employee();
        employee.setId("2234");
        employee.setAbout("Jim likes the Beatles");
        employee.setAge(42);
        employee.setFirst_name("James");
        employee.setLast_name("Agius");
        employee.setInterests(Arrays.asList("music","guns","guitars"));
        employee.setSsn("123456789");

        ObjectMapper mapper = new ObjectMapper();
        byte[] json = mapper.writeValueAsBytes(employee);

        IndexResponse response = client.prepareIndex("megacorp", "employee")
                .setSource(json).setId(employee.getId())
                .execute()
                .actionGet();

        System.out.println(response);

        SearchResponse search = client.prepareSearch("megacorp").setTypes("employee").execute().actionGet();
        for (SearchHit hit : search.getHits()){
            System.out.println("HIT: " + hit.getSourceAsString());
            Employee e = mapper.readValue(hit.getSourceAsString(), Employee.class);
            System.out.println("Deserialized: " + e);
        }
// on shutdown

        client.close();
    }
}
