package ar.edu.ubp.das.mascotas.services.auth;

import ar.edu.ubp.das.mascotas.BE.auth.LoginRequestBE;
import ar.edu.ubp.das.mascotas.BE.auth.LoginResponseBE;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

@WebService(targetNamespace = "http://auth.services.mascotas.das.ubp.edu.ar/")
public interface IAuthService {

    @WebMethod
    @WebResult(name = "response")
    LoginResponseBE login(@WebParam(name = "request") LoginRequestBE request);

}
