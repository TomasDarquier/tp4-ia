import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public abstract class ImagenProcessor {
    protected BufferedImage imagen;

    public ImagenProcessor(String rutaImagen) throws IOException {
        imagen = ImageIO.read(new File(rutaImagen));
    }

    protected BufferedImage convertirEscalaGrises(BufferedImage original) {
        BufferedImage escalaGrises = new BufferedImage(
            original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        
        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {
                int rgb = original.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int gris = (r + g + b) / 3;
                int grisRGB = gris << 16 | gris << 8 | gris;
                escalaGrises.setRGB(x, y, grisRGB);
            }
        }
        
        return escalaGrises;
    }

    protected BufferedImage detectarBordes(BufferedImage escalaGrises) {
        int width = escalaGrises.getWidth();
        int height = escalaGrises.getHeight();
        BufferedImage bordes = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        
        int[][] sobelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] sobelY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
        
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int gx = 0;
                int gy = 0;
                
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int pixel = (escalaGrises.getRGB(x + i, y + j) & 0xFF);
                        gx += sobelX[i + 1][j + 1] * pixel;
                        gy += sobelY[i + 1][j + 1] * pixel;
                    }
                }
                
                int magnitud = (int) Math.sqrt(gx * gx + gy * gy);
                int colorBorde = (magnitud > 128) ? 0xFFFFFF : 0x000000;
                bordes.setRGB(x, y, colorBorde);
            }
        }
        
        return bordes;
    }

    protected void guardarImagen(String rutaSalida) throws IOException {
        ImageIO.write(imagen, "png", new File(rutaSalida));
    }
}
