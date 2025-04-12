import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        int boardWidth = 600;
        int boardHeight = boardWidth;

        JFrame frame = new JFrame("Snake");

        try {
            ImageIcon logoIcon = new ImageIcon("logo.png");
            frame.setIconImage(logoIcon.getImage());
        } catch (Exception e) {
            System.err.println("Error al cargar el logo: " + e.getMessage());
        }

        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight); // establecer el tamaño de la ventana
        frame.setLocationRelativeTo(null);
        frame.setResizable(false); // hacer que su tamaño no pueda ser moldeable
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // si apretamos la x la ventana se cierra

        SnakeGame snakeGame = new SnakeGame(boardWidth, boardHeight);
        frame.add(snakeGame);
        frame.pack();
        snakeGame.requestFocus();
    }
}
