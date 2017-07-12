
  Documentation Du service ZooManager 

auteurs: BABAGHAYOU Khaled & BENAOUICHA Mohand-Said 

 1ere partie:
 ------------
  - on a implementer un site web simple on utilisons les service web Geonames et Wolframalpha
  - le site veut permet e detecter les information d'un batiment selectionée
  - on a afficher les données dans les deux format XML et JSON on utilisant les deux requete 
     XML   :  http://api.geonames.org/findNearby?lat="+lat+"&lng="+lng+"&username=m1gil
     JSON  :  http://api.geonames.org/findNearJSONby?lat="+lat+"&lng="+lng+"&username=m1gil
    ou lat= latitude  et lng=langitude
  - ensuite on a afficher la position exacte du batiment
  - enfin on a utiliser l'API Wolfram pour recuperer plus d'information du batiment 
    dans notre exemple on a utiliser la requete 
        https://api.wolframalpha.com/v2/query?input=49.3869+N%2C+1.0683+E&format=pleintext,html&output=XML&appid=LWKYT5-6WTT7KX439
     ou on retrouver la commune de notre batiment: saint-etienne-du-rouveray

 2eme partie
 ------------
   on a essayer de completer les service de base offert par le service ZOOMAnger on ajoutant les nouvelles 
fonctionnalités suivantes:
   
   1) Modifie l'ensemble des animaux : le service offre la possibilité de modifier l'ensemble des animaux existant en utilisant
   la methode "update_Animals" definit dans la classe MyClient

   2) pour la suppression de tous les animaux on utilise la methode "delete_animals" definit dans la classe MyClient

   3) pour la creation d'un animal on utilise la methode "create_animal" qui prend en parametre d'entre un animal
   exemple d'utilisation : client.create_animal(new Animal("Panda","usa","Mamifere",UUID.randomUUID()));

   4) Pour modifie un animal identifié par un id_animal on utilise la methode "reset_animal"
   exemple d'utilisation : client.reset_animal("6d01f462-b4e3-420a-83f1-3ddbaf42ce0d");  
   cette methode utilise la methode http PUT
   
   5) Pour supprimer un animal identifié par un id_animal on utilise la methode delete_animal_By_Id()
   exemple d'utilisation : client.delete_animal_By_Id("6d01f462-b4e3-420a-83f1-3ddbaf42ce0d");
   cette methode utilise la methode http DELETE

   6) pour afficher les animaux par leurs noms on lance la requette http get sur le navigateur.
   exemple d'utilisation : http://127.0.0.1:8084/find/byName/Canine

   7) pour afficher un annimal par sa position on lance la requette http get sur le navigateur.
   exemple d'utilisation : http://127.0.0.1:8084/find/at/49.305&1.2157357

   8) pour afficher des animaux par leurs position on lance la requette http get sur le navigateur.
   exemple d'utilisation : http://127.0.0.1:8084/find/near/49.305&1.2157357