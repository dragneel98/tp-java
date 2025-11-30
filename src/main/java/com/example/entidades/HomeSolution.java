package com.example.entidades;

import java.util.*;

/**
 * Implementación principal del sistema HomeSolution.
 */
public class HomeSolution implements IHomeSolution {
    private HashMap<Integer, Empleado> empleados;
    private HashMap<Integer, Proyecto> proyectos;

    public HomeSolution() {
        this.empleados = new HashMap<>();
        this.proyectos = new HashMap<>();
        // Resetear contadores para asegurar IDs consistentes en el testing
        Empleado.resetearContador();
        Proyecto.resetearContador();
    }

    // ============================================================
    // REGISTRO DE EMPLEADOS
    // ============================================================

    @Override
    public void registrarEmpleado(String nombre, double valor) throws IllegalArgumentException {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (valor < 0) {
            throw new IllegalArgumentException("El valor no puede ser negativo");
        }

        Empleado empleado = new EmpleadoContratado(nombre, valor);
        empleados.put(empleado.getLegajo(), empleado);
    }

    @Override
    public void registrarEmpleado(String nombre, double valor, String categoria) throws IllegalArgumentException {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (valor < 0) {
            throw new IllegalArgumentException("El valor no puede ser negativo");
        }

        Empleado empleado = new EmpleadoDePlanta(nombre, valor, categoria);
        empleados.put(empleado.getLegajo(), empleado);
    }

    // ============================================================
    // REGISTRO Y GESTIÓN DE PROYECTOS
    // ============================================================

    @Override
    public void registrarProyecto(String[] titulos, String[] descripcion, double[] dias,
            String domicilio, String[] cliente, String inicio, String fin)
            throws IllegalArgumentException {

        if (titulos == null || descripcion == null || dias == null ||
                titulos.length == 0 || titulos.length != descripcion.length ||
                titulos.length != dias.length) {
            throw new IllegalArgumentException("Los arrays de tareas deben tener el mismo tamaño y no estar vacíos");
        }

        Proyecto proyecto = new Proyecto(cliente, domicilio, inicio, fin);

        // Agregar tareas iniciales sin modificar las fechas del proyecto
        for (int i = 0; i < titulos.length; i++) {
            proyecto.agregarTareaInicial(titulos[i], descripcion[i], dias[i]);
        }

        proyectos.put(proyecto.getId(), proyecto);
    }

    // ============================================================
    // ASIGNACIÓN Y GESTIÓN DE TAREAS
    // ============================================================

    @Override
    public void asignarResponsableEnTarea(Integer numero, String titulo) throws Exception {
        Proyecto proyecto = proyectos.get(numero);
        if (proyecto == null) {
            throw new Exception("El proyecto no existe");
        }
        if (proyecto.estaFinalizado()) {
            throw new Exception("No se pueden asignar tareas en un proyecto finalizado");
        }

        Empleado empleado = buscarPrimerEmpleadoDisponible();
        if (empleado == null) {
            throw new Exception("No hay empleados disponibles");
        }

        proyecto.asignarEmpleadoATarea(titulo, empleado);
    }

    @Override
    public void asignarResponsableMenosRetraso(Integer numero, String titulo) throws Exception {
        Proyecto proyecto = proyectos.get(numero);
        if (proyecto == null) {
            throw new Exception("El proyecto no existe");
        }
        if (proyecto.estaFinalizado()) {
            throw new Exception("No se pueden asignar tareas en un proyecto finalizado");
        }

        Empleado empleado = buscarEmpleadoConMenosRetrasos();
        if (empleado == null) {
            throw new Exception("No hay empleados disponibles");
        }

        proyecto.asignarEmpleadoATarea(titulo, empleado);
    }

    @Override
    public void registrarRetrasoEnTarea(Integer numero, String titulo, double cantidadDias)
            throws IllegalArgumentException {
        Proyecto proyecto = proyectos.get(numero);
        if (proyecto == null) {
            throw new IllegalArgumentException("El proyecto no existe");
        }

        proyecto.registrarRetraso(titulo, cantidadDias);
    }

