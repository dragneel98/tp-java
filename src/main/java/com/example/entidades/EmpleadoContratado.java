package com.example.entidades;

/**
 * Representa un empleado contratado por horas.
 * Este tipo de empleado cobra según las horas trabajadas.
 * Cada día de trabajo equivale a 8 horas.
 */
public class EmpleadoContratado extends Empleado {
    private double valorHora;

    /**
     * Constructor del EmpleadoContratado.
     * 
     * @param nombre Nombre del empleado
     * @param valorHora Valor que cobra por hora trabajada
     * @throws IllegalArgumentException si el nombre es inválido o el valorHora es negativo
     */
    public EmpleadoContratado(String nombre, double valorHora) {
        super(nombre);
        if (valorHora < 0) {
            throw new IllegalArgumentException("El valor hora no puede ser negativo");
        }
        this.valorHora = valorHora;
    }

    /**
     * Obtiene el valor por hora del empleado.
     * @return Valor hora
     */
    public double getValorHora() {
        return valorHora;
    }

    /**
     * Establece un nuevo valor por hora.
     * @param valorHora Nuevo valor hora
     * @throws IllegalArgumentException si el valor es negativo
     */
    public void setValorHora(double valorHora) {
        if (valorHora < 0) {
            throw new IllegalArgumentException("El valor hora no puede ser negativo");
        }
        this.valorHora = valorHora;
    }

    /**
     * Calcula el costo de una tarea para un empleado contratado.
     * 
     * Fórmula: horas × valorHora
     * Donde: horas = duracionDias × 8 (cada día son 8 horas de trabajo)
     * 
     * Nota: El parámetro conRetraso no afecta el cálculo para empleados contratados,
     * ya que cobran lo mismo sin importar si hay retrasos.
     * 
     * @param duracionDias Duración de la tarea en días (puede incluir retrasos)
     * @param conRetraso Indica si la tarea tuvo retraso (no afecta el cálculo)
     * @return Costo calculado de la tarea
     */
    @Override
    public double calcularCostoTarea(double duracionDias, boolean conRetraso) {
        // Convertir días a horas (cada día = 8 horas)
        double horas = duracionDias * 8;
        
        // Calcular costo: horas × valorHora
        return valorHora * horas;
    }

    /**
     * Representación en String del empleado contratado con su legajo.
     * @return String con el número de legajo
     */
    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * Representación detallada del empleado contratado.
     * Incluye información específica como el valor hora.
     * 
     * @return String con información completa del empleado
     */
    public String toStringDetallado() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== EMPLEADO CONTRATADO ===\n");
        sb.append(super.toStringDetallado()).append("\n");
        sb.append("Valor hora: $").append(String.format("%.2f", valorHora)).append("\n");
        sb.append("Tipo: Contratado");
        return sb.toString();
    }

    /**
     * Calcula el costo estimado por una cantidad de días de trabajo.
     * Útil para presupuestos antes de asignar tareas.
     * 
     * @param dias Cantidad de días a calcular
     * @return Costo estimado
     */
    public double calcularCostoEstimado(double dias) {
        return calcularCostoTarea(dias, false);
    }

    /**
     * Calcula cuántas horas de trabajo representa una tarea.
     * 
     * @param duracionDias Duración en días
     * @return Cantidad de horas
     */
    public double calcularHorasTrabajo(double duracionDias) {
        return duracionDias * 8;
    }
}
