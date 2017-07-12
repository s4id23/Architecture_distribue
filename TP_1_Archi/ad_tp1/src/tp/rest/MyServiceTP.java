package tp.rest;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.http.HTTPBinding;
import javax.xml.ws.http.HTTPException;

import tp.model.Animal;
import tp.model.AnimalNotFoundException;
import tp.model.Cage;
import tp.model.Center;
import tp.model.Position;

@WebServiceProvider
@ServiceMode(value = Service.Mode.MESSAGE)
public class MyServiceTP implements Provider<Source> {

    public final static String url = "http://127.0.0.1:8084/";

    public static void main(String args[]) {
        Endpoint e = Endpoint.create(HTTPBinding.HTTP_BINDING, new MyServiceTP());

        e.publish(url);
        System.out.println("Service started, listening on " + url);
        // pour arrêter : e.stop();
    }

    private JAXBContext jc;

    @javax.annotation.Resource(type = Object.class)
    protected WebServiceContext wsContext;

    private Center center = new Center(new LinkedList<>(), new Position(49.30494d, 1.2170602d), "Biotropica");

    public MyServiceTP() {
        try {
            jc = JAXBContext.newInstance(Center.class, Cage.class, Animal.class, Position.class);
        } catch (JAXBException je) {
            System.out.println("Exception " + je);
            throw new WebServiceException("Cannot create JAXBContext", je);
        }

        // Fill our center with some animals
        Cage usa = new Cage(
                "usa",
                new Position(49.305d, 1.2157357d),
                25,
                new LinkedList<>(Arrays.asList(
                        new Animal("Tic", "usa", "Chipmunk", UUID.randomUUID()),
                        new Animal("Tac", "usa", "Chipmunk", UUID.randomUUID())
                ))
        );

        Cage amazon = new Cage(
                "amazon",
                new Position(49.305142d, 1.2154067d),
                15,
                new LinkedList<>(Arrays.asList(
                        new Animal("Canine", "amazon", "Piranha", UUID.randomUUID()),
                        new Animal("Incisive", "amazon", "Piranha", UUID.randomUUID()),
                        new Animal("Molaire", "amazon", "Piranha", UUID.randomUUID()),
                        new Animal("De lait", "amazon", "Piranha", UUID.randomUUID())
                ))
        );

        center.getCages().addAll(Arrays.asList(usa, amazon));
    }

    public Source invoke(Source source) {
        MessageContext mc = wsContext.getMessageContext();
        String path = (String) mc.get(MessageContext.PATH_INFO);
        String method = (String) mc.get(MessageContext.HTTP_REQUEST_METHOD);

        // determine the targeted ressource of the call
        try {
            // no target, throw a 404 exception.
            if (path == null) {
                throw new HTTPException(404);
            }
            // "/animals" target - Redirect to the method in charge of managing this sort of call.
            else if (path.startsWith("animals")) {
                String[] path_parts = path.split("/");
                switch (path_parts.length){
                    case 1 :
                        return this.animalsCrud(method, source);
                    case 2 :
                        return this.animalCrud(method, source, path_parts[1]);
                    default:
                        throw new HTTPException(404);
                }
            }
            else if (path.startsWith("find")) {
            	 String[] path_parts = path.split("/");
                 if (path_parts.length == 3){
                	 switch (path_parts[1]){
                     case "byName" :
                    	 return this.animalFindName(method, source, path_parts[2]);
                     case "at" :
                         return this.animalFindAt(method, source, path_parts[2]);
                     case "near" :
                         return this.animalFindNear(method, source, path_parts[2]);
                     default:
                         throw new HTTPException(404);
                 }
                	 
                 }else
                	 throw new HTTPException(503);
                                
            }
            else if ("coffee".equals(path)) {
                throw new HTTPException(418);
            }
            else {
                throw new HTTPException(404);
            }
        } catch (JAXBException e) {
            throw new HTTPException(500);
        }
    }

