package ni.edu.uam.facturacion.mode1;

import java.time.LocalDate;

public class Empleado {

    private Integer id;
    private String nombres;
    private String apellidos;
    private Cargo cargo;
    private LocalDate fechaContratacion;
    private boolean activo;

    public Empleado() {
    }

    public Empleado(
            Integer id,
            String nombres,
            String apellidos,
            Cargo cargo,
            LocalDate fechaContratacion,
            boolean activo) {

        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.cargo = cargo;
        this.fechaContratacion = fechaContratacion;
        this.activo = activo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public LocalDate getFechaContratacion() {
        return fechaContratacion;
    }

    public void setFechaContratacion(
            LocalDate fechaContratacion) {

        this.fechaContratacion = fechaContratacion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}