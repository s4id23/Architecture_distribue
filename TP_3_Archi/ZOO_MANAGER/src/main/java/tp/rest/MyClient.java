package tp.rest;

import jdk.management.resource.ResourceId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import tp.model.Animal;
import tp.model.Center;

import javax.xml.bind.JAXBException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class MyClient {

    private static final String url = "http://localhost:8080";
    private static final String HTTP_ERROR_CODE = "HTTP_ERROR_CODE";

    public static void main(String args[]) throws Exception {
        MyClient client = new MyClient();
        client.get_animals().ifPresent(center -> System.out.println(center.toString()));
        client.add_animal(new Animal("Bob", "amazon", "Arapaima gigas", UUID.randomUUID())).ifPresent(center -> System.out.println(center.toString()));
    }

    private final RestTemplate restTemplate;

    public MyClient() {
        restTemplate = new RestTemplate();
    }

    public Optional<Center> get_animals(){
        return processResponse(
                restTemplate.getForEntity(url + "/animals", Center.class),
                () -> "Got an HTTP "+ HTTP_ERROR_CODE + " exception while listing the animals"
        );
    }

    public Optional<Center> add_animal(Animal animal) throws JAXBException {
        return processResponse(
                restTemplate.postForEntity(url + "/animals", animal, Center.class),
                () -> String.format("Got an HTTP "+ HTTP_ERROR_CODE + " exception while sending %s[%s] to the remote center", animal.getName(), animal.getSpecies())
        );
    }

    public <T> Optional<T> processResponse(final ResponseEntity<T> response, final Supplier<String> error_message_supplier){
        if(response.getStatusCode().is2xxSuccessful()){
            return Optional.of(response.getBody());
        }
        else{
            System.out.print(error_message_supplier.get().replace(HTTP_ERROR_CODE, String.valueOf(response.getStatusCodeValue())));
            return Optional.empty();
        }
    }
}
