package ar.edu.ubp.das.mascotas.integrations.veterinarias;

import ar.edu.ubp.das.mascotas.BE.common.PageResponseBE;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Conector SOAP (Apache CXF JAX-WS) hacia sistemas externos de veterinarias adheridas (RF06 / RF09 / RNF09).
 */
@Component
public class SoapVeterinariaIntegrationClient implements VeterinariaIntegrationClient {

    @Override
    public TecnologiaIntegracion getTecnologia() {
        return TecnologiaIntegracion.SOAP;
    }

    @Override
    public PageResponseBE<MascotaExternaBE> obtenerMascotasSinNrm(
            VeterinariaConfiguracionBE config,
            LoteRangoFechasRequestBE filtro) {
        VeterinariaSoapPort port = createPort(config);
        SoapPaginaMascotas page = port.obtenerMascotasSinNrm(
                filtro != null && filtro.getFechaDesde() != null ? filtro.getFechaDesde().toString() : null,
                filtro != null && filtro.getFechaHasta() != null ? filtro.getFechaHasta().toString() : null,
                filtro != null ? filtro.getPagina() : 1,
                filtro != null ? filtro.getTamanioPagina() : 20
        );
        if (page == null) {
            return new PageResponseBE<>();
        }
        List<MascotaExternaBE> mapped = new ArrayList<>();
        if (page.getItems() != null) {
            for (SoapMascotaDto item : page.getItems()) {
                MascotaExternaBE dto = new MascotaExternaBE();
                dto.setIdMascotaExterna(item.getIdMascotaExterna());
                dto.setNombre(item.getNombre());
                dto.setSexo(item.getSexo());
                dto.setAñoNacimiento(item.getAnioNacimiento());
                dto.setEdadAproximada(item.getEdadAproximada());
                dto.setEspecie(item.getEspecie());
                dto.setRaza(item.getRaza());
                dto.setCastrado(item.getCastrado());
                dto.setMicrochip(item.getMicrochip());
                dto.setCuilResponsable(item.getCuilResponsable());
                mapped.add(dto);
            }
        }
        return new PageResponseBE<>(
                mapped,
                page.getPagina(),
                page.getTamanioPagina(),
                page.getTotalElementos(),
                page.getTotalPaginas()
        );
    }

    @Override
    public List<NrmAcuseBE> informarNrm(
            VeterinariaConfiguracionBE config,
            List<NrmAsignadoBE> asignaciones) {
        if (asignaciones == null || asignaciones.isEmpty()) {
            return Collections.emptyList();
        }
        VeterinariaSoapPort port = createPort(config);
        List<NrmAcuseBE> acuses = port.informarNrm(asignaciones);
        return acuses != null ? acuses : Collections.emptyList();
    }

    @Override
    public PageResponseBE<PracticaSanitariaExternaBE> obtenerAtencionesSanitarias(
            VeterinariaConfiguracionBE config,
            LoteRangoFechasRequestBE filtro) {
        VeterinariaSoapPort port = createPort(config);
        SoapPaginaPracticas page = port.obtenerAtencionesSanitarias(
                filtro != null && filtro.getFechaDesde() != null ? filtro.getFechaDesde().toString() : null,
                filtro != null && filtro.getFechaHasta() != null ? filtro.getFechaHasta().toString() : null,
                filtro != null ? filtro.getPagina() : 1,
                filtro != null ? filtro.getTamanioPagina() : 20
        );
        if (page == null) {
            return new PageResponseBE<>();
        }
        List<PracticaSanitariaExternaBE> mapped = new ArrayList<>();
        if (page.getItems() != null) {
            for (SoapPracticaDto item : page.getItems()) {
                PracticaSanitariaExternaBE dto = new PracticaSanitariaExternaBE();
                dto.setIdMascotaExterna(item.getIdMascotaExterna());
                dto.setNroRegMunicipal(item.getNroRegMunicipal());
                dto.setNroAtencion(item.getNroAtencion());
                dto.setNroDetalle(item.getNroDetalle());
                dto.setFechaAtencion(parseIsoDate(item.getFechaAtencion()));
                dto.setTipoPractica(item.getTipoPractica());
                dto.setDetalle(item.getDetalle());
                dto.setFechaVencimiento(parseIsoDate(item.getFechaVencimiento()));
                dto.setProfesional(item.getProfesional());
                mapped.add(dto);
            }
        }
        return new PageResponseBE<>(
                mapped,
                page.getPagina(),
                page.getTamanioPagina(),
                page.getTotalElementos(),
                page.getTotalPaginas()
        );
    }

