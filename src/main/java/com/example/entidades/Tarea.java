package com.example.entidades;

/**
 * Representa una tarea dentro de un proyecto.
 * Una tarea tiene un responsable (Empleado), puede tener retrasos y estados
 * (finalizada/pendiente).
 * Mantiene un historial del responsable para el cálculo de costos incluso
 * después de finalizada.
 */
public class Tarea {
    private String titulo;
    private String descripcion;
    private double duracionEstimada;
    private double retraso;
    private Empleado responsable;
    private boolean finalizada;
    private Empleado responsableHistorico; // Para mantener el historial del responsable original

    /**
     * Constructor de la Tarea.
     * 
     * @param titulo           Título de la tarea
     * @param descripcion      Descripción detallada de la tarea
     * @param duracionEstimada Duración estimada en días (mínimo 0.5 días = medio
     *                         día)
     * @throws IllegalArgumentException si la duración es menor a 0.5 días
     */
    public Tarea(String titulo, String descripcion, double duracionEstimada) {
        if (duracionEstimada < 0.5) {
            throw new IllegalArgumentException("La duración mínima es medio día (0.5)");
        }
        if (titulo == null || titulo.isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }

        this.titulo = titulo;
        this.descripcion = descripcion;
        this.duracionEstimada = duracionEstimada;
        this.retraso = 0;
        this.responsable = null;
        this.finalizada = false;
        this.responsableHistorico = null;
    }

    /**
     * Obtiene el título de la tarea.
     * 
     * @return Título
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Obtiene la descripción de la tarea.
     * 
     * @return Descripción
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Obtiene la duración estimada inicial de la tarea.
     * 
     * @return Duración en días
     */
    public double getDuracionEstimada() {
        return duracionEstimada;
    }

    /**
     * Obtiene el retraso acumulado de la tarea.
     * 
     * @return Días de retraso
     */
    public double getRetraso() {
        return retraso;
    }

    /**
     * Establece el retraso de la tarea.
     * 
     * @param retraso Cantidad de días de retraso
     * @throws IllegalArgumentException si el retraso es negativo
     */
    public void setRetraso(double retraso) {
        if (retraso < 0) {
            throw new IllegalArgumentException("El retraso no puede ser negativo");
        }
        this.retraso = retraso;
    }

    /**
     * Obtiene el empleado responsable actual de la tarea.
     * 
     * @return Empleado responsable o null si no está asignada
     */
    public Empleado getResponsable() {
        return responsable;
    }

    /**
     * Asigna un empleado como responsable de la tarea.
     * También guarda el responsable en el historial para cálculos futuros.
     * 
     * @param empleado Empleado a asignar
     */
    public void setResponsable(Empleado empleado) {
        this.responsable = empleado;
        // Si se asigna un empleado, guardarlo en el historial
        if (empleado != null) {
            this.responsableHistorico = empleado;
        }
    }

    /**
     * Obtiene el responsable histórico de la tarea.
     * Útil para calcular costos después de que la tarea fue finalizada.
     * 
     * @return Empleado del historial o el responsable actual si no hay historial
     */
    public Empleado getResponsableHistorico() {
        return responsableHistorico != null ? responsableHistorico : responsable;
    }

    /**
     * Verifica si la tarea está finalizada.
     * 
     * @return true si está finalizada, false en caso contrario
     */
    public boolean estaFinalizada() {
        return finalizada;
    }

    /**
     * Marca la tarea como finalizada.
     * Una vez finalizada, el empleado es liberado pero se mantiene en el historial.
     */
    public void finalizar() {
        this.finalizada = true;
    }

    /**
     * Verifica si la tarea tiene un responsable asignado actualmente.
     * 
     * @return true si tiene responsable, false en caso contrario
     */
    public boolean tieneResponsable() {
        return responsable != null;
    }

    /**
     * Calcula la duración real de la tarea.
     * Duración real = Duración estimada + Retrasos
     * 
     * @return Duración real en días
     */
    public double duracionReal() {
        return duracionEstimada + retraso;
    }

    /**
     * Verifica si la tarea tuvo retrasos.
     * 
     * @return true si hubo retrasos (retraso > 0), false en caso contrario
     */
    public boolean tieneRetraso() {
        return retraso > 0;
    }

    /**
     * Calcula el costo de la tarea según el empleado responsable.
     * 
     * El cálculo depende del tipo de empleado:
     * - EmpleadoContratado: horas × valorHora (1 día = 8 horas)
     * - EmpleadoDePlanta: días × valorDía (+ 2% si no hubo retraso)
     * 
     * @return Costo calculado de la tarea, 0 si no tiene responsable
     */
    public double calcularCosto() {
        // Usar el responsable histórico para calcular el costo
        if (responsableHistorico == null) {
            return 0;
        }
        return responsableHistorico.calcularCostoTarea(duracionReal(), tieneRetraso());
    }

    /**
     * Representación en String de la tarea con información básica.
     * 
     * @return String con título, responsable y estado
     */
    @Override
    public String toString() {
        String resp = responsable != null ? responsable.getNombre()
                : (responsableHistorico != null ? responsableHistorico.getNombre() + " (histórico)" : "Sin asignar");
        String estadoStr = finalizada ? "Finalizada" : "Pendiente";
        return titulo + " - " + resp + " - " + estadoStr;
    }

    /**
     * Representación detallada de la tarea con toda la información.
     * 
     * @return String con todos los detalles de la tarea
     */
    public String toStringDetallado() {
        StringBuilder sb = new StringBuilder();
        sb.append("Título: ").append(titulo).append("\n");
        sb.append("Descripción: ").append(descripcion).append("\n");
        sb.append("Duración estimada: ").append(duracionEstimada).append(" días\n");
        sb.append("Retraso: ").append(retraso).append(" días\n");
        sb.append("Duración real: ").append(duracionReal()).append(" días\n");

        if (responsable != null) {
            sb.append("Responsable actual: ").append(responsable.getNombre()).append(" (Legajo: ")
                    .append(responsable.getLegajo()).append(")\n");
        } else if (responsableHistorico != null) {
            sb.append("Responsable histórico: ").append(responsableHistorico.getNombre()).append(" (Legajo: ")
                    .append(responsableHistorico.getLegajo()).append(")\n");
        } else {
            sb.append("Responsable: Sin asignar\n");
        }

        sb.append("Estado: ").append(finalizada ? "Finalizada" : "Pendiente").append("\n");
        sb.append("Costo: $").append(String.format("%.2f", calcularCosto()));

        return sb.toString();
    }

    /**
     * Verifica si dos tareas son iguales comparando sus títulos.
     * 
     * @param obj Objeto a comparar
     * @return true si son la misma tarea (mismo título)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Tarea tarea = (Tarea) obj;
        return titulo.equals(tarea.titulo);
    }

    /**
     * Genera el hash code basado en el título de la tarea.
     * 
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return titulo.hashCode();
    }
}