    @Override
    public void agregarTareaEnProyecto(Integer numero, String titulo, String descripcion, double dias)
            throws IllegalArgumentException {
        Proyecto proyecto = proyectos.get(numero);
        if (proyecto == null) {
            throw new IllegalArgumentException("El proyecto no existe");
        }

        proyecto.agregarTarea(titulo, descripcion, dias);
    }

    @Override
    public void finalizarTarea(Integer numero, String titulo) throws Exception {
        Proyecto proyecto = proyectos.get(numero);
        if (proyecto == null) {
            throw new Exception("El proyecto no existe");
        }

        proyecto.finalizarTarea(titulo);
    }

    @Override
    public void finalizarProyecto(Integer numero, String fin) throws IllegalArgumentException {
        Proyecto proyecto = proyectos.get(numero);
        if (proyecto == null) {
            throw new IllegalArgumentException("El proyecto no existe");
        }

        // Verificar que no haya tareas sin asignar
        List<Tarea> tareasNoAsignadas = proyecto.obtenerTareasNoAsignadas();
        if (!tareasNoAsignadas.isEmpty()) {
            throw new IllegalArgumentException("No se puede finalizar el proyecto con tareas sin asignar");
        }

        proyecto.finalizar(fin);
    }

    // ============================================================
    // REASIGNACIÓN DE EMPLEADOS
    // ============================================================

    @Override
    public void reasignarEmpleadoEnProyecto(Integer numero, Integer legajo, String titulo) throws Exception {
        Proyecto proyecto = proyectos.get(numero);
        if (proyecto == null) {
            throw new Exception("El proyecto no existe");
        }

        Empleado empleado = empleados.get(legajo);
        if (empleado == null) {
            throw new Exception("El empleado no existe");
        }
        if (!empleado.estaDisponible()) {
            throw new Exception("El empleado no está disponible");
        }

        proyecto.reasignarEmpleadoEnTarea(titulo, empleado);
    }

    @Override
    public void reasignarEmpleadoConMenosRetraso(Integer numero, String titulo) throws Exception {
        Proyecto proyecto = proyectos.get(numero);
        if (proyecto == null) {
            throw new Exception("El proyecto no existe");
        }

        Empleado empleado = buscarEmpleadoConMenosRetrasos();
        if (empleado == null) {
            throw new Exception("No hay empleados disponibles");
        }

        proyecto.reasignarEmpleadoEnTarea(titulo, empleado);
    }

    // ============================================================
    // CONSULTAS Y REPORTES
    // ============================================================

    @Override
    public double costoProyecto(Integer numero) {
        Proyecto proyecto = proyectos.get(numero);
        if (proyecto == null) {
            return 0;
        }
        double costo = proyecto.calcularCosto();
        System.out.println("Proyecto #" + numero);
        System.out.println("Estado: " + proyecto.getEstado());
        System.out.println("Fecha estimada: " + proyecto.getFechaEstimadaFin());
        System.out.println("Fecha real: " + proyecto.getFechaRealFin());
        System.out.println("Costo calculado: " + costo);
        return costo;
        // return proyecto.calcularCosto();
    }

    @Override
    public List<Tupla<Integer, String>> proyectosFinalizados() {
        List<Tupla<Integer, String>> resultado = new ArrayList<>();
        for (Proyecto proyecto : proyectos.values()) {
            if (proyecto.estaFinalizado()) {
                resultado.add(new Tupla<>(proyecto.getId(), proyecto.getDireccion()));
            }
        }
        return resultado;
    }

    @Override
    public List<Tupla<Integer, String>> proyectosPendientes() {
        List<Tupla<Integer, String>> resultado = new ArrayList<>();
        for (Proyecto proyecto : proyectos.values()) {
            if (proyecto.estaPendiente()) {
                resultado.add(new Tupla<>(proyecto.getId(), proyecto.getDireccion()));
            }
        }
        return resultado;
    }

    @Override
    public List<Tupla<Integer, String>> proyectosActivos() {
        List<Tupla<Integer, String>> resultado = new ArrayList<>();
        for (Proyecto proyecto : proyectos.values()) {
            if (proyecto.estaActivo()) {
                resultado.add(new Tupla<>(proyecto.getId(), proyecto.getDireccion()));
            }
        }
        return resultado;
    }