    protected VeterinariaSoapPort createPort(VeterinariaConfiguracionBE config) {
        if (config == null || config.getUrlServicio() == null || config.getUrlServicio().isBlank()) {
            throw new IllegalArgumentException("URL de servicio SOAP de la veterinaria no configurada");
        }
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(VeterinariaSoapPort.class);
        factory.setAddress(config.getUrlServicio().trim());

        VeterinariaSoapPort port = (VeterinariaSoapPort) factory.create();
        Client client = ClientProxy.getClient(port);
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("X-API-KEY", List.of(config.getApiKey() != null ? config.getApiKey().trim() : ""));
        client.getRequestContext().put(Message.PROTOCOL_HEADERS, headers);
        return port;
    }

    private static LocalDate parseIsoDate(String iso) {
        return iso != null && !iso.isBlank() ? LocalDate.parse(iso.trim()) : null;
    }

    @WebService(targetNamespace = "http://veterinaria.das.ubp.edu.ar/", name = "VeterinariaSoapService")
    public interface VeterinariaSoapPort {

        @WebMethod
        SoapPaginaMascotas obtenerMascotasSinNrm(
                @WebParam(name = "fechaDesde") String fechaDesde,
                @WebParam(name = "fechaHasta") String fechaHasta,
                @WebParam(name = "pagina") Integer pagina,
                @WebParam(name = "tamanioPagina") Integer tamanioPagina
        );

        @WebMethod
        List<NrmAcuseBE> informarNrm(@WebParam(name = "asignaciones") List<NrmAsignadoBE> asignaciones);

