package Clases;

public class Proceso {

    private final PCB pcb;
    private final int totalInstrucciones;
    private int instruccionesRestantes;
    private volatile boolean terminado = false;

    // Constructor para procesos CPU-bound
    public Proceso(String nombre, int totalInstrucciones) {
        this.pcb = new PCB(nombre);
        this.totalInstrucciones = totalInstrucciones;
        this.instruccionesRestantes = totalInstrucciones;
    }

    // Constructor para procesos I/O-bound
    public Proceso(String nombre, int totalInstrucciones,
            int ciclosExcepcion, int ciclosCompletarExcepcion) {
        this.pcb = new PCB(nombre, ciclosExcepcion, ciclosCompletarExcepcion);
        this.totalInstrucciones = totalInstrucciones;
        this.instruccionesRestantes = totalInstrucciones;
    }

    /**
     * Ejecuta una instrucción del proceso. Método sincronizado para evitar
     * condiciones de carrera.
     */
    public synchronized void ejecutarInstruccion() {
        if (!terminado) {
            pcb.incrementarPc();
            pcb.incrementarMar();
            instruccionesRestantes--;

            if (instruccionesRestantes <= 0) {
                pcb.setEstado(PCB.Estado.TERMINATED);
                terminado = true;
            }
        }
    }

    public int getInstruccionesEjecutadas() {
        return totalInstrucciones - instruccionesRestantes;
    }

    /**
     * Determina si el proceso debe bloquearse por una operación de E/S.
     *
     * @return true si debe bloquearse, false en caso contrario.
     */
    public boolean debeBloquearse() {
        return pcb.debeBloquearse();
    }

    public PCB getPCB() {
        return pcb;
    }

    public void setInstruccionesRestantes(int instruccionesRestantes) {
        this.instruccionesRestantes = instruccionesRestantes;
    }

    public int getTiempoRestante() {
        return totalInstrucciones - getInstruccionesEjecutadas();
    }

    public int getTotalInstrucciones() {
        return totalInstrucciones;
    }

    public int getInstruccionesRestantes() {
        return instruccionesRestantes;
    }

    public boolean estaTerminado() {
        return terminado;
    }

    
    public String getNombre() {
        return pcb.getNombre();
    }

    @Override
    public String toString() {
        return String.format(
                "%s | Instrucciones restantes: %d",
                pcb.toString(), instruccionesRestantes
        );
    }
}
