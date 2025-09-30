package busquedaheuristica;

import java.util.*;

public class BusquedaHeuristicaPrimeroElMejor {
    
    static class Nodo implements Comparable<Nodo> {
        double posicion;
        String nombre;
        Nodo padre;
        String direccion;
        double valorHeuristico;  // Distancia al plano patrón
        
        public Nodo(double posicion, String nombre, Nodo padre, String direccion, double valorHeuristico) {
            this.posicion = posicion;
            this.nombre = nombre;
            this.padre = padre;
            this.direccion = direccion;
            this.valorHeuristico = valorHeuristico;
        }
        
        public int compareTo(Nodo otro) {
            return Double.compare(this.valorHeuristico, otro.valorHeuristico);
        }
        
        public String toString() {
            return String.format("%s(%.2f mm, h=%.2f)", nombre, posicion, valorHeuristico);
        }
    }
    
    static class RobotMontajeHeuristico {
        private double posicionObjetivo;
        private double posicionInicial;
        private double incremento;
        private int movimientosRealizados;
        private PriorityQueue<Nodo> colaPrioridad;
        private Set<Double> visitados;
        private List<Nodo> ordenExploracion;
        private double radioAnillo = 3.0;  // Radio del anill
        
        public RobotMontajeHeuristico(double posicionInicial, double posicionObjetivo, double incremento) {
            this.posicionInicial = posicionInicial;
            this.posicionObjetivo = posicionObjetivo;
            this.incremento = incremento;
            this.movimientosRealizados = 0;
            this.colaPrioridad = new PriorityQueue<>();
            this.visitados = new HashSet<>();
            this.ordenExploracion = new ArrayList<>();
        }
        
        // función heurística
        private double calcularHeuristica(double posicion) {
            double distanciaAlCentro = Math.abs(posicion - posicionObjetivo);
            
            // Dentro del anillo: valor heurístico bajo (mejor)
            if (distanciaAlCentro <= radioAnillo) {
                return distanciaAlCentro;
            }
            // Fuera del anillo: valor heurístico alto (peor)
            return distanciaAlCentro + 10;
        }
        
        private boolean palpar(double posicion) {
            movimientosRealizados++;
            return Math.abs(posicion - posicionObjetivo) < 0.5;
        }
        
        private List<Nodo> generarHijos(Nodo nodoActual) {
            List<Nodo> hijos = new ArrayList<>();
            
            double posicionDerecha = nodoActual.posicion + incremento;
            if (!visitados.contains(posicionDerecha) && posicionDerecha <= 150) {
                double heuristicaDer = calcularHeuristica(posicionDerecha);
                String nombreDer = "B" + (ordenExploracion.size() + 1);
                hijos.add(new Nodo(posicionDerecha, nombreDer, nodoActual, 
                                  "DERECHA", heuristicaDer));
            }
            
            double posicionIzquierda = nodoActual.posicion - incremento;
            if (!visitados.contains(posicionIzquierda) && posicionIzquierda >= 50) {
                double heuristicaIzq = calcularHeuristica(posicionIzquierda);
                String nombreIzq = "B" + (ordenExploracion.size() + 1);
                hijos.add(new Nodo(posicionIzquierda, nombreIzq, nodoActual, 
                                  "IZQUIERDA", heuristicaIzq));
            }
            
            return hijos;
        }
        
