
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class Regla {

    private List<String> hechos;
    private String conclusion;

    public Regla(List<String> hechos, String conclusion) {
        this.hechos = new ArrayList<>(hechos);
        if (hechos.contains(conclusion)) {
            throw new IllegalArgumentException("La conclusión no puede ser parte de los hechos");
        }
        this.conclusion = conclusion;
    }

    public List<String> getHechos() {
        return hechos;
    }

    public String getConclusion() {
        return conclusion;
    }

    // Verifica si todos los hechos de la regla están presentes y son verdaderos
    public boolean esAplicable(Map<String, String> hechosDisponibles) {
        for (String hecho : hechos) {
            if (!hechosDisponibles.containsKey(hecho) || hechosDisponibles.get(hecho).equals("falso")) {
                return false;
            }
        }
        return true;
    }
}

class MotorDeInferencia {

    private Map<String, String> hechos;
    private List<Regla> reglas;
    private List<String> conclusionesInferidas;

    public MotorDeInferencia() {
        this.hechos = new HashMap<>();
        this.reglas = new ArrayList<>();
        this.conclusionesInferidas = new ArrayList<>();
    }

    public void agregarHecho(String hecho, String valor) {
        hechos.put(hecho, valor);
        System.out.println("Hecho agregado: " + hecho + "=" + valor);
    }

    public void agregarRegla(List<String> hechos, String conclusion) {
        if (hechos.contains(conclusion)) {
            System.out.println("Error: La conclusión no puede ser parte de los hechos.");
            return;
        }
        Regla nuevaRegla = new Regla(hechos, conclusion);
        reglas.add(nuevaRegla);
        System.out.println("Regla agregada: " + hechos + " -> " + conclusion);
    }

    public void inferir(Scanner scanner) {
        boolean hayCambios;

        do {
            hayCambios = false;

            // Recorremos todas las reglas
            for (Regla regla : reglas) {
                // Si la conclusión aún no ha sido inferida
                if (!hechos.containsKey(regla.getConclusion())) {

                    // Verificamos si todos los hechos de la regla están disponibles o deducibles
                    boolean todosHechosConocidos = true;
                    for (String hecho : regla.getHechos()) {
                        if (!hechos.containsKey(hecho)) {
                            todosHechosConocidos = false;
                            break; // Rompemos el ciclo si encontramos un hecho desconocido
                        }
                    }

                    // Si todos los hechos necesarios ya son conocidos y verdaderos, aplicamos la regla
                    if (todosHechosConocidos && regla.esAplicable(hechos)) {
                        String conclusion = regla.getConclusion();
                        System.out.println("Conclusión inferida: " + conclusion);
                        hechos.put(conclusion, "verdadero");
                        conclusionesInferidas.add(conclusion);
                        hayCambios = true; // Se produjo un cambio, debemos revisar las reglas nuevamente
                    }
                }
            }

            // Si no hubo inferencias nuevas, preguntamos al usuario por los hechos desconocidos
            if (!hayCambios) {
                for (Regla regla : reglas) {
                    if (!hechos.containsKey(regla.getConclusion())) {
                        for (String hecho : regla.getHechos()) {
                            if (!hechos.containsKey(hecho)) {
                                System.out.print("¿Es cierto que " + hecho + "? (sí/no): ");
                                String respuesta = scanner.nextLine().trim().toLowerCase();
                                if (respuesta.equals("si")) {
                                    hechos.put(hecho, "verdadero");
                                } else {
                                    hechos.put(hecho, "falso");
                                }
                                hayCambios = true;
                                break; // Volvemos a intentar inferir con la nueva información
                            }
                        }
                    }
                    if (hayCambios) {
                        break; // Salimos del ciclo para volver a intentar inferir
                    }
                }
            }

        } while (hayCambios);

        if (conclusionesInferidas.isEmpty()) {
            System.out.println("No se pudo inferir ninguna conclusión.");
        } else {
            System.out.println("Conclusiones finales inferidas: " + conclusionesInferidas);
        }
    }
}

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MotorDeInferencia motor = new MotorDeInferencia();

        boolean continuar = true;

        while (continuar) {
            System.out.println("\n¿Qué te gustaría hacer?");
            System.out.println("1. Insertar hecho");
            System.out.println("2. Insertar regla");
            System.out.println("3. Realizar inferencia");
            System.out.println("4. Salir");
            System.out.print("Selecciona una opción: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    System.out.print("Ingresa el hecho en formato clave=valor (Ejemplo: color=verde): ");
                    String hecho = scanner.nextLine().trim();
                    String[] hechoPartes = hecho.split("=");
                    if (hechoPartes.length == 2) {
                        motor.agregarHecho(hechoPartes[0], hechoPartes[1]);
                    } else {
                        System.out.println("Formato incorrecto, usa 'clave=valor'");
                    }
                    break;

                case "2":
                    System.out.println("Ingresa los hechos que formarán la regla.");
                    List<String> hechosRegla = new ArrayList<>();
                    String hechoNuevo;
                    do {
                        System.out.print("Ingresa un hecho en formato clave=valor (o deja vacío para terminar): ");
                        hechoNuevo = scanner.nextLine().trim();
                        if (!hechoNuevo.isEmpty()) {
                            hechosRegla.add(hechoNuevo);
                        }
                    } while (!hechoNuevo.isEmpty());

                    if (hechosRegla.isEmpty()) {
                        System.out.println("No se ingresaron hechos.");
                        break;
                    }

                    System.out.print("Ingresa la conclusión en formato clave=valor (Ejemplo: fruta=banana): ");
                    String conclusion = scanner.nextLine().trim();

                    motor.agregarRegla(hechosRegla, conclusion);
                    break;

                case "3":
                    System.out.println("Realizando inferencias a partir de preguntas...");
                    motor.inferir(scanner);
                    break;

                case "4":
                    continuar = false;
                    System.out.println("Saliendo del sistema...");
                    break;

                default:
                    System.out.println("Opción no válida. Intenta de nuevo.");
                    break;
            }
        }

        scanner.close();
    }
}
