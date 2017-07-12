package tp.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by arthu on 09/03/2017.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CageNotFoundException extends Exception {
}
