package com.example.entidades;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Representa un proyecto con sus tareas, empleados asignados y gestión de
 * estados.
 * Un proyecto pasa por los estados: PENDIENTE → ACTIVO → FINALIZADO
 */
public class Proyecto {
    private int id;
    private String nombreCliente;
    private String emailCliente;
    private String telefonoCliente;
    private String direccion;
    private LocalDate fechaInicio;
    private LocalDate fechaEstimadaFin;
    private LocalDate fechaRealFin;
    private HashMap<String, Tarea> tareas;
    private String estado; // "PENDIENTE", "ACTIVO", "FINALIZADO"
    private Set<Integer> empleadosAsignados; // Empleados actualmente asignados
    private List<Integer> empleadosHistoricos; // Historial de todos los empleados que participaron
    private static int contadorId = 1;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Constructor del Proyecto.
     * 
     * @param cliente     Array con [nombre, email, telefono]
     * @param direccion   Dirección donde se realiza el proyecto
     * @param fechaInicio Fecha de inicio (formato: yyyy-MM-dd)
     * @param fechaFin    Fecha estimada de finalización (formato: yyyy-MM-dd)
     * @throws IllegalArgumentException si la fecha de fin es anterior a la de
     *                                  inicio
     */
    public Proyecto(String[] cliente, String direccion, String fechaInicio, String fechaFin) {
        this.id = contadorId++;
        this.nombreCliente = cliente[0];
        this.emailCliente = cliente.length > 1 ? cliente[1] : "";
        this.telefonoCliente = cliente.length > 2 ? cliente[2] : "";
        this.direccion = direccion;
        this.fechaInicio = LocalDate.parse(fechaInicio, formatter);
        this.fechaEstimadaFin = LocalDate.parse(fechaFin, formatter);
        this.fechaRealFin = LocalDate.parse(fechaFin, formatter);

        if (this.fechaEstimadaFin.isBefore(this.fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio");
        }

        this.tareas = new HashMap<>();
        this.estado = "PENDIENTE";
        this.empleadosAsignados = new HashSet<>();
        this.empleadosHistoricos = new ArrayList<>();
    }

    /**
     * Resetea el contador de IDs de proyectos para testing
     */
    public static void resetearContador() {
        contadorId = 1;
    }

    /**
     * Obtiene el ID único del proyecto.
     * 
     * @return ID del proyecto
     */
    public int getId() {
        return id;
    }

    /**
     * Obtiene la dirección del proyecto.
     * 
     * @return Dirección
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Obtiene el estado actual del proyecto.
     * 
     * @return Estado (PENDIENTE, ACTIVO, FINALIZADO)
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Obtiene la fecha de inicio del proyecto.
     * 
     * @return Fecha de inicio
     */
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    /**
     * Obtiene la fecha estimada de finalización.
     * 
     * @return Fecha estimada de fin
     */
    public LocalDate getFechaEstimadaFin() {
        return fechaEstimadaFin;
    }

    /**
     * Obtiene la fecha real de finalización.
     * 
     * @return Fecha real de fin
     */
    public LocalDate getFechaRealFin() {
        return fechaRealFin;
    }

    /**
     * Obtiene el nombre del cliente.
     * 
     * @return Nombre del cliente
     */
    public String getNombreCliente() {
        return nombreCliente;
    }

    /**
     * Agrega una nueva tarea al proyecto.
     * Actualiza las fechas de finalización sumando la duración de la tarea.
     * 
     * @param titulo      Título de la tarea
     * @param descripcion Descripción de la tarea
     * @param duracion    Duración estimada en días
     * @throws IllegalArgumentException si el proyecto está finalizado
     */
    public void agregarTarea(String titulo, String descripcion, double duracion) {
        agregarTarea(titulo, descripcion, duracion, true);
    }

    /**
     * Agrega una tarea al proyecto con opción de actualizar fechas.
     * 
     * @param titulo           Título de la tarea
     * @param descripcion      Descripción de la tarea
     * @param duracion         Duración estimada en días
     * @param actualizarFechas Si es true, suma la duración a las fechas de
     *                         finalización
     * @throws IllegalArgumentException si el proyecto está finalizado
     */
    private void agregarTarea(String titulo, String descripcion, double duracion, boolean actualizarFechas) {
        if (estaFinalizado()) {
            throw new IllegalArgumentException("No se pueden agregar tareas a un proyecto finalizado");
        }
        Tarea tarea = new Tarea(titulo, descripcion, duracion);
        tareas.put(titulo, tarea);

        // Solo actualizar fechas si se solicita (para tareas agregadas después de crear
        // el proyecto)
        if (actualizarFechas) {
            long diasAAgregar = (long) Math.ceil(duracion);
            fechaEstimadaFin = fechaEstimadaFin.plusDays(diasAAgregar);
            fechaRealFin = fechaRealFin.plusDays(diasAAgregar);
        }
    }

    /**
     * Agrega una tarea inicial al proyecto sin modificar las fechas.
     * Usado durante la construcción del proyecto.
     * 
     * @param titulo      Título de la tarea
     * @param descripcion Descripción de la tarea
     * @param duracion    Duración estimada en días
     */
    public void agregarTareaInicial(String titulo, String descripcion, double duracion) {
        agregarTarea(titulo, descripcion, duracion, false);
    }

    /**
     * Obtiene una tarea por su título.
     * 
     * @param titulo Título de la tarea
     * @return Tarea encontrada o null si no existe
     */
    public Tarea obtenerTarea(String titulo) {
        return tareas.get(titulo);
    }

    /**
     * Asigna un empleado a una tarea específica.
     * Actualiza el estado del proyecto si todas las tareas quedan asignadas.
     * 
     * @param titulo   Título de la tarea
     * @param empleado Empleado a asignar
     * @throws Exception si el proyecto está finalizado, la tarea no existe o ya
     *                   tiene responsable
     */
    public void asignarEmpleadoATarea(String titulo, Empleado empleado) throws Exception {
        if (estaFinalizado()) {
            throw new Exception("No se pueden asignar tareas en un proyecto finalizado");
        }

        Tarea tarea = tareas.get(titulo);
        if (tarea == null) {
            throw new Exception("La tarea no existe");
        }
        if (tarea.tieneResponsable()) {
            throw new Exception("La tarea ya tiene un responsable asignado");
        }

        tarea.setResponsable(empleado);
        empleado.asignar();
        empleadosAsignados.add(empleado.getLegajo());

        // Agregar al historial si no está
        if (!empleadosHistoricos.contains(empleado.getLegajo())) {
            empleadosHistoricos.add(empleado.getLegajo());
        }

        actualizarEstado();
    }

    /**
     * Reasigna un empleado a una tarea que ya tenía un responsable.
     * Libera al empleado anterior y asigna el nuevo.
     * Complejidad: O(1) gracias al uso de HashMap para tareas.
     * 
     * @param titulo        Título de la tarea
     * @param nuevoEmpleado Nuevo empleado a asignar
     * @throws Exception si el proyecto está finalizado, la tarea no existe o no
     *                   tiene responsable previo
     */
    public void reasignarEmpleadoEnTarea(String titulo, Empleado nuevoEmpleado) throws Exception {
        if (estaFinalizado()) {
            throw new Exception("No se pueden reasignar tareas en un proyecto finalizado");
        }

        Tarea tarea = tareas.get(titulo);
        if (tarea == null) {
            throw new Exception("La tarea no existe");
        }
        if (!tarea.tieneResponsable()) {
            throw new Exception("La tarea no tiene un empleado asignado previamente");
        }

        // Liberar al empleado anterior
        Empleado anteriorEmpleado = tarea.getResponsable();
        anteriorEmpleado.liberar();
        empleadosAsignados.remove(anteriorEmpleado.getLegajo());

        // Asignar el nuevo empleado
        tarea.setResponsable(nuevoEmpleado);
        nuevoEmpleado.asignar();
        empleadosAsignados.add(nuevoEmpleado.getLegajo());

        // Agregar al historial si no está
        if (!empleadosHistoricos.contains(nuevoEmpleado.getLegajo())) {
            empleadosHistoricos.add(nuevoEmpleado.getLegajo());
        }
    }

    /**
     * Registra un retraso en una tarea específica.
     * Si es el primer retraso de la tarea, incrementa el contador del empleado
     * responsable.
     * 
     * @param titulo Título de la tarea
     * @param dias   Cantidad de días de retraso
     * @throws IllegalArgumentException si el proyecto está finalizado o la tarea no
     *                                  existe
     */
    public void registrarRetraso(String titulo, double dias) {
        if (estaFinalizado()) {
            throw new IllegalArgumentException("No se pueden registrar retrasos en un proyecto finalizado");
        }

        Tarea tarea = tareas.get(titulo);
        if (tarea == null) {
            throw new IllegalArgumentException("La tarea no existe");
        }

        double retrasoAnterior = tarea.getRetraso();
        tarea.setRetraso(dias);

        // Si hay un responsable y es el primer retraso, incrementar su contador
        if (tarea.getResponsable() != null && dias > 0 && retrasoAnterior == 0) {
            tarea.getResponsable().incrementarRetrasos();
        }
    }

    /**
     * Marca una tarea como finalizada.
     * Libera al empleado asignado y lo agrega a su historial de tareas.
     * 
     * @param titulo Título de la tarea
     * @throws Exception si la tarea no existe o ya está finalizada
     */
    public void finalizarTarea(String titulo) throws Exception {
        Tarea tarea = tareas.get(titulo);
        if (tarea == null) {
            throw new Exception("La tarea no existe");
        }
        if (tarea.estaFinalizada()) {
            throw new Exception("La tarea ya está finalizada");
        }

        tarea.finalizar();

        // Liberar al empleado
        if (tarea.getResponsable() != null) {
            Empleado emp = tarea.getResponsable();
            emp.liberar();
            emp.agregarTareaRealizada(titulo);
            empleadosAsignados.remove(emp.getLegajo());
            tarea.setResponsable(null); // Quitar la referencia pero mantener el histórico
        }
    }

    /**
     * Finaliza el proyecto completo.
     * Libera todos los empleados asignados y cambia el estado a FINALIZADO.
     * 
     * @param fechaFin Fecha de finalización real (formato: yyyy-MM-dd)
     * @throws IllegalArgumentException si la fecha es anterior a la de inicio
     */
    public void finalizar(String fechaFin) {
        LocalDate fecha = LocalDate.parse(fechaFin, formatter);
        if (fecha.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de finalización no puede ser anterior a la de inicio");
        }

        this.fechaRealFin = fecha;
        this.estado = "FINALIZADO";

        // Liberar todos los empleados asignados
        for (Tarea tarea : tareas.values()) {
            if (tarea.getResponsable() != null) {
                tarea.getResponsable().liberar();
                tarea.setResponsable(null);
            }
        }
        empleadosAsignados.clear();
    }

    /**
     * Verifica si el proyecto está finalizado.
     * 
     * @return true si está finalizado, false en caso contrario
     */
    public boolean estaFinalizado() {
        return estado.equals("FINALIZADO");
    }

    /**
     * Verifica si el proyecto está pendiente.
     * 
     * @return true si está pendiente, false en caso contrario
     */
    public boolean estaPendiente() {
        return estado.equals("PENDIENTE");
    }

    /**
     * Verifica si el proyecto está activo.
     * 
     * @return true si está activo, false en caso contrario
     */
    public boolean estaActivo() {
        return estado.equals("ACTIVO");
    }

    /**
     * Actualiza el estado del proyecto según las tareas asignadas.
     * - PENDIENTE: si hay tareas sin asignar
     * - ACTIVO: si todas las tareas están asignadas
     */
    private void actualizarEstado() {
        if (estaFinalizado()) {
            return;
        }

        boolean todasAsignadas = true;
        for (Tarea tarea : tareas.values()) {
            if (!tarea.tieneResponsable() && !tarea.estaFinalizada()) {
                todasAsignadas = false;
                break;
            }
        }

        if (todasAsignadas && !tareas.isEmpty()) {
            estado = "ACTIVO";
        } else {
            estado = "PENDIENTE";
        }
    }

    /**
     * Obtiene la lista de tareas no asignadas del proyecto.
     * 
     * @return Lista de tareas sin responsable y no finalizadas
     */
    public List<Tarea> obtenerTareasNoAsignadas() {
        List<Tarea> noAsignadas = new ArrayList<>();
        for (Tarea tarea : tareas.values()) {
            if (!tarea.tieneResponsable() && !tarea.estaFinalizada()) {
                noAsignadas.add(tarea);
            }
        }
        return noAsignadas;
    }

    /**
     * Obtiene todas las tareas del proyecto.
     * 
     * @return Lista con todas las tareas
     */
    public List<Tarea> obtenerTodasLasTareas() {
        return new ArrayList<>(tareas.values());
    }

    /**
     * Obtiene el conjunto de empleados actualmente asignados.
     * 
     * @return Set con los legajos de empleados asignados
     */
    public Set<Integer> getEmpleadosAsignados() {
        return new HashSet<>(empleadosAsignados);
    }

    /**
     * Obtiene el historial completo de empleados que participaron en el proyecto.
     * 
     * @return Lista con los legajos de todos los empleados históricos
     */
    public List<Integer> getEmpleadosHistoricos() {
        return new ArrayList<>(empleadosHistoricos);
    }

    /**
     * Calcula el costo total del proyecto.
     * Fórmula: Suma de costos de tareas × 1.35 (sin retraso) o × 1.25 (con retraso)
     * 
     * Se considera que hubo retraso si:
     * 1. Alguna tarea tiene retraso registrado, O
     * 2. El proyecto finalizó después de la fecha estimada
     * 
     * @return Costo total del proyecto
     */
    public double calcularCosto() {
        double costoBase = 0;
        boolean huboRetraso = false;

        // Sumar el costo de todas las tareas
        for (Tarea tarea : tareas.values()) {
            costoBase += tarea.calcularCosto();
            if (tarea.tieneRetraso()) {
                huboRetraso = true;
            }
        }

        // También verificar si el proyecto se finalizó después de la fecha estimada
        // SOLO si el proyecto ya está finalizado
        if (estado.equals("FINALIZADO") && fechaRealFin.isAfter(fechaEstimadaFin)) {
            huboRetraso = true;
        }

        // Agregar porcentaje adicional según si hubo retrasos
        if (huboRetraso) {
            return costoBase * 1.25; // 25% adicional con retrasos
        } else {
            return costoBase * 1.35; // 35% adicional sin retrasos
        }
    }

    /*
     * Representación en String del proyecto con toda su información.
     * 
     * return String con los detalles completos del proyecto
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Proyecto #").append(id).append("\n");
        sb.append("Cliente: ").append(nombreCliente).append("\n");
        if (!emailCliente.isEmpty()) {
            sb.append("Email: ").append(emailCliente).append("\n");
        }
        if (!telefonoCliente.isEmpty()) {
            sb.append("Teléfono: ").append(telefonoCliente).append("\n");
        }
        sb.append("Dirección: ").append(direccion).append("\n");
        sb.append("Estado: ").append(estado).append("\n");
        sb.append("Fecha Inicio: ").append(fechaInicio).append("\n");
        sb.append("Fecha Estimada Fin: ").append(fechaEstimadaFin).append("\n");
        sb.append("Fecha Real Fin: ").append(fechaRealFin).append("\n");
        sb.append("Tareas:\n");
        for (Tarea tarea : tareas.values()) {
            sb.append("  - ").append(tarea.toString()).append("\n");
        }
        sb.append("Costo Total: $").append(String.format("%.2f", calcularCosto()));
        return sb.toString();
    }
}