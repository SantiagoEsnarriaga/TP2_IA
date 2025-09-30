package busquedaexhaustiva;

import java.util.*;

public class BusquedaExhaustivaDFS {
    
    static class Nodo {
        double posicion;
        String nombre;
        Nodo padre;
        String direccion;
        int profundidad;
        
        public Nodo(double posicion, String nombre, Nodo padre, String direccion, int profundidad) {
            this.posicion = posicion;
            this.nombre = nombre;
            this.padre = padre;
            this.direccion = direccion;
            this.profundidad = profundidad;
        }
        
        public String toString() {
            return String.format("%s(%.2f mm)", nombre, posicion);
        }
    }
    
    static class RobotMontajeDFS {
        private double posicionObjetivo;
        private double posicionInicial;
        private double incremento;
        private int movimientosRealizados;
        private int profundidadMaxima;
        private Stack<Nodo> pila;
        private Set<Double> visitados;
        private List<Nodo> ordenExploracion;
        
        public RobotMontajeDFS(double posicionInicial, double posicionObjetivo, 
                               double incremento, int profundidadMaxima) {
            this.posicionInicial = posicionInicial;
            this.posicionObjetivo = posicionObjetivo;
            this.incremento = incremento;
            this.profundidadMaxima = profundidadMaxima;
            this.movimientosRealizados = 0;
            this.pila = new Stack<>();
            this.visitados = new HashSet<>();
            this.ordenExploracion = new ArrayList<>();
        }
        
        private boolean palpar(double posicion) {
            movimientosRealizados++;
            return Math.abs(posicion - posicionObjetivo) < 0.5;
        }
        
        private List<Nodo> generarHijos(Nodo nodoActual) {
            List<Nodo> hijos = new ArrayList<>();
            
            if (nodoActual.profundidad < profundidadMaxima) {
                double posicionDerecha = nodoActual.posicion + incremento;
                if (!visitados.contains(posicionDerecha)) {
                    String nombreDer = "B" + (ordenExploracion.size() + 1) + "d";
                    hijos.add(new Nodo(posicionDerecha, nombreDer, nodoActual, 
                                      "DERECHA", nodoActual.profundidad + 1));
                }
                
                double posicionIzquierda = nodoActual.posicion - incremento;
                if (!visitados.contains(posicionIzquierda)) {
                    String nombreIzq = "B" + (ordenExploracion.size() + 1) + "i";
                    hijos.add(new Nodo(posicionIzquierda, nombreIzq, nodoActual, 
                                      "IZQUIERDA", nodoActual.profundidad + 1));
                }
            }
            
            return hijos;
        }
        
        public Nodo busquedaDFS() {
            System.out.println("=== BÚSQUEDA EXHAUSTIVA DFS - ROBOT DE MONTAJE ===");
            System.out.println("Posición inicial B: " + posicionInicial + " mm");
            System.out.println("Posición objetivo A: " + posicionObjetivo + " mm");
            System.out.println("Incremento ΔH: " + incremento + " mm\n");
            
            Nodo raiz = new Nodo(posicionInicial, "B", null, "INICIO", 0);
            pila.push(raiz);
            
            while (!pila.isEmpty()) {
                Nodo nodoActual = pila.pop();
                
                if (visitados.contains(nodoActual.posicion)) {
                    continue;
                }
                
                visitados.add(nodoActual.posicion);
                ordenExploracion.add(nodoActual);
                
                System.out.printf("Explorando %s en posición %.2f mm... ", 
                                 nodoActual.nombre, nodoActual.posicion);
                
                if (palpar(nodoActual.posicion)) {
                    System.out.println("¡ENCONTRADO!");
                    return nodoActual;
                }
                System.out.println("continuar búsqueda");
                
                List<Nodo> hijos = generarHijos(nodoActual);
                Collections.reverse(hijos);
                for (Nodo hijo : hijos) {
                    pila.push(hijo);
                }
            }
            
            return null;
        }
        
        public void mostrarResultados() {
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
        }
    }
    
    public static void main(String[] args) {
        double posicionTeorica = 100.0;
        double incrementoBusqueda = 2.0;
        int profundidadMax = 10;
        
        System.out.println("CASO 1: Desplazamiento a la derecha");
        System.out.println("------------------------------------");
        double posicionReal1 = 106.0;
        RobotMontajeDFS robot1 = new RobotMontajeDFS(posicionTeorica, posicionReal1, 
                                                      incrementoBusqueda, profundidadMax);
        Nodo resultado1 = robot1.busquedaDFS();
        if (resultado1 != null) {
            System.out.println("Punto de montaje encontrado: " + resultado1);
        }
        robot1.mostrarResultados();
        
        System.out.println("\n\nCASO 2: Desplazamiento a la izquierda");
        System.out.println("--------------------------------------");
        double posicionReal2 = 94.0;
        RobotMontajeDFS robot2 = new RobotMontajeDFS(posicionTeorica, posicionReal2, 
                                                      incrementoBusqueda, profundidadMax);
        Nodo resultado2 = robot2.busquedaDFS();
        if (resultado2 != null) {
            System.out.println("Punto de montaje encontrado: " + resultado2);
        }
        robot2.mostrarResultados();
    }
}