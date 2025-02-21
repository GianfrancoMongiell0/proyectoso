package planificacion;

import Clases.Proceso;
import Estructuras.Queue;

public class SJF extends Planificador {

    public SJF() {
        // No se necesita inicialización específica para SJF en este momento, se usa el constructor de Planificador
    }

    @Override
    public Proceso siguienteProceso() {
        try {
            mutex.acquire();
            if (colaListos.isEmpty()) {
                return null; // No hay procesos en la cola de listos
            }

            Proceso mejorProcesoSJF = null;
            Queue<Proceso> colaAuxiliarSJF = new Queue<>();

            // Inicializar mejorProcesoSJF con el primer proceso de la cola
            if (!colaListos.isEmpty()) {
                mejorProcesoSJF = colaListos.dequeue();
            } else {
                return null; // La cola estaba vacía
            }

            int lengthColaListos = colaListos.getLength();
            for (int i = 0; i < lengthColaListos; i++) {
                Proceso procesoActual = colaListos.dequeue();

                // Comparar por tiempo restante (SJF no preemptivo usa tiempo total inicialmente estimado, aquí usamos restante para consistencia con SRT)
                if (procesoActual.getInstruccionesRestantes() < mejorProcesoSJF.getInstruccionesRestantes()) {
                    colaAuxiliarSJF.enqueue(mejorProcesoSJF); // Re-encolar el anterior 'mejor' proceso
                    mejorProcesoSJF = procesoActual; // El actual es ahora el mejor (más corto)
                } else {
                    colaAuxiliarSJF.enqueue(procesoActual); // Re-encolar el proceso actual (no es más corto)
                }
            }

            // Re-encolar todos los procesos de la cola auxiliar DE VUELTA a la cola de listos
            int lengthColaAuxiliar = colaAuxiliarSJF.getLength(); // Recalcular longitud por seguridad
            for (int i = 0; i < lengthColaAuxiliar; i++) {
                colaListos.enqueue(colaAuxiliarSJF.dequeue());
            }

            return mejorProcesoSJF; // Retornar el proceso SJF (Shortest Job First)

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            mutex.release(); // Liberar el mutex
        }
    }

    @Override
    public void agregarProceso(Proceso p) {
        try {
            mutex.acquire();
            colaListos.enqueue(p); // Añadir proceso a la cola de listos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            mutex.release(); // Liberar el mutex
        }
    }

    @Override
    public boolean estaVacio() {
        return colaListos.isEmpty();
    }

    // reordenarCola() se hereda de Planificador y tiene una implementación por defecto vacía,
    // SJF NO necesita una implementación específica de reordenarCola() en este diseño no preemptivo.
}
