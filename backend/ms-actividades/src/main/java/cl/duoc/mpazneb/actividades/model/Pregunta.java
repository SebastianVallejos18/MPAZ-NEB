package cl.duoc.mpazneb.actividades.model;

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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "preguntas")
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Muchas preguntas pertenecen a una actividad (clave foránea actividad_id)
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "actividad_id", nullable = false)
    private Actividad actividad;

    // EXPLICITA, IMPLICITA, VOCABULARIO, SUMA, RESTA, MULTIPLICACION o PROBLEMA
    @NotBlank(message = "El tipo es obligatorio")
    @Column(nullable = false, length = 20)
    private String tipo;

    @NotBlank(message = "El enunciado es obligatorio")
    @Size(min = 10, max = 200, message = "Debe tener entre 10 y 200 caracteres")
    @Column(nullable = false, length = 200)
    private String enunciado;

    @NotBlank @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String opcionA;

    @NotBlank @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String opcionB;

    @NotBlank @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String opcionC;

    // 0 = A, 1 = B, 2 = C
    @Min(value = 0, message = "La respuesta correcta va de 0 a 2")
    @Max(value = 2, message = "La respuesta correcta va de 0 a 2")
    private int respuestaCorrecta;

    // Solo se usa en Matemática
    @Size(max = 150)
    @Column(length = 150)
    private String procedimiento = "";

    @NotBlank(message = "La retroalimentación es obligatoria")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String retroalimentacion;

    // Se agrega al JSON de salida para que la app sepa a qué actividad pertenece (al recibir se ignora)
    @JsonProperty(value = "actividadId", access = JsonProperty.Access.READ_ONLY)
    public Long getActividadId() {
        return actividad == null ? null : actividad.getId();
    }

    // Constructor vacío: JPA lo necesita para crear el objeto al leer una fila de la tabla
    public Pregunta() {
    }

    // ----- Getters y setters -----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Actividad getActividad() {
        return actividad;
    }

    public void setActividad(Actividad actividad) {
        this.actividad = actividad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public String getOpcionA() {
        return opcionA;
    }

    public void setOpcionA(String opcionA) {
        this.opcionA = opcionA;
    }

    public String getOpcionB() {
        return opcionB;
    }

    public void setOpcionB(String opcionB) {
        this.opcionB = opcionB;
    }

    public String getOpcionC() {
        return opcionC;
    }

    public void setOpcionC(String opcionC) {
        this.opcionC = opcionC;
    }

    public int getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public void setRespuestaCorrecta(int respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
    }

    public String getProcedimiento() {
        return procedimiento;
    }

    public void setProcedimiento(String procedimiento) {
        this.procedimiento = procedimiento;
    }

    public String getRetroalimentacion() {
        return retroalimentacion;
    }

    public void setRetroalimentacion(String retroalimentacion) {
        this.retroalimentacion = retroalimentacion;
    }
}
