package cl.duoc.mpazneb.actividades.model;

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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "actividades")
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 5, max = 60, message = "Debe tener entre 5 y 60 caracteres")
    @Column(nullable = false, length = 60)
    private String nombre;

    @NotNull(message = "El módulo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Modulo modulo;

    @Min(value = 1, message = "El curso va de 1 a 4")
    @Max(value = 4, message = "El curso va de 1 a 4")
    private int cursoRecomendado;

    @NotBlank(message = "El contenido es obligatorio")
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String contenido;

    @NotBlank(message = "El objetivo pedagógico es obligatorio")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String objetivoPedagogico;

    @NotNull(message = "La dificultad es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Dificultad dificultad;

    @NotBlank(message = "Las instrucciones son obligatorias")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String instrucciones;

    // Solo se usa en Lectura; en Matemática queda vacío
    @Size(max = 600)
    @Column(length = 600)
    private String textoBase = "";

    @Min(value = 1, message = "Los intentos van de 1 a 5")
    @Max(value = 5, message = "Los intentos van de 1 a 5")
    private int intentosPermitidos = 1;

    private boolean lecturaEnVoz = false;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoActividad estado = EstadoActividad.BORRADOR;

    // Una actividad tiene muchas preguntas; si se borra la actividad se borran sus preguntas.
    // @JsonIgnore: las preguntas se piden aparte en GET /api/actividades/{id}/preguntas
    @JsonIgnore
    @OneToMany(mappedBy = "actividad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pregunta> preguntas = new ArrayList<>();

    // Constructor vacío: JPA lo necesita para crear el objeto al leer una fila de la tabla
    public Actividad() {
    }

    // ----- Getters y setters -----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    public int getCursoRecomendado() {
        return cursoRecomendado;
    }

    public void setCursoRecomendado(int cursoRecomendado) {
        this.cursoRecomendado = cursoRecomendado;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getObjetivoPedagogico() {
        return objetivoPedagogico;
    }

    public void setObjetivoPedagogico(String objetivoPedagogico) {
        this.objetivoPedagogico = objetivoPedagogico;
    }

    public Dificultad getDificultad() {
        return dificultad;
    }

    public void setDificultad(Dificultad dificultad) {
        this.dificultad = dificultad;
    }

    public String getInstrucciones() {
        return instrucciones;
    }

    public void setInstrucciones(String instrucciones) {
        this.instrucciones = instrucciones;
    }

    public String getTextoBase() {
        return textoBase;
    }

    public void setTextoBase(String textoBase) {
        this.textoBase = textoBase;
    }

    public int getIntentosPermitidos() {
        return intentosPermitidos;
    }

    public void setIntentosPermitidos(int intentosPermitidos) {
        this.intentosPermitidos = intentosPermitidos;
    }

    public boolean isLecturaEnVoz() {
        return lecturaEnVoz;
    }

    public void setLecturaEnVoz(boolean lecturaEnVoz) {
        this.lecturaEnVoz = lecturaEnVoz;
    }

    public EstadoActividad getEstado() {
        return estado;
    }

    public void setEstado(EstadoActividad estado) {
        this.estado = estado;
    }

    public List<Pregunta> getPreguntas() {
        return preguntas;
    }

    public void setPreguntas(List<Pregunta> preguntas) {
        this.preguntas = preguntas;
    }
}
