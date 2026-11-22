package cl.duoc.mpazneb.asignaciones.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

// Una actividad asignada a un estudiante ficticio por una asistente.
// actividadId, estudianteId y asistenteId apuntan a registros de los OTROS microservicios,
// por eso se guardan solo como números (cada microservicio tiene su propia base de datos).
@Entity
@Table(name = "asignaciones")
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long actividadId;

    @Column(nullable = false)
    private Long estudianteId;

    @Column(nullable = false)
    private Long asistenteId;

    // Código de 6 caracteres que el estudiante escribe para entrar
    @Column(nullable = false, unique = true, length = 6)
    private String codigoTemporal;

    // Fecha de creación en milisegundos
    @Column(nullable = false)
    private Long fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoAsignacion estado = EstadoAsignacion.PENDIENTE;

    private int intentos = 0;
    private int aciertos = 0;
    private int errores = 0;

    @Column(length = 300)
    private String observacion = "";

    // Si se elimina la asignación, se eliminan también sus respuestas
    @JsonIgnore
    @OneToMany(mappedBy = "asignacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Respuesta> respuestas = new ArrayList<>();

    // Constructor vacío: JPA lo necesita para crear el objeto al leer una fila de la tabla
    public Asignacion() {
    }

    // ----- Getters y setters -----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActividadId() {
        return actividadId;
    }

    public void setActividadId(Long actividadId) {
        this.actividadId = actividadId;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public Long getAsistenteId() {
        return asistenteId;
    }

    public void setAsistenteId(Long asistenteId) {
        this.asistenteId = asistenteId;
    }

    public String getCodigoTemporal() {
        return codigoTemporal;
    }

    public void setCodigoTemporal(String codigoTemporal) {
        this.codigoTemporal = codigoTemporal;
    }

    public Long getFecha() {
        return fecha;
    }

    public void setFecha(Long fecha) {
        this.fecha = fecha;
    }

    public EstadoAsignacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoAsignacion estado) {
        this.estado = estado;
    }

    public int getIntentos() {
        return intentos;
    }

    public void setIntentos(int intentos) {
        this.intentos = intentos;
    }

    public int getAciertos() {
        return aciertos;
    }

    public void setAciertos(int aciertos) {
        this.aciertos = aciertos;
    }

    public int getErrores() {
        return errores;
    }

    public void setErrores(int errores) {
        this.errores = errores;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public List<Respuesta> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(List<Respuesta> respuestas) {
        this.respuestas = respuestas;
    }
}
