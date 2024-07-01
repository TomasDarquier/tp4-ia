import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TransformadaHoughCirculos extends ImagenProcessor {

    public TransformadaHoughCirculos(String rutaImagen) throws IOException {
        super(rutaImagen);
    }

    private List<int[]> transformadaHoughCirculos(BufferedImage bordes, int radioMin, int radioMax, int umbral) {
        int width = bordes.getWidth();
        int height = bordes.getHeight();
        int[][][] acumulador = new int[width][height][radioMax - radioMin + 1];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if ((bordes.getRGB(x, y) & 0xFF) == 0xFF) {
                    for (int r = radioMin; r <= radioMax; r++) {
                        for (int theta = 0; theta < 360; theta++) {
                            double radianes = Math.toRadians(theta);
                            int a = (int) (x - r * Math.cos(radianes));
                            int b = (int) (y - r * Math.sin(radianes));
                            if (a >= 0 && a < width && b >= 0 && b < height) {
                                acumulador[a][b][r - radioMin]++;
                            }
                        }
                    }
                }
            }
        }

        List<int[]> circulos = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int r = radioMin; r <= radioMax; r++) {
                    if (acumulador[x][y][r - radioMin] > umbral) {
                        circulos.add(new int[]{x, y, r});
                    }
                }
            }
        }
        return circulos;
    }

    private List<int[]> supresionNoMaxima(List<int[]> circulos, int radioSupresion) {
        List<int[]> circulosSuprimidos = new ArrayList<>();

        for (int i = 0; i < circulos.size(); i++) {
            int[] circulo1 = circulos.get(i);
            boolean esMaximo = true;

            for (int j = 0; j < circulos.size(); j++) {
                if (i == j) continue;
                int[] circulo2 = circulos.get(j);

                double distancia = Math.sqrt(Math.pow(circulo1[0] - circulo2[0], 2) + Math.pow(circulo1[1] - circulo2[1], 2));
                if (distancia < radioSupresion && circulo1[2] < circulo2[2]) {
                    esMaximo = false;
                    break;
                }
            }

            if (esMaximo) {
                circulosSuprimidos.add(circulo1);
            }
        }

        return circulosSuprimidos;
    }

    private void dibujarCirculos(List<int[]> circulos, int grosor) {
        int color = 0xFF0000; // Color rojo para círculos

        for (int[] circulo : circulos) {
            int x0 = circulo[0];
            int y0 = circulo[1];
            int radio = circulo[2];

            for (int theta = 0; theta < 360; theta++) {
                double radianes = Math.toRadians(theta);
                int x = (int) (x0 + radio * Math.cos(radianes));
                int y = (int) (y0 + radio * Math.sin(radianes));
                dibujarPuntoGordo(imagen, x, y, grosor, color);
            }
        }
    }

    private void dibujarPuntoGordo(BufferedImage img, int x, int y, int grosor, int color) {
        for (int i = -grosor; i <= grosor; i++) {
            for (int j = -grosor; j <= grosor;) {
                int nuevoX = x + i;
                int nuevoY = y + j;
                if (nuevoX >= 0 && nuevoX < img.getWidth() && nuevoY >= 0 && nuevoY < img.getHeight()) {
                    img.setRGB(nuevoX, nuevoY, color);
                }
            }
        }
    }

    public void procesarImagenParaCirculos(int radioMin, int radioMax, int umbral, int radioSupresion, int grosor) throws IOException {
        BufferedImage escalaGrises = convertirEscalaGrises(imagen);
        BufferedImage bordes = detectarBordes(escalaGrises);
        List<int[]> circulos = transformadaHoughCirculos(bordes, radioMin, radioMax, umbral);
        circulos = supresionNoMaxima(circulos, radioSupresion);
        dibujarCirculos(circulos, grosor);
        guardarImagen("output_circulos.png");
    }
}
