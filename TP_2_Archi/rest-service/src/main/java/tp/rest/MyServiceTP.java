package tp.rest;

import tp.model.*;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.ws.http.HTTPException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/zoo-manager/")
public class MyServiceTP {

    private Center center = new Center(new LinkedList<>(), new Position(49.30494d, 1.2170602d), "Biotropica");

    public MyServiceTP() {
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

    /**
     * GET method bound to calls on /animals/{something}
     */
    @GET
    @Path("/animals/{id}/")
    @Produces("application/xml")
    public Animal getAnimal(@PathParam("id") String animal_id) throws JAXBException {
        try {
            return center.findAnimalById(UUID.fromString(animal_id));
        } catch (AnimalNotFoundException e) {
            throw new HTTPException(404);
        }
    }

    /**
     * GET method bound to calls on /animals/{something}
     */
    @GET
    @Path("/find/byName/{name}/")
    @Produces("application/xml")
    public Animal getAnimalName(@PathParam("name") String animal_name) throws JAXBException {
        try {
            return center.Animal_By_Name(animal_name);
        } catch (AnimalNotFoundException e) {
            throw new HTTPException(404);
        }
    }

    @GET
    @Path("/find/near/{position}")
    @Produces("application/xml")
    public Cage animalFindNear(@PathParam("position")String position) throws JAXBException {
        try {
            return this.center.Animals_Near_Position(position);
        } catch (AnimalNotFoundException e) {
            throw new HTTPException(404);
        }
    }

    @GET
    @Path("/find/at/{position}")
    @Produces("application/xml")
    public Animal animalFindAt(@PathParam("position")String position) throws JAXBException {
        try {
            return this.center.Animal_By_Position(position);
        } catch (AnimalNotFoundException e) {
            throw new HTTPException(404);
        }
    }
    /**
     * GET method bound to calls on /animals
     */
    @GET
    @Path("/animals/")
    @Produces("application/xml")
    public Center getAnimals(){
        return this.center;
    }

    /**
     * POST method bound to calls on /animals
     */
    @POST
    @Path("/animals/")
    @Consumes({"application/xml", "application/json" })
    public Center postAnimals(Animal animal) throws JAXBException {
        this.center.getCages()
                .stream()
                .filter(cage -> cage.getName().equals(animal.getCage()))
                .findFirst()
                .orElseThrow(() -> new HTTPException(404))
                .getResidents()
                .add(animal);
        return this.center;
    }
    /**
     * POST method bound to calls on /animals/id
     */
    @POST
    @Path("/animals/{id}")
    @Consumes({"application/xml", "application/json" })
    public Center postAnimal(Animal animal,@PathParam("id")String animal_id) throws JAXBException {
        animal.setId(UUID.fromString(animal_id));
        this.center.getCages()
                .stream()
                .filter(cage -> cage.getName().equals(animal.getCage()))
                .findFirst()
                .orElseThrow(() -> new HTTPException(404))
                .getResidents()
                .add(animal);
        return this.center;
    }



    @DELETE
    @Path(value="animals")
    public void delete_all_animals(){
        Collection<Cage> cages = this.center.getCages();
        Iterator<Cage> cages_iterator = cages.iterator();
         //parcourir des cages
        while(cages_iterator.hasNext()){
            Cage cage = cages_iterator.next();
            // suppression de tous les animaux
            cage.getResidents().removeAll(cage.getResidents());
        }

    }



    @DELETE
    @Path(value="animals/{animal_id}")
    public void delete_animal_by_id(@PathParam(value="animal_id")String animal_id){
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

                    if(animal.getId().equals(UUID.fromString(animal_id))){
                        collection_animals.remove(animal);
                    }
                }
            }
        }
    }

    @DELETE
    @Path(value="animals/at/{emplacement}")
    public void delete_animal_by_(@PathParam("emplacement")String ville){
        Collection<Cage> collection_cages = this.center.getCages();
        Iterator<Cage> iter_cages = collection_cages.iterator();
        /*parcourir chaque cage*/
        while(iter_cages.hasNext()){
            Cage cage = iter_cages.next();
            /*supprimer tout les animaux*/
            if(cage.getName().equals(ville))
                cage.getResidents().removeAll(cage.getResidents());
        }
    }

    @PUT
    @Path(value="animals/{id}")
    @Produces(MediaType.APPLICATION_JSON+";charset=utf-8")
    public void put_animal_by_id(Animal animal_to_put, @PathParam("id")String id_animal){

        animal_to_put.setId(UUID.fromString(id_animal));
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
    }

    @PUT
    @Path(value="/animals")
    public void put_animal(Animal animal){

        animal.setId(UUID.randomUUID());
        Collection<Cage> cages_set = this.center.getCages();
        Collection<Animal> collection_animals;
        Iterator<Cage> cages_iterator = cages_set.iterator();
        Iterator<Animal> iter_animals;

        while(cages_iterator.hasNext()){
            Cage cage = cages_iterator.next();
            if(cage.getName().equals(animal.getCage())){
                for(Animal a: cage.getResidents()){
                    a.setName(animal.getName());
                }
            }
        }
    }
}