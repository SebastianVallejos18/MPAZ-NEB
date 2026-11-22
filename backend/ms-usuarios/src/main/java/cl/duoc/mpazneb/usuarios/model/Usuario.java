package cl.duoc.mpazneb.usuarios.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Usuario ficticio: solo alias, perfil y estado. No se guardan datos personales reales.
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String alias;

    // Hash SHA-256 de la contraseña. @JsonIgnore evita que viaje a la app.
    @JsonIgnore
    @Column(nullable = false, length = 64)
    private String claveHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Perfil perfil;

    @Column(nullable = false)
    private boolean activo = true;

    // Fecha del último acceso en milisegundos (igual que System.currentTimeMillis() en la app)
    private Long ultimoAcceso;

    // Constructor vacío: JPA lo necesita para crear el objeto al leer una fila de la tabla
    public Usuario() {
    }

    // ----- Getters y setters -----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getClaveHash() {
        return claveHash;
    }

    public void setClaveHash(String claveHash) {
        this.claveHash = claveHash;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Long getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(Long ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }
}
