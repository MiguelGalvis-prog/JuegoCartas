import java.util.Random;
import javax.swing.JPanel;

public class Jugador {

    private int DISTANCIA = 40;
    private int MARGEN = 10;
    private int TOTAL_CARTAS = 10;
    private String MENSAJE_PREDETERMINADO = "No se encontraron grupos";
    private String ENCABEZADO_MENSAJE = "Se encontraron los siguientes grupos:\n";
    private int MNIMA_CANTIDAD_GRUPO = 2;
    private Carta[] cartas = new Carta[TOTAL_CARTAS];
    private Random r = new Random();

    public void repartir() {
        for (int i = 0; i < TOTAL_CARTAS; i++) {
            cartas[i] = new Carta(r);
        }
    }

    public void mostrar(JPanel pnl) {
        pnl.removeAll();
        int x = MARGEN + (TOTAL_CARTAS - 1) * DISTANCIA;
        for (Carta carta : cartas) {
            carta.mostrar(pnl, x, MARGEN);
            x -= DISTANCIA;
        }
        pnl.repaint();
    }

    public String getGrupos() {
        String mensaje = MENSAJE_PREDETERMINADO;

        // 1) Contadores por nombre (para detectar pares, ternas, etc.)
        int[] contadores = new int[NombreCarta.values().length];
        // 2) Marcamos en este arreglo las cartas que ya forman parte de algún grupo o escalera
        boolean[] cartasMarcadas = new boolean[TOTAL_CARTAS];

        // Llenar contadores
        for (Carta c : cartas) {
            contadores[c.getNombre().ordinal()]++;
        }

        // Revisar si hay grupos de repetición
        boolean hayGrupos = false;
        for (int rep : contadores) {
            if (rep >= MNIMA_CANTIDAD_GRUPO) {
                hayGrupos = true;
                break;
            }
        }

        
        if (hayGrupos) {
            mensaje = ENCABEZADO_MENSAJE;
            
            for (int i = 0; i < contadores.length; i++) {
                if (contadores[i] >= MNIMA_CANTIDAD_GRUPO) {
                    mensaje += Grupo.values()[contadores[i]] + " de " + NombreCarta.values()[i] + "\n";
                    // Marcar las cartas de este grupo
                    for (int j = 0; j < TOTAL_CARTAS; j++) {
                        if (cartas[j].getNombre().ordinal() == i) {
                            cartasMarcadas[j] = true;
                        }
                    }
                }
            }
        } else {
             mensaje = MENSAJE_PREDETERMINADO;
        }

        
        
        String escaleraMsg = detectarEscaleras(cartasMarcadas);
        if (!escaleraMsg.isEmpty()) {
           
            if (mensaje.equals(MENSAJE_PREDETERMINADO)) {
                mensaje = ENCABEZADO_MENSAJE;
            }
            mensaje += escaleraMsg;
        }

       
        int puntaje = calcularPuntaje(cartasMarcadas);
        mensaje += "Puntaje total: " + puntaje;

        return mensaje;
    }

   
    private String detectarEscaleras(boolean[] cartasMarcadas) {
        StringBuilder sb = new StringBuilder();

        
        for (int p = 0; p < Pinta.values().length; p++) {
           
            int[] indicesPinta = new int[TOTAL_CARTAS];
            int count = 0;
            for (int i = 0; i < TOTAL_CARTAS; i++) {
                if (cartas[i].getPinta().ordinal() == p) {
                    indicesPinta[count++] = i; 
                }
            }
        
            for (int i = 0; i < count - 1; i++) {
                for (int j = i + 1; j < count; j++) {
                    if (cartas[indicesPinta[i]].getNombre().ordinal() >
                        cartas[indicesPinta[j]].getNombre().ordinal()) {
                        int temp = indicesPinta[i];
                        indicesPinta[i] = indicesPinta[j];
                        indicesPinta[j] = temp;
                    }
                }
            }

            
            int consecutivas = 1;
            int inicio = 0;
            for (int i = 1; i < count; i++) {
                int rankPrev = cartas[indicesPinta[i - 1]].getNombre().ordinal();
                int rankCurr = cartas[indicesPinta[i]].getNombre().ordinal();
                if (rankCurr == rankPrev + 1) {
                    consecutivas++;
                } else {
                    
                    if (consecutivas >= 3) {
                        sb.append(marcarEscalera(p, indicesPinta, inicio, i - 1, cartasMarcadas));
                    }
                   
                    consecutivas = 1;
                    inicio = i;
                }
            }
            
            if (consecutivas >= 3) {
                sb.append(marcarEscalera(p, indicesPinta, inicio, count - 1, cartasMarcadas));
            }
        }

        return sb.toString();
    }

   
    private String marcarEscalera(int pinta, int[] indicesPinta, int inicio, int fin, boolean[] cartasMarcadas) {
        StringBuilder sb = new StringBuilder();
        sb.append("ESCALERA de ").append(Pinta.values()[pinta]).append(": ");
        for (int i = inicio; i <= fin; i++) {
            int idxCarta = indicesPinta[i];
            sb.append(cartas[idxCarta].getNombre()).append(" ");
            cartasMarcadas[idxCarta] = true;
        }
        sb.append("\n");
        return sb.toString();
    }

    
    private int calcularPuntaje(boolean[] cartasMarcadas) {
        int puntaje = 0;
        for (int i = 0; i < TOTAL_CARTAS; i++) {
            if (!cartasMarcadas[i]) {
                int rank = cartas[i].getNombre().ordinal();
                
                if (rank == 0 || rank >= 9) {
                    puntaje += 10;
                } else {
                    puntaje += (rank + 1);
                }
            }
        }
        return puntaje;
    }
}
