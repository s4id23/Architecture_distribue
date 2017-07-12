package tp.rest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import tp.model.*;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.http.HTTPException;

import static org.springframework.http.MediaType.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@EnableAutoConfiguration
public class MyServiceController {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(MyServiceController.class, args);
    }

    private Center center;

    public MyServiceController() {
        // Creates a new center
        center = new Center(new LinkedList<>(), new Position(49.30494d, 1.2170602d), "Biotropica");
        // And fill it with some animals
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

    // -----------------------------------------------------------------------------------------------------------------
    // /animals
    @RequestMapping(path = "/animals", method = GET, produces = APPLICATION_JSON_VALUE)
    public Center getAnimals(){
        return center;
    }

    @RequestMapping(path = "/animals", method = POST, produces = APPLICATION_JSON_VALUE)
    public Center addAnimal(@RequestBody Animal animal) throws CageNotFoundException {
        boolean success = this.center.getCages()
                .stream()
                .filter(cage -> cage.getName().equals(animal.getCage()))
                .findFirst()
                .orElseThrow(CageNotFoundException::new)
                .getResidents()
                .add(animal);
        if(success)return this.center;
        throw new IllegalStateException("Failing to add the animal while the input was valid and it's cage was existing is not suppose to happen.");
    }

    // -----------------------------------------------------------------------------------------------------------------
    // /animals/{id}
    @RequestMapping(path = "/animals/{id}", method = GET, produces = APPLICATION_JSON_VALUE)
    public Animal getAnimalById(@PathVariable UUID id) throws AnimalNotFoundException {
        return center.findAnimalById(id);
    }

    // /animals/findByName/{name}
    @RequestMapping(path = "/animals/findByName/{name}", method = GET, produces = APPLICATION_JSON_VALUE)
    public Animal getAnimalById(@PathVariable String name) throws AnimalNotFoundException {
        return center.findAnimalByName(name);
    }

    @RequestMapping(path = "/position/{lag}/{lat}", method = GET, produces = APPLICATION_JSON_VALUE)
    public Source getCentrePosition(@PathVariable String lag, @PathVariable String lat) throws JAXBException {
        try {
            //InputStreamReader stream = new InputStreamReader(new URL("https://graphhopper.com/api/1/route?point=51.131%2C12.414&point=48.224%2C3.867&vehicle=car&type=gpx&locale=FR&key=9301c1c2-7634-4a71-a5ed-63008005f032").openStream());
            InputStreamReader stream = new InputStreamReader(new URL("http://api.geonames.org/findNearby?lat=49.3866&lng=1.0685&username=essamaoual").openStream());
            Source src = new StreamSource(stream);
            return src;
        } catch (MalformedURLException e) {
            throw new HTTPException(404);
        } catch (IOException e) {
            throw new HTTPException(404);
        }
    }

    @RequestMapping(path = "/findAnimalPositionByName/{name}", method = GET, produces = APPLICATION_JSON_VALUE)
    public String getCagePosition(@PathVariable String name) throws JAXBException, AnimalNotFoundException, CageNotFoundException {
        Cage cage = center.CageByName(center.findAnimalByName(name).getCage());
        try {
            String string = IOUtils.toString(new InputStreamReader(new URL("http://api.geonames.org/findNearbyJSON?lat=" + cage.getPosition().getLatitude() + "&lng=" + cage.getPosition().getLongitude() + "&username=m1gil").openStream()));
            return string;
        } catch (MalformedURLException e) {
            throw new HTTPException(404);
        } catch (IOException e) {
            throw new HTTPException(404);
        }
    }

    @RequestMapping(path ="/animals/{animal_id}", method = DELETE, produces = APPLICATION_JSON_VALUE)
    public Center delete_animal_by_id(@PathVariable UUID animal_id){
        {
        	/* recuperer toutes les cages */
            Collection<Cage> cages = this.center.getCages();
            Cage cage;
            Collection<Animal> collection_animals;
            Iterator<Cage> iter_cage = cages.iterator();
            Iterator<Animal> iter_animal;
           /*parcourir la collection des cage*/
            while(iter_cage.hasNext()){
                cage = iter_cage.next();
                collection_animals = cage.getResidents();
                iter_animal = collection_animals.iterator();
            	/*parcourir la listes des animaux par cage jusqu'a trouv�
            	 * l'animal rechercher (� supprimer)*/
                while(iter_animal.hasNext()){
                    Animal animal= iter_animal.next();
                    if(animal.getId().equals(animal_id)){
                        collection_animals.remove(animal);
                    }
                }
            }
        }
        return this.center;
    }

    // /animals/update/{id}
    @RequestMapping(path = "/animals/update/{id}", method = PUT, produces = APPLICATION_JSON_VALUE)
    public Center UpdateAnimalById(@RequestBody Animal animal, @PathVariable UUID id) throws AnimalNotFoundException {

        try {
            Animal refAnimal = this.center.findAnimalById(id);
            refAnimal.setName(animal.getName());
            refAnimal.setSpecies(animal.getSpecies());
            return this.center;
        } catch (AnimalNotFoundException e) {
            throw new HTTPException(404);
        }

    }


    @RequestMapping(path ="/animals/{id_animal}", method = PUT, produces = APPLICATION_JSON_VALUE)
    public Center put_animal_by_id(@RequestBody Animal animal_to_put,@PathVariable UUID id_animal)
    {
        animal_to_put.setId(id_animal);
        Collection<Cage> cages_set = this.center.getCages();
        Cage cage;
        Collection<Animal> collection_animals;
        Iterator<Cage> cages_iterator = cages_set.iterator();
        Iterator<Animal> iter_animals;
             /*parcourir chaque cage*/
        while(cages_iterator.hasNext()){
            cage = cages_iterator.next();
            collection_animals = cage.getResidents();
            iter_animals=collection_animals.iterator();

                /*parcourir les animaux de chaque cage*/
            while(iter_animals.hasNext()){
                Animal animal= iter_animals.next();
                    /*si l'animal recherch� est retrouv� par son Id alors le modifier:*/
                if(animal.getId().equals(animal_to_put.getId())){
                    animal.setCage(animal_to_put.getCage());
                    animal.setName(animal_to_put.getName());
                    animal.setSpecies(animal_to_put.getSpecies());
                }
            }
        }
        return this.center;
    }

}
