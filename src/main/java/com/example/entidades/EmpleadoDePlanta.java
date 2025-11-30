package com.example.entidades;

/**
 * Representa un empleado de planta permanente.
 * Este tipo de empleado cobra por día trabajado y tiene una categoría.
 * Si trabaja medio día o menos, se cuenta como día completo.
 * Recibe un 2% adicional si completa la tarea sin retrasos.
 */
public class EmpleadoDePlanta extends Empleado {
    private double valorDia;
    private String categoria;

    // Categorías válidas
    public static final String CATEGORIA_INICIAL = "INICIAL";
    public static final String CATEGORIA_TECNICO = "TÉCNICO";
    public static final String CATEGORIA_EXPERTO = "EXPERTO";

    /**
     * Constructor del EmpleadoDePlanta.
     * 
     * @param nombre Nombre del empleado
     * @param valorDia Valor que cobra por día trabajado
     * @param categoria Categoría del empleado (INICIAL, TÉCNICO o EXPERTO)
     * @throws IllegalArgumentException si el nombre es inválido, el valorDia es negativo 
     *         o la categoría no es válida
     */
    public EmpleadoDePlanta(String nombre, double valorDia, String categoria) {
        super(nombre);
        if (valorDia < 0) {
            throw new IllegalArgumentException("El valor día no puede ser negativo");
        }
        if (!esCategoriaValida(categoria)) {
            throw new IllegalArgumentException("Categoría inválida. Debe ser INICIAL, TÉCNICO o EXPERTO");
        }
        this.valorDia = valorDia;
        this.categoria = categoria.toUpperCase();
    }

    /**
     * Valida si una categoría es válida.
     * 
     * @param categoria Categoría a validar
     * @return true si es válida, false en caso contrario
     */
    private boolean esCategoriaValida(String categoria) {
        if (categoria == null) return false;
        String cat = categoria.toUpperCase();
        return cat.equals(CATEGORIA_INICIAL) || 
               cat.equals(CATEGORIA_TECNICO) || 
               cat.equals(CATEGORIA_EXPERTO);
    }

    /**
     * Obtiene el valor por día del empleado.
     * @return Valor día
     */
    public double getValorDia() {
        return valorDia;
    }

    /**
     * Establece un nuevo valor por día.
     * @param valorDia Nuevo valor día
     * @throws IllegalArgumentException si el valor es negativo
     */
    public void setValorDia(double valorDia) {
        if (valorDia < 0) {
            throw new IllegalArgumentException("El valor día no puede ser negativo");
        }
        this.valorDia = valorDia;
    }

    /**
     * Obtiene la categoría del empleado.
     * @return Categoría (INICIAL, TÉCNICO o EXPERTO)
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Establece una nueva categoría.
     * @param categoria Nueva categoría
     * @throws IllegalArgumentException si la categoría no es válida
     */
    public void setCategoria(String categoria) {
        if (!esCategoriaValida(categoria)) {
            throw new IllegalArgumentException("Categoría inválida. Debe ser INICIAL, TÉCNICO o EXPERTO");
        }
        this.categoria = categoria.toUpperCase();
    }

    /**
     * Calcula el costo de una tarea para un empleado de planta.
     * 
     * Características especiales:
     * 1. Si la tarea dura medio día (0.5) o menos, se cuenta como día completo.
     *    Ejemplo: 0.5 días → se cobra 1 día completo
     *             0.7 días → se cobra 1 día completo
     *             1.3 días → se cobran 2 días completos
     * 
     * 2. Si la tarea NO tuvo retrasos, recibe un 2% adicional sobre el costo base.
     * 
     * Fórmula base: días trabajados (redondeados hacia arriba) × valorDia
     * Si no hubo retraso: costo base × 1.02
     * 
     * @param duracionDias Duración de la tarea en días (puede incluir retrasos)
     * @param conRetraso Indica si la tarea tuvo retraso
     * @return Costo calculado de la tarea
     */
    @Override
    public double calcularCostoTarea(double duracionDias, boolean conRetraso) {
        // Si es medio día o menos, se cuenta como día completo
        // Math.ceil redondea hacia arriba: 0.5 → 1, 1.3 → 2, 2.0 → 2
        double diasTrabajados = Math.ceil(duracionDias);
        
        // Calcular costo base
        double costo = valorDia * diasTrabajados;
        
        // Si no hubo retraso, recibe 2% adicional
        if (!conRetraso) {
            costo *= 1.02; // Incrementa un 2%
        }
        
        return costo;
    }

    /**
     * Verifica si el empleado es de categoría EXPERTO.
     * @return true si es EXPERTO
     */
    public boolean esExperto() {
        return categoria.equals(CATEGORIA_EXPERTO);
    }

    /**
     * Verifica si el empleado es de categoría TÉCNICO.
     * @return true si es TÉCNICO
     */
    public boolean esTecnico() {
        return categoria.equals(CATEGORIA_TECNICO);
    }

    /**
     * Verifica si el empleado es de categoría INICIAL.
     * @return true si es INICIAL
     */
    public boolean esInicial() {
        return categoria.equals(CATEGORIA_INICIAL);
    }

    /**
     * Representación en String del empleado de planta con su legajo.
     * @return String con el número de legajo
     */
    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * Representación detallada del empleado de planta.
     * Incluye información específica como valor día y categoría.
     * 
     * @return String con información completa del empleado
     */
    public String toStringDetallado() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== EMPLEADO DE PLANTA ===\n");
        sb.append(super.toStringDetallado()).append("\n");
        sb.append("Valor día: $").append(String.format("%.2f", valorDia)).append("\n");
        sb.append("Categoría: ").append(categoria).append("\n");
        sb.append("Tipo: Planta Permanente");
        return sb.toString();
    }

    /**
     * Calcula el costo estimado por una cantidad de días de trabajo sin retrasos.
     * Útil para presupuestos antes de asignar tareas.
     * 
     * @param dias Cantidad de días a calcular
     * @return Costo estimado (incluye el 2% adicional por trabajar sin retrasos)
     */
    public double calcularCostoEstimadoSinRetraso(double dias) {
        return calcularCostoTarea(dias, false);
    }

    /**
     * Calcula el costo estimado por una cantidad de días de trabajo con retrasos.
     * 
     * @param dias Cantidad de días a calcular
     * @return Costo estimado (sin el 2% adicional)
     */
    public double calcularCostoEstimadoConRetraso(double dias) {
        return calcularCostoTarea(dias, true);
    }

    /**
     * Calcula cuántos días completos se cobran por una duración dada.
     * Aplica el redondeo hacia arriba característico de empleados de planta.
     * 
     * @param duracionDias Duración en días
     * @return Días completos que se cobrarán
     */
    public int calcularDiasCobrados(double duracionDias) {
        return (int) Math.ceil(duracionDias);
    }

    /**
     * Calcula el bono del 2% que recibiría por completar una tarea sin retrasos.
     * 
     * @param duracionDias Duración de la tarea
     * @return Monto del bono (2% del costo base)
     */
    public double calcularBonoPuntualidad(double duracionDias) {
        double diasTrabajados = Math.ceil(duracionDias);
        double costoBase = valorDia * diasTrabajados;
        return costoBase * 0.02; // 2% del costo base
    }
}