        public Nodo busquedaPrimeroElMejor() {
            System.out.println("=== BÚSQUEDA HEURÍSTICA PRIMERO EL MEJOR - ROBOT DE MONTAJE ===");
            System.out.println("Posición inicial B: " + posicionInicial + " mm");
            System.out.println("Posición objetivo A: " + posicionObjetivo + " mm");
            System.out.println("Incremento ΔH: " + incremento + " mm");
            System.out.println("Método: Guiado por relieve del block\n");
            

            double heuristicaInicial = calcularHeuristica(posicionInicial);
            Nodo raiz = new Nodo(posicionInicial, "B", null, "INICIO", heuristicaInicial);
            colaPrioridad.add(raiz);
            
            while (!colaPrioridad.isEmpty()) {
                Nodo nodoActual = colaPrioridad.poll();
                
                if (visitados.contains(nodoActual.posicion)) {
                    continue;
                }
                
                visitados.add(nodoActual.posicion);
                ordenExploracion.add(nodoActual);
                
                System.out.printf("Explorando %s en posición %.2f mm (h=%.2f)... ", 
                                 nodoActual.nombre, nodoActual.posicion, nodoActual.valorHeuristico);
                
                if (palpar(nodoActual.posicion)) {
                    System.out.println("¡ENCONTRADO!");
                    return nodoActual;
                }
                System.out.println("continuar búsqueda");
                
                List<Nodo> hijos = generarHijos(nodoActual);
                for (Nodo hijo : hijos) {
                    colaPrioridad.add(hijo);
                    System.out.printf("  -> Generando hijo: posición=%.2f, heurística=%.2f\n", 
                                    hijo.posicion, hijo.valorHeuristico);
                }
            }
            
            return null;
        }
        
        public void mostrarResultados(Nodo nodoObjetivo) {
            System.out.println("\n=== RESULTADOS ===");
            System.out.println("Movimientos realizados: " + movimientosRealizados);
            System.out.println("Posiciones exploradas: " + ordenExploracion.size());
            System.out.println("Tiempo estimado: " + (movimientosRealizados * 2) + " segundos");
            
            System.out.println("\nSecuencia de exploración:");
            for (int i = 0; i < ordenExploracion.size(); i++) {
                System.out.print(ordenExploracion.get(i).nombre);
                if (i < ordenExploracion.size() - 1) System.out.print(" → ");
            }
            System.out.println();
            
            if (nodoObjetivo != null) {
                System.out.println("\nCamino desde B hasta A:");
                List<String> camino = new ArrayList<>();
                Nodo temp = nodoObjetivo;
                while (temp != null) {
                    camino.add(0, temp.nombre);
                    temp = temp.padre;
                }
                camino.set(camino.size() - 1, "A");
                System.out.println(String.join(" → ", camino));
            }
        }
    }
    
    public static void main(String[] args) {
        double posicionTeorica = 100.0;
        double incrementoBusqueda = 2.0;
        
        System.out.println("CASO 1: Desplazamiento a la derecha");
        System.out.println("------------------------------------");
        double posicionReal1 = 106.0;
        RobotMontajeHeuristico robot1 = new RobotMontajeHeuristico(posicionTeorica, posicionReal1, 
                                                                   incrementoBusqueda);
        Nodo resultado1 = robot1.busquedaPrimeroElMejor();
        if (resultado1 != null) {
            System.out.println("\nPunto de montaje encontrado en: " + resultado1.posicion + " mm");
        }
        robot1.mostrarResultados(resultado1);
        
        System.out.println("\n\nCASO 2: Desplazamiento a la izquierda");
        System.out.println("--------------------------------------");
        double posicionReal2 = 94.0;
        RobotMontajeHeuristico robot2 = new RobotMontajeHeuristico(posicionTeorica, posicionReal2, 
                                                                   incrementoBusqueda);
        Nodo resultado2 = robot2.busquedaPrimeroElMejor();
        if (resultado2 != null) {
            System.out.println("\nPunto de montaje encontrado en: " + resultado2.posicion + " mm");
        }
        robot2.mostrarResultados(resultado2);
        
        System.out.println("\n\nCASO 3: Desplazamiento mayor");
        System.out.println("-----------------------------");
        double posicionReal3 = 112.0;
        RobotMontajeHeuristico robot3 = new RobotMontajeHeuristico(posicionTeorica, posicionReal3, 
                                                                   incrementoBusqueda);
        Nodo resultado3 = robot3.busquedaPrimeroElMejor();
        if (resultado3 != null) {
            System.out.println("\nPunto de montaje encontrado en: " + resultado3.posicion + " mm");
        }
        robot3.mostrarResultados(resultado3);
    }
}