    /**
     * Method bound to calls on /animals/{something}
     */
    private Source animalCrud(String method, Source source, String animal_id) throws JAXBException {
        if("GET".equals(method)){
            try {
                return new JAXBSource(this.jc, center.findAnimalById(UUID.fromString(animal_id)));
            } catch (AnimalNotFoundException e) {
                throw new HTTPException(404);
            }
        }
        
        // methode post pour ajouter un animal
        else if("POST".equals(method)){
            Animal animal = unmarshalAnimal(source);
            this.center.getCages()
                    .stream()
                    .filter(cage -> cage.getName().equals(animal.getCage()))
                    .findFirst()
                    .orElseThrow(() -> new HTTPException(404))
                    .getResidents()
                    .add(animal);
            return new JAXBSource(this.jc, this.center);
        }
        
        // la methode put pour modifier un animal
        else if("PUT".equals(method)){
        	Animal added_animal = unmarshalAnimal(source);
        	Collection<Cage> cagesList = this.center.getCages();
            Iterator<Cage> cageIt = cagesList.iterator();
            Collection<Animal> animalsList;
                               
            while(cageIt.hasNext()){
            	animalsList = cageIt.next().getResidents();
            	Iterator<Animal> animalIt = animalsList.iterator();

            	while(animalIt.hasNext()){
            		Animal animal= animalIt.next();
            	
            		if(animal.getId().equals(UUID.fromString(animal_id))){
            			animal.setCage(added_animal.getCage());
             			animal.setName(added_animal.getName());
             			animal.setSpecies(added_animal.getSpecies());
            		}
            	}
            }
            return new JAXBSource(this.jc, this.center);
        
        }
        
        // la methode delete pour supprimer un animal     
        else if("DELETE".equals(method)){
        	
        	Collection<Cage> cagesList = this.center.getCages();
            Iterator<Cage> cageIt = cagesList.iterator();
            Collection<Animal> animalsList;
                               
            while(cageIt.hasNext()){
            	animalsList = cageIt.next().getResidents();
            	Iterator<Animal> animalIt = animalsList.iterator();

            	while(animalIt.hasNext()){
            		Animal animal= animalIt.next();
            	
            		if(animal.getId().equals(UUID.fromString(animal_id))){
            			animalsList.remove(animal);
            		}
            	}
            }
            return new JAXBSource(this.jc, this.center);
        
        }
        else
        {
            throw new HTTPException(405);
        }
    }
    
    /**
     * Method bound to calls on /find/ByName
     */
    private Source animalFindName(String method, Source source, String animal_name) throws JAXBException {
        if("GET".equals(method)){
            try {
                return new JAXBSource(this.jc, center.Animal_By_Name(animal_name));
            } catch (AnimalNotFoundException e) {
                throw new HTTPException(404);
            }
        }
        
        else{
            throw new HTTPException(405);
        }
    }
    
    /**
     * Method bound to calls on /find/At
     */
    private Source animalFindAt(String method, Source source, String animal_name) throws JAXBException {
        if("GET".equals(method)){
            try {
                return new JAXBSource(this.jc, center.Animal_By_Position(animal_name));
            } catch (AnimalNotFoundException e) {
                throw new HTTPException(404);
            }
        }
        else{
            throw new HTTPException(405);
        }
    }
    
    /**
     * Method bound to calls on /find/near
     */
    private Source animalFindNear(String method, Source source, String animal_name) throws JAXBException {
        if("GET".equals(method)){
            try {
                return new JAXBSource(this.jc, center.Animals_Near_Position(animal_name));
            } catch (AnimalNotFoundException e) {
                throw new HTTPException(404);
            }
        }
        else{
            throw new HTTPException(405);
        }
    }


    /**
     * Method bound to calls on /animals
     */
    private Source animalsCrud(String method, Source source) throws JAXBException {
        if("GET".equals(method)){
            return new JAXBSource(this.jc, this.center);
        }
        
     // methode delete pour supprimer tous les animaux
        else if("DELETE".equals(method)){
            Collection<Cage> col = this.center.getCages();
            Iterator<Cage> it = col.iterator();
            while(it.hasNext()){
                Cage cage = it.next();
                cage.getResidents().removeAll(cage.getResidents());
            }
            return new JAXBSource(this.jc, this.center);
        } 
        
     // methode delete pour creer des animaux
        else if("POST".equals(method)){
            Animal animal = unmarshalAnimal(source);
            this.center.getCages()
                    .stream()
                    .filter(cage -> cage.getName().equals(animal.getCage()))
                    .findFirst()
                    .orElseThrow(() -> new HTTPException(404))
                    .getResidents()
                    .add(animal);
            return new JAXBSource(this.jc, this.center);
        }
        
     // methode put pour creer des animaux
        else if("PUT".equals(method)){
            Animal animal = unmarshalAnimal(source);
            Collection<Cage> col = this.center.getCages();
            Iterator<Cage> it = col.iterator();
            while(it.hasNext()){
                Cage cage = it.next();
                if(cage.getName().equals(animal.getCage())){
                    for(Animal a: cage.getResidents()){
                        a.setName(animal.getName());
                        a.setCage(animal.getCage());
                        a.setSpecies(animal.getSpecies());
                    }
                }
            }
            return new JAXBSource(this.jc, this.center);
        }
        
        else{
            throw new HTTPException(405);
        }
    }

    private Animal unmarshalAnimal(Source source) throws JAXBException {
        return (Animal) this.jc.createUnmarshaller().unmarshal(source);
    }
}
