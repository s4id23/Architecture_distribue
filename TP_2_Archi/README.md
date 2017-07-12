#Auteurs : 	
BENAOUICHA Mohand-Said
BABAGHAYOU Khaled

DOCUMENTATION DU SERVICE ZOO-MANAGER TP3 & TP4
----------------------------------------------

1.	pour la modification de tous les animaux a la fois, on utilise une extension de google chrome nommée Advanced Rest Client :
	1- on introduit dans l'url l'adresse suivante : 
	http://rest-service-unpictured-perionychium.eu-gb.mybluemix.net/zoo-manager/animals
   	2- on selectionne la methode put
   	3- on insert dans Patload {"name":"nouveau_nom","cage":"usa","species":"chipmunk"}
   	4- on envoi les information en cliquant sur send


2.  pour la suppression de tous les animaux on utilise une extension de google chrome nommée Advanced Rest Client:
   	1- on introduit dans l'url l'adresse suivante : 
	http://rest-service-unpictured-perionychium.eu-gb.mybluemix.net/zoo-manager/animals
   	2- on selectionne la methode delete

3.  pour la creation d'un animal on utilise l'extension ARC en introduisant les informations suivantes :
	1- l'url : http://rest-service-unpictured-perionychium.eu-gb.mybluemix.net/zoo-manager/animals/<nouveau_id_animal>
	2- la methode : post
	3- Payload : {"name":"nouveau_nom","cage":"usa","species":"nouvelle_espèce"}
	4- Content-Type : application/json

4.	pour la modification d'un animal existant on utilise l'extension ARC en introduisant les informations suivantes :
	1- l'url : http://rest-service-unpictured-perionychium.eu-gb.mybluemix.net/zoo-manager/animals/<id_animal_a_modifié>
	2- la methode : put
	3- Payload : {"name":"nouveau_nom","cage":"usa","species":"nouvelle_espèce"}
	4- Content-Type : application/json

5.  pour la suppresion d'un animal par son id on utilise l'extension ARC en introduisant les informations suivantes :
	1- l'url : http://rest-service-unpictured-perionychium.eu-gb.mybluemix.net/zoo-manager/animals/<id_animal_a_supprimé>
	2- la methode : delete

6. 	pour afficher les animaux par leurs noms on lance la requette http get sur le navigateur. 
 	exemple d'utilisation : http://rest-service-unpictured-perionychium.eu-gb.mybluemix.net/zoo-manager/find/byName/Canine

7. 	pour afficher un annimal par sa position on lance la requette http get sur le navigateur. 
	exemple d'utilisation : http://rest-service-unpictured-perionychium.eu-gb.mybluemix.net/zoo-manager/find/at/49.305&1.2157357

8. 	pour afficher des animaux par leurs position on lance la requette http get sur le navigateur.
	exemple d'utilisation : http://rest-service-unpictured-perionychium.eu-gb.mybluemix.net/zoo-manager/find/near/49.305&1.2157357

9.  pour suppresion d'un animal par emplacement on utilise l'extension ARC en introduisant les informations suivantes : 
	1- URL : http://rest-service-unpictured-perionychium.eu-gb.mybluemix.net/zoo-manager/animals/at/<emplacement>
	2- methode : delete
