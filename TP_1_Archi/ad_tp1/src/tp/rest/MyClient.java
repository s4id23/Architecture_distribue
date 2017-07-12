package tp.rest;

import tp.model.Animal;
import tp.model.Cage;
import tp.model.Center;
import tp.model.Position;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.http.HTTPBinding;

import java.util.Map;
import java.util.UUID;

public class MyClient {
    private Service service;
    private JAXBContext jc;

    private static final QName qname = new QName("", "");
    private static final String url = "http://127.0.0.1:8084";

    public MyClient() {
        try {
            jc = JAXBContext.newInstance(Center.class, Cage.class, Animal.class, Position.class);
        } catch (JAXBException je) {
            System.out.println("Cannot create JAXBContext " + je);
        }
    }

    public void add_animal(Animal animal) throws JAXBException {
        service = Service.create(qname);
        service.addPort(qname, HTTPBinding.HTTP_BINDING, url + "/animals");
        Dispatch<Source> dispatcher = service.createDispatch(qname, Source.class, Service.Mode.MESSAGE);
        Map<String, Object> requestContext = dispatcher.getRequestContext();
        requestContext.put(MessageContext.HTTP_REQUEST_METHOD, "POST");
        Source result = dispatcher.invoke(new JAXBSource(jc, animal));
        printSource(result);
    }
    
    // Supprime l’animal identifié par { animal_id }
    public void delete_animal_By_Id(String animal_id) throws JAXBException {
        service = Service.create(qname);
        service.addPort(qname, HTTPBinding.HTTP_BINDING, url + "/animals/"+animal_id);
        Dispatch<Source> dispatcher = service.createDispatch(qname, Source.class, Service.Mode.MESSAGE);
        Map<String, Object> requestContext = dispatcher.getRequestContext();
        requestContext.put(MessageContext.HTTP_REQUEST_METHOD, "DELETE");
        Source result = dispatcher.invoke(new JAXBSource(jc, new Animal()));
        printSource(result);
    }
    
    // Supprime l'ensemble des animaux
    public void delete_animals() throws JAXBException {
        service = Service.create(qname);
        service.addPort(qname, HTTPBinding.HTTP_BINDING, url + "/animals");
        Dispatch<Source> dispatcher = service.createDispatch(qname, Source.class, Service.Mode.MESSAGE);
        Map<String, Object> requestContext = dispatcher.getRequestContext();
        requestContext.put(MessageContext.HTTP_REQUEST_METHOD, "DELETE");
        Source result = dispatcher.invoke(null);
        printSource(result);
    }
   
    // Modifie l’animal identifié par { animal_id }
    public void reset_animal(String animal_id) throws JAXBException {
        service = Service.create(qname);
        service.addPort(qname, HTTPBinding.HTTP_BINDING, url + "/animals/"+animal_id);
        Dispatch<Source> dispatcher = service.createDispatch(qname, Source.class, Service.Mode.MESSAGE);
        Map<String, Object> requestContext = dispatcher.getRequestContext();
        requestContext.put(MessageContext.HTTP_REQUEST_METHOD, "PUT");
        Source result = dispatcher.invoke(new JAXBSource(jc, new Animal("name", "amazon", "species", UUID.fromString(animal_id))));
        printSource(result);
    }
    
    // créer un animal identifié par { animal_id }
    public void create_animal(Animal animal) throws JAXBException {
    	service = Service.create(qname);
        service.addPort(qname, HTTPBinding.HTTP_BINDING, url + "/animals/"+animal.getId());
        Dispatch<Source> dispatcher = service.createDispatch(qname, Source.class, Service.Mode.MESSAGE);
        Map<String, Object> requestContext = dispatcher.getRequestContext();
        requestContext.put(MessageContext.HTTP_REQUEST_METHOD, "POST");
        Source result = dispatcher.invoke(new JAXBSource(jc, animal));
        printSource(result);
    }
    
    public void update_Animals() throws JAXBException {
        service = Service.create(qname);
        service.addPort(qname, HTTPBinding.HTTP_BINDING, url + "/animals");
        Dispatch<Source> dispatcher = service.createDispatch(qname, Source.class, Service.Mode.MESSAGE);
        Map<String, Object> requestContext = dispatcher.getRequestContext();
        requestContext.put(MessageContext.HTTP_REQUEST_METHOD, "PUT");
        Source result = dispatcher.invoke(new JAXBSource(jc,new Animal("test","amazon","test",UUID.randomUUID())));
        printSource(result);
    }
    
    public void printSource(Source s) {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(s, new StreamResult(System.out));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
       

    public static void main(String args[]) throws Exception {
        MyClient client = new MyClient();
        
        //ajout d'un animal
        //client.add_animal(new Animal("Bob2", "usa", "Arapaima gigas2", UUID.randomUUID()));
        
        // supprimé l'animal numero : "le-numero"
        //client.delete_animal_By_Id("le-numero");
        
        //supprimé tous les animaux
        //client.delete_animals();
        
        //modification de tous les animaux 
        //client.update_Animals();
        
        // modifie l'animal numero : "le-numero"
        //client.reset_animal("6d01f462-b4e3-420a-83f1-3ddbaf42ce0d");      
        
        // creer un animal
        //client.create_animal(new Animal("Panda","usa","Mamifere",UUID.randomUUID()));
        
       }
}
