import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TransformadaHoughLineas extends ImagenProcessor {

    public TransformadaHoughLineas(String rutaImagen) throws IOException {
        super(rutaImagen);
    }

    private List<int[]> transformadaHough(BufferedImage bordes) {
        int width = bordes.getWidth();
        int height = bordes.getHeight();
        int maxDistancia = (int) Math.hypot(width, height);
        int[][] acumulador = new int[2 * maxDistancia][180];
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if ((bordes.getRGB(x, y) & 0xFF) == 0xFF) {
                    for (int theta = 0; theta < 180; theta++) {
                        double radianes = Math.toRadians(theta);
                        int r = (int) (x * Math.cos(radianes) + y * Math.sin(radianes));
                        if (r + maxDistancia >= 0 && r + maxDistancia < 2 * maxDistancia) {
                            acumulador[r + maxDistancia][theta]++;
                        }
                    }
                }
            }
        }
        
        List<int[]> lineas = new ArrayList<>();
        int umbral = 120; // umbral para deteccion de lineas
        for (int r = 0; r < 2 * maxDistancia; r++) {
            for (int theta = 0; theta < 180; theta++) {
                if (acumulador[r][theta] > umbral) {
                    lineas.add(new int[]{r - maxDistancia, theta});
                }
            }
        }
        
        return lineas;
    }

    private void dibujarLineas(List<int[]> lineas) {
        for (int[] linea : lineas) {
            int r = linea[0];
            int theta = linea[1];
            double radianes = Math.toRadians(theta);
            
            for (int x = 0; x < imagen.getWidth(); x++) {
                int y = (int) ((r - x * Math.cos(radianes)) / Math.sin(radianes));
                if (y >= 0 && y < imagen.getHeight()) {
                    imagen.setRGB(x, y, 0xFF0000); 
                }
            }
        }
    }

    public void procesarImagen() throws IOException {
        BufferedImage escalaGrises = convertirEscalaGrises(imagen);
        BufferedImage bordes = detectarBordes(escalaGrises);
        List<int[]> lineas = transformadaHough(bordes);
        dibujarLineas(lineas);
        guardarImagen("output_lineas.png");
    }
}