        @WebMethod
        SoapPaginaPracticas obtenerAtencionesSanitarias(
                @WebParam(name = "fechaDesde") String fechaDesde,
                @WebParam(name = "fechaHasta") String fechaHasta,
                @WebParam(name = "pagina") Integer pagina,
                @WebParam(name = "tamanioPagina") Integer tamanioPagina
        );
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SoapPaginaMascotas {
        private List<SoapMascotaDto> items;
        private int pagina;
        private int tamanioPagina;
        private long totalElementos;
        private int totalPaginas;

        public List<SoapMascotaDto> getItems() {
            return items;
        }

        public void setItems(List<SoapMascotaDto> items) {
            this.items = items;
        }

        public int getPagina() {
            return pagina;
        }

        public void setPagina(int pagina) {
            this.pagina = pagina;
        }

        public int getTamanioPagina() {
            return tamanioPagina;
        }

        public void setTamanioPagina(int tamanioPagina) {
            this.tamanioPagina = tamanioPagina;
        }

        public long getTotalElementos() {
            return totalElementos;
        }

        public void setTotalElementos(long totalElementos) {
            this.totalElementos = totalElementos;
        }

        public int getTotalPaginas() {
            return totalPaginas;
        }

        public void setTotalPaginas(int totalPaginas) {
            this.totalPaginas = totalPaginas;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SoapMascotaDto {
        private String idMascotaExterna;
        private String nombre;
        private String sexo;
        private Integer anioNacimiento;
        private Integer edadAproximada;
        private String especie;
        private String raza;
        private Boolean castrado;
        private String microchip;
        private String cuilResponsable;

        public String getIdMascotaExterna() {
            return idMascotaExterna;
        }

        public void setIdMascotaExterna(String idMascotaExterna) {
            this.idMascotaExterna = idMascotaExterna;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getSexo() {
            return sexo;
        }

        public void setSexo(String sexo) {
            this.sexo = sexo;
        }

        public Integer getAnioNacimiento() {
            return anioNacimiento;
        }

        public void setAnioNacimiento(Integer anioNacimiento) {
            this.anioNacimiento = anioNacimiento;
        }

        public Integer getEdadAproximada() {
            return edadAproximada;
        }

        public void setEdadAproximada(Integer edadAproximada) {
            this.edadAproximada = edadAproximada;
        }

        public String getEspecie() {
            return especie;
        }

        public void setEspecie(String especie) {
            this.especie = especie;
        }

        public String getRaza() {
            return raza;
        }

        public void setRaza(String raza) {
            this.raza = raza;
        }

        public Boolean getCastrado() {
            return castrado;
        }

        public void setCastrado(Boolean castrado) {
            this.castrado = castrado;
        }

        public String getMicrochip() {
            return microchip;
        }

        public void setMicrochip(String microchip) {
            this.microchip = microchip;
        }

        public String getCuilResponsable() {
            return cuilResponsable;
        }

        public void setCuilResponsable(String cuilResponsable) {
            this.cuilResponsable = cuilResponsable;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SoapPaginaPracticas {
        private List<SoapPracticaDto> items;
        private int pagina;
        private int tamanioPagina;
        private long totalElementos;
        private int totalPaginas;

        public List<SoapPracticaDto> getItems() {
            return items;
        }

        public void setItems(List<SoapPracticaDto> items) {
            this.items = items;
        }

        public int getPagina() {
            return pagina;
        }

        public void setPagina(int pagina) {
            this.pagina = pagina;
        }

        public int getTamanioPagina() {
            return tamanioPagina;
        }

        public void setTamanioPagina(int tamanioPagina) {
            this.tamanioPagina = tamanioPagina;
        }

        public long getTotalElementos() {
            return totalElementos;
        }

        public void setTotalElementos(long totalElementos) {
            this.totalElementos = totalElementos;
        }

        public int getTotalPaginas() {
            return totalPaginas;
        }

        public void setTotalPaginas(int totalPaginas) {
            this.totalPaginas = totalPaginas;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SoapPracticaDto {
        private String idMascotaExterna;
        private Integer nroRegMunicipal;
        private Integer nroAtencion;
        private Integer nroDetalle;
        private String fechaAtencion;
        private String tipoPractica;
        private String detalle;
        private String fechaVencimiento;
        private String profesional;

        public String getIdMascotaExterna() {
            return idMascotaExterna;
        }

        public void setIdMascotaExterna(String idMascotaExterna) {
            this.idMascotaExterna = idMascotaExterna;
        }

        public Integer getNroRegMunicipal() {
            return nroRegMunicipal;
        }

        public void setNroRegMunicipal(Integer nroRegMunicipal) {
            this.nroRegMunicipal = nroRegMunicipal;
        }

        public Integer getNroAtencion() {
            return nroAtencion;
        }

        public void setNroAtencion(Integer nroAtencion) {
            this.nroAtencion = nroAtencion;
        }

        public Integer getNroDetalle() {
            return nroDetalle;
        }

        public void setNroDetalle(Integer nroDetalle) {
            this.nroDetalle = nroDetalle;
        }

        public String getFechaAtencion() {
            return fechaAtencion;
        }

        public void setFechaAtencion(String fechaAtencion) {
            this.fechaAtencion = fechaAtencion;
        }

        public String getTipoPractica() {
            return tipoPractica;
        }

        public void setTipoPractica(String tipoPractica) {
            this.tipoPractica = tipoPractica;
        }

        public String getDetalle() {
            return detalle;
        }

        public void setDetalle(String detalle) {
            this.detalle = detalle;
        }

        public String getFechaVencimiento() {
            return fechaVencimiento;
        }

        public void setFechaVencimiento(String fechaVencimiento) {
            this.fechaVencimiento = fechaVencimiento;
        }

        public String getProfesional() {
            return profesional;
        }

        public void setProfesional(String profesional) {
            this.profesional = profesional;
        }
    }
}
