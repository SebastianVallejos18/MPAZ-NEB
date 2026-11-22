package cl.duoc.mpazneb.asignaciones.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// Respuesta elegida por el estudiante en una pregunta, dentro de un intento
@Entity
@Table(name = "respuestas")
public class Respuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "asignacion_id", nullable = false)
    private Asignacion asignacion;

    @Column(nullable = false)
    private Long preguntaId;

    private int intento;

    // 0 = A, 1 = B, 2 = C
    private int opcionElegida;

    private boolean esCorrecta;

    @Column(nullable = false)
    private Long fecha;

    @JsonProperty(value = "asignacionId", access = JsonProperty.Access.READ_ONLY)
    public Long getAsignacionId() {
        return asignacion == null ? null : asignacion.getId();
    }

    // Constructor vacío: JPA lo necesita para crear el objeto al leer una fila de la tabla
    public Respuesta() {
    }

    // ----- Getters y setters -----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Asignacion getAsignacion() {
        return asignacion;
    }

    public void setAsignacion(Asignacion asignacion) {
        this.asignacion = asignacion;
    }

    public Long getPreguntaId() {
        return preguntaId;
    }

    public void setPreguntaId(Long preguntaId) {
        this.preguntaId = preguntaId;
    }

    public int getIntento() {
        return intento;
    }

    public void setIntento(int intento) {
        this.intento = intento;
    }

    public int getOpcionElegida() {
        return opcionElegida;
    }

    public void setOpcionElegida(int opcionElegida) {
        this.opcionElegida = opcionElegida;
    }

    public boolean isEsCorrecta() {
        return esCorrecta;
    }

    public void setEsCorrecta(boolean esCorrecta) {
        this.esCorrecta = esCorrecta;
    }

    public Long getFecha() {
        return fecha;
    }

    public void setFecha(Long fecha) {
        this.fecha = fecha;
    }
}