    @Override
    public Object[] empleadosNoAsignados() {
        List<Integer> resultado = new ArrayList<>();
        for (Empleado empleado : empleados.values()) {
            if (empleado.estaDisponible()) {
                resultado.add(empleado.getLegajo());
            }
        }
        return resultado.toArray();
    }

    @Override
    public boolean estaFinalizado(Integer numero) {
        Proyecto proyecto = proyectos.get(numero);
        if (proyecto == null) {
            return false;
        }
        return proyecto.estaFinalizado();
    }

    @Override
    public int consultarCantidadRetrasosEmpleado(Integer legajo) {
        Empleado empleado = empleados.get(legajo);
        if (empleado == null) {
            return 0;
        }
        return empleado.getCantRetrasos();
    }

    @Override
    public List<Tupla<Integer, String>> empleadosAsignadosAProyecto(Integer numero) {
        Proyecto proyecto = proyectos.get(numero);
        List<Tupla<Integer, String>> resultado = new ArrayList<>();

        if (proyecto == null) {
            return resultado;
        }

        // Obtener empleados del historial o asignados
        List<Integer> legajos = proyecto.getEmpleadosHistoricos();
        for (Integer legajo : legajos) {
            Empleado empleado = empleados.get(legajo);
            if (empleado != null) {
                resultado.add(new Tupla<>(empleado.getLegajo(), empleado.getNombre()));
            }
        }

        return resultado;
    }

    @Override
    public Object[] tareasProyectoNoAsignadas(Integer numero) {
        Proyecto proyecto = proyectos.get(numero);
        if (proyecto == null || proyecto.estaFinalizado()) {
            throw new IllegalArgumentException("El proyecto no existe o está finalizado");
        }

        List<Tarea> tareasNoAsignadas = proyecto.obtenerTareasNoAsignadas();
        Object[] resultado = new Object[tareasNoAsignadas.size()];
        for (int i = 0; i < tareasNoAsignadas.size(); i++) {
            resultado[i] = tareasNoAsignadas.get(i).getTitulo();
        }
        return resultado;
    }

    @Override
    public Object[] tareasDeUnProyecto(Integer numero) {
        Proyecto proyecto = proyectos.get(numero);
        if (proyecto == null) {
            return new Object[0];
        }

        List<Tarea> todasLasTareas = proyecto.obtenerTodasLasTareas();
        Object[] resultado = new Object[todasLasTareas.size()];
        for (int i = 0; i < todasLasTareas.size(); i++) {
            resultado[i] = todasLasTareas.get(i).getTitulo();
        }
        return resultado;
    }

    @Override
    public String consultarDomicilioProyecto(Integer numero) {
        Proyecto proyecto = proyectos.get(numero);
        if (proyecto == null) {
            return "";
        }
        return proyecto.getDireccion();
    }

    @Override
    public boolean tieneRestrasos(Integer legajo) {
        Empleado empleado = empleados.get(legajo);
        if (empleado == null) {
            return false;
        }
        return empleado.getCantRetrasos() > 0;
    }

    @Override
    public List<Tupla<Integer, String>> empleados() {
        List<Tupla<Integer, String>> resultado = new ArrayList<>();
        for (Empleado empleado : empleados.values()) {
            resultado.add(new Tupla<>(empleado.getLegajo(), empleado.getNombre()));
        }
        return resultado;
    }

    @Override
    public String consultarProyecto(Integer numero) {
        Proyecto proyecto = proyectos.get(numero);
        if (proyecto == null) {
            return "Proyecto no encontrado";
        }
        return proyecto.toString();
    }

    // ============================================================
    // MÉTODOS AUXILIARES
    // ============================================================

    private Empleado buscarPrimerEmpleadoDisponible() {
        for (Empleado empleado : empleados.values()) {
            if (empleado.estaDisponible()) {
                return empleado;
            }
        }
        return null;
    }

    private Empleado buscarEmpleadoConMenosRetrasos() {
        Empleado mejorEmpleado = null;
        int menorRetraso = Integer.MAX_VALUE;

        for (Empleado empleado : empleados.values()) {
            if (empleado.estaDisponible()) {
                if (empleado.getCantRetrasos() < menorRetraso) {
                    menorRetraso = empleado.getCantRetrasos();
                    mejorEmpleado = empleado;
                }
            }
        }

        return mejorEmpleado;
    }
}
