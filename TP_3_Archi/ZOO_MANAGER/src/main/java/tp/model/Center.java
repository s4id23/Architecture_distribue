package tp.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

public class Center {

    Collection<Cage> cages;
    Position position;
    String name;

    public Center() {
        cages = new LinkedList<>();
    }

    public Center(Collection<Cage> cages, Position position, String name) {
        this.cages = cages;
        this.position = position;
        this.name = name;
    }

    public Animal findAnimalById(UUID uuid) throws AnimalNotFoundException {
        return this.cages.stream()
                .map(Cage::getResidents)
                .flatMap(Collection::stream)
                .filter(animal -> uuid.equals(animal.getId()))
                .findFirst()
                .orElseThrow(AnimalNotFoundException::new);
    }

    public Animal findAnimalByName(String name) throws AnimalNotFoundException {
        return this.cages.stream()
                .map(Cage::getResidents)
                .flatMap(Collection::stream)
                .filter(animal -> name.equals(animal.getName()))
                .findFirst()
                .orElseThrow(AnimalNotFoundException::new);
    }

    public Collection<Cage> getCages() {
        return cages;
    }

    public Cage CageByName(String name) throws CageNotFoundException {
        return this.cages.stream()
                .filter(cage -> cage.getName().equals(name))
                .findFirst()
                .orElseThrow(CageNotFoundException::new);
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "Center{" +
                "cages=" + cages +
                ", position=" + position +
                ", name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setCages(Collection<Cage> cages) {
        this.cages = cages;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setName(String name) {
        this.name = name;
    }
}
