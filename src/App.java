import java.io.IOException;

public class App {
public static void main(String[] args) {
    try {
        TransformadaHoughCirculos houghCirculos = new TransformadaHoughCirculos("input_circulo.png");
        TransformadaHoughLineas houghLineas = new TransformadaHoughLineas("input_linea.png");
        houghLineas.procesarImagen();
        houghCirculos.procesarImagenParaCirculos(50, 80, 185, 20, 3);  // Example radius range
        System.out.println("Procesamiento Finalizado");
    } catch (IOException e) {
        e.printStackTrace();
    }
}


}
