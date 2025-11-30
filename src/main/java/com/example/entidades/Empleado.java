package com.example.entidades;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase abstracta que representa un empleado del sistema.
 * Maneja los datos comunes de todos los empleados: legajo, nombre,
 * disponibilidad, cantidad de retrasos y el historial de tareas realizadas.
 */
public abstract class Empleado {
    protected int legajo;
    protected String nombre;
    protected boolean disponible;
    protected int cantRetrasos;
    protected List<String> tareasRealizadas;
    protected static int contadorLegajo = 1;

    /**
     * Constructor de la clase Empleado.
     * 
     * @param nombre Nombre del empleado (no puede ser null o vacío)
     * @throws IllegalArgumentException si el nombre es inválido
     */
    public Empleado(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        this.legajo = contadorLegajo++;
        this.nombre = nombre;
        this.disponible = true;
        this.cantRetrasos = 0;
        this.tareasRealizadas = new ArrayList<>();
    }

    /**
     * Resetea el contador de IDs de proyectos para testing
     */
    public static void resetearContador() {
        contadorLegajo = 1;
    }

    /**
     * Obtiene el número de legajo único del empleado.
     * 
     * @return Número de legajo
     */
    public int getLegajo() {
        return legajo;
    }

    /**
     * Obtiene el nombre del empleado.
     * 
     * @return Nombre del empleado
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Verifica si el empleado está disponible para asignación.
     * 
     * @return true si está disponible, false si está ocupado
     */
    public boolean estaDisponible() {
        return disponible;
    }

    /**
     * Marca al empleado como asignado (no disponible).
     */
    public void asignar() {
        this.disponible = false;
    }

    /**
     * Libera al empleado (lo marca como disponible).
     */
    public void liberar() {
        this.disponible = true;
    }

    /**
     * Obtiene la cantidad total de retrasos acumulados del empleado.
     * 
     * @return Cantidad de retrasos
     */
    public int getCantRetrasos() {
        return cantRetrasos;
    }

    /**
     * Incrementa el contador de retrasos del empleado.
     * Se llama cuando una tarea asignada al empleado tiene un retraso.
     */
    public void incrementarRetrasos() {
        this.cantRetrasos++;
    }

    /**
     * Agrega una tarea al historial de tareas realizadas.
     * 
     * @param tarea Título de la tarea realizada
     */
    public void agregarTareaRealizada(String tarea) {
        this.tareasRealizadas.add(tarea);
    }

    /**
     * Obtiene una copia de la lista de tareas realizadas por el empleado.
     * 
     * @return Lista de títulos de tareas
     */
    public List<String> getTareasRealizadas() {
        return new ArrayList<>(tareasRealizadas);
    }

    /**
     * Método abstracto para calcular el costo de una tarea según el tipo de
     * empleado.
     * Debe ser implementado por las subclases (EmpleadoContratado y
     * EmpleadoDePlanta).
     * 
     * @param duracionDias Duración de la tarea en días
     * @param conRetraso   Indica si la tarea tuvo retraso
     * @return Costo calculado de la tarea
     */
    public abstract double calcularCostoTarea(double duracionDias, boolean conRetraso);

    /**
     * Retorna una representación en String del empleado (su legajo).
     * 
     * @return String con el número de legajo
     */
    @Override
    public String toString() {
        return legajo + "";
    }

    /**
     * Representación completa del empleado con todos sus datos.
     * 
     * @return String detallado con información del empleado
     */
    public String toStringDetallado() {
        StringBuilder sb = new StringBuilder();
        sb.append("Legajo: ").append(legajo).append("\n");
        sb.append("Nombre: ").append(nombre).append("\n");
        sb.append("Disponible: ").append(disponible ? "Sí" : "No").append("\n");
        sb.append("Cantidad de retrasos: ").append(cantRetrasos).append("\n");
        sb.append("Tareas realizadas: ").append(tareasRealizadas.size());
        return sb.toString();
    }
}
