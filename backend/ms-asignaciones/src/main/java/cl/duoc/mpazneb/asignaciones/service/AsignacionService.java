package cl.duoc.mpazneb.asignaciones.service;

import cl.duoc.mpazneb.asignaciones.dto.CrearAsignacionRequest;
import cl.duoc.mpazneb.asignaciones.dto.ProgresoRequest;
import cl.duoc.mpazneb.asignaciones.dto.RespuestaRequest;
import cl.duoc.mpazneb.asignaciones.exception.RecursoNoEncontradoException;
import cl.duoc.mpazneb.asignaciones.exception.ReglaNegocioException;
import cl.duoc.mpazneb.asignaciones.model.Asignacion;
import cl.duoc.mpazneb.asignaciones.model.EstadoAsignacion;
import cl.duoc.mpazneb.asignaciones.model.Respuesta;
import cl.duoc.mpazneb.asignaciones.repository.AsignacionRepository;
import cl.duoc.mpazneb.asignaciones.repository.RespuestaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
public class AsignacionService {

    // Sin 0/O ni 1/I/L para que el código no se confunda al leerlo o escribirlo
    private static final String ALFABETO = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
    private static final int LARGO_CODIGO = 6;

    private final AsignacionRepository asignaciones;
    private final RespuestaRepository respuestas;
    private final SecureRandom aleatorio = new SecureRandom();

    public AsignacionService(AsignacionRepository asignaciones, RespuestaRepository respuestas) {
        this.asignaciones = asignaciones;
        this.respuestas = respuestas;
    }

    // Sin asistenteId devuelve todas
    public List<Asignacion> deAsistente(Long asistenteId) {
        return asistenteId == null
                ? asignaciones.findAll()
                : asignaciones.findByAsistenteIdOrderByFechaDesc(asistenteId);
    }

    public Asignacion buscarPorId(Long id) {
        Asignacion asignacion = asignaciones.findById(id).orElse(null);
        if (asignacion == null) {
            throw new RecursoNoEncontradoException("No existe la asignación " + id);
        }
        return asignacion;
    }

    public Asignacion buscarPorCodigo(String codigo) {
        Asignacion asignacion = asignaciones.findByCodigoTemporal(codigo.toUpperCase());
        if (asignacion == null) {
            throw new RecursoNoEncontradoException("No encontramos ese código");
        }
        return asignacion;
    }

    public Asignacion crear(CrearAsignacionRequest datos) {
        Asignacion asignacion = new Asignacion();
        asignacion.setActividadId(datos.actividadId());
        asignacion.setEstudianteId(datos.estudianteId());
        asignacion.setAsistenteId(datos.asistenteId());
        asignacion.setCodigoTemporal(generarCodigoUnico());
        asignacion.setFecha(System.currentTimeMillis());
        return asignaciones.save(asignacion);
    }

    public Asignacion actualizarProgreso(Long id, ProgresoRequest datos) {
        Asignacion asignacion = buscarPorId(id);
        if (asignacion.getEstado() == EstadoAsignacion.FINALIZADA) {
            throw new ReglaNegocioException("La asignación ya está finalizada");
        }
        asignacion.setEstado(datos.estado());
        asignacion.setIntentos(datos.intentos());
        asignacion.setAciertos(datos.aciertos());
        asignacion.setErrores(datos.errores());
        return asignaciones.save(asignacion);
    }

    public Asignacion guardarObservacion(Long id, String observacion) {
        Asignacion asignacion = buscarPorId(id);
        asignacion.setObservacion(observacion == null ? "" : observacion.trim());
        return asignaciones.save(asignacion);
    }

    @Transactional
    public void eliminar(Long id) {
        asignaciones.delete(buscarPorId(id));
    }

    public List<Respuesta> respuestasDe(Long asignacionId, Integer intento) {
        buscarPorId(asignacionId); // 404 si no existe
        return intento == null
                ? respuestas.findByAsignacionIdOrderByIntentoAscIdAsc(asignacionId)
                : respuestas.findByAsignacionIdAndIntentoOrderByIdAsc(asignacionId, intento);
    }

    // @Transactional: se guarda la respuesta Y se actualizan los aciertos/errores, o no se guarda nada
    @Transactional
    public Asignacion registrarRespuesta(Long asignacionId, RespuestaRequest datos) {
        Asignacion asignacion = buscarPorId(asignacionId);
        if (asignacion.getEstado() == EstadoAsignacion.FINALIZADA) {
            throw new ReglaNegocioException("Esta actividad ya terminó");
        }

        Respuesta respuesta = new Respuesta();
        respuesta.setAsignacion(asignacion);
        respuesta.setPreguntaId(datos.preguntaId());
        respuesta.setIntento(datos.intento());
        respuesta.setOpcionElegida(datos.opcionElegida());
        respuesta.setEsCorrecta(datos.esCorrecta());
        respuesta.setFecha(System.currentTimeMillis());
        respuestas.save(respuesta);

        if (datos.esCorrecta()) {
            asignacion.setAciertos(asignacion.getAciertos() + 1);
        } else {
            asignacion.setErrores(asignacion.getErrores() + 1);
        }
        return asignaciones.save(asignacion);
    }

    private String generarCodigoUnico() {
        String codigo;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < LARGO_CODIGO; i++) {
                sb.append(ALFABETO.charAt(aleatorio.nextInt(ALFABETO.length())));
            }
            codigo = sb.toString();
        } while (asignaciones.existsByCodigoTemporal(codigo)); // se repite si ya existe
        return codigo;
    }
}
