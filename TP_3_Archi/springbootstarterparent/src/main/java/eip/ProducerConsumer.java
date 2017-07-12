/**
 * Created by khaled baba on 28/04/2017.
 */
package eip;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.log4j.BasicConfigurator;
import java.util.Scanner;

import static java.lang.System.exit;

public class ProducerConsumer {

    public static void main(String[] args) throws Exception
    {
        Scanner sc = new Scanner(System.in);
        Scanner sc2 = new Scanner(System.in);

        System.out.println("saisir la methode :");
        System.out.println(" 1 -> Consumer-1\n 2 -> Consumer-All\n 3 -> GET ALL\n 4 -> GET BY ID\n 5 -> ajouter Animal POST\n 6 -> Modifier Animal PUT\n 7 -> Supprimer Animal\n  8 -> Pleusieur Instance \n 9 -> Find Position Animal By geoname" );
        int a=sc.nextInt();

        System.out.println(" Id_Choisie pour get id:");
        final String idAnimal=sc2.nextLine();

        // Configure le logger par défaut
        BasicConfigurator.configure();

// Contexte Camel par défaut
        CamelContext context = new DefaultCamelContext();
// Crée une route contenant le consommateur
        RouteBuilder routeBuilder = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // On définit un consommateur 'consumer-1'
                from("direct:consumer-1").to("log:affiche-1-log");
                // On définit un consommateur 'consumer-2'
                from("direct:consumer-2").to("file:messages");

                from("direct:consumer-all")
                        .choice()
                        .when(header("destinataire").isEqualTo("ecrire"))
                        .to("direct:consumer-2")
                        .otherwise()
                        .to("direct:consumer-1");

                from("direct:CitymanagerGETALL")
                        .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                        .setHeader(Exchange.HTTP_METHOD,constant("GET"))
                        .to("http://127.0.0.1:8084/animals/")
                        .log("reponse received : ${body}");

                from("direct:CitymanagerGetByName")
                        .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                        .setHeader(Exchange.HTTP_METHOD,constant("GET"))
                        .to("http://127.0.0.1:8084/animals/findByName/Canine")
                        .log("reponse received : ${body}");

                from("direct:CitymanagerPOST")
                        .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                        .setHeader(Exchange.HTTP_METHOD,constant("POST"))
                        .to("http://127.0.0.1:8084/animals/")
                        .log("reponse received : ${body}");

                from("direct:CitymanagerPUT")
                        .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                        .setHeader(Exchange.HTTP_METHOD,constant("PUT"))
                        .to("http://127.0.0.1:8084/animals/"+idAnimal)
                        .log("reponse received : ${body}");

                from("direct:CitymanagerDELETE")
                        .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                        .setHeader(Exchange.HTTP_METHOD,constant("DELETE"))
                        .to("http://127.0.0.1:8084/animals/"+idAnimal)
                        .log("reponse received : ${body}");

                from("direct:CitymanagerMultiServeur")
                        .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                        .setHeader(Exchange.HTTP_METHOD,constant("GET"))
                        .to("http://127.0.0.1:8084/animals/")
                        .to("http://127.0.0.1:8085/animals/")
                        .to("http://127.0.0.1:8086/animals/")
                        .log("reponse received : ${body}");

                from("direct:CitymanagerFindPosition")
                        .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                        .setHeader(Exchange.HTTP_METHOD,constant("GET"))
                        .to("http://127.0.0.1:8084//findAnimalPositionByName/Canine")
                        .log("reponse received : ${body}");
            }
        };

        String str="",BeginWithW="";
        if(a==2 || a==1) {
            System.out.println("saisir le message :");
            str = sc.nextLine();
            BeginWithW = "header";
            if (str.startsWith("w")) {
                BeginWithW = "ecrire";
            }
        }

        // On ajoute la route au contexte
        routeBuilder.addRoutesToCamelContext(context);
        // On démarre le contexte pour activer les routes
        context.start();
        // On crée un producteur
        ProducerTemplate pt = context.createProducerTemplate();

        switch(a) {
            case 1:
                // qui envoie un message au consommateur 'consumer-1'
                pt.sendBody("direct:consumer-1", str);
                break;
            case 2:
                // qui envoie un message au consommateur 'consumer-1' ou 'consumer-2'
                pt.sendBodyAndHeader("direct:consumer-all",str,"destinataire",BeginWithW);
                break;
            case 3:
                // Envoie un message GET all animals
                pt.sendBody("direct:CitymanagerGETALL","");
                break;
            case 4:
                //GEt animal par id
                pt.sendBody("direct:CitymanagerGetByName","");
                break;
            case 5:
                // POST
                pt.sendBody("direct:CitymanagerPOST", "{\n" + "\"name\": \"Molaire\",\n" + "\"cage\": \"amazon\",\n" + "\"species\": \"Piranha\",\n" + "\"id\": \"ca1219b8-37a8-4d63-bb2d-76a29ffae2cd\"\n" + "}");
                break;
            case 6:
                // PUT
                pt.sendBody("direct:CitymanagerPUT", "{\n" + "\"name\": \"Molaire\",\n" + "\"cage\": \"amazon\",\n" + "\"species\": \"Piranha\",\n" + "\"id\": \"ca1219b8-37a8-4d63-bb2d-76a29ffae2cd\"\n" + "}");
                break;
            case 7:
                pt.sendBody("direct:CitymanagerDELETE", "");
                // DELETE
                break;
            case 8:
                pt.sendBody("direct:CitymanagerMultiServeur", "");
                break;
            case 9:
                pt.sendBody("direct:CitymanagerFindPosition", "");
                break;
            default:
                break;
        }
    }
}
