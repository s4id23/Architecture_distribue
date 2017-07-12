package tp.rest;

import java.io.StringReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.*;
import javax.xml.ws.http.HTTPBinding;

@WebServiceProvider
@ServiceMode(value = Service.Mode.PAYLOAD)
public class REST implements Provider<Source> {
	private String message;
    public REST(String msg){
        this.message=msg;
    }
	public Source invoke(Source source) {
		String replyElement = new String("<p>hello world</p>");
		StreamSource reply = new StreamSource(new StringReader(replyElement));
		String replyElement2 = new String("<p>Université de Rouen</p>");
        StreamSource reply2 = new StreamSource(new StringReader(replyElement2));
        String replyElement3 = new String("<p>Reponse du service rest</p>");
        StreamSource reply3 = new StreamSource(new StringReader(replyElement3));
        if (this.message=="hello word")
            return reply;
        if (this.message=="univ rouen")
            return reply2;
        if (this.message=="reponse service rest")
            return reply3;
		
		return reply;
	}

	public static void main(String args[]) {
		Endpoint e = Endpoint.create(HTTPBinding.HTTP_BINDING, new REST("hello word"));
        e.publish("http://127.0.0.1:8084/hello/world");
        e = Endpoint.create(HTTPBinding.HTTP_BINDING, new REST("univ rouen"));
        e.publish("http://127.0.0.1:8089/test");
        e = Endpoint.create(HTTPBinding.HTTP_BINDING, new REST("reponse service rest"));
        e.publish("http://127.0.0.1:8090/hello/world");
		
	}
}