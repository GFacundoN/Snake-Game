import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private class Tile {
        int x, y;
        Tile(int x, int y) {

            this.x = x;
            this.y = y;
        }
    }

    int boardWidth, boardHeight;
    int tileSize = 25;

    // Snake
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    // Food
    Tile food;
    Random random;

    // Game Logic
    Timer gameLoop;
    int velocityX;
    int velocityY;
    boolean gameOver = false;
    boolean promptRestart = false;

    // Scores
    int currentScore = 0;
    int maxScore = 0;

    // Font
    Font gameFont;

    // Menu options
    String[] menuOptions = {"Start Game", "Exit"};
    int selectedMenuOption = 0;
    boolean inMenu = true;

    // Restart options
    String[] options = {"YES", "NO"};
    int selectedOption = 0;

    SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.BLACK);
        setLayout(null);
        addKeyListener(this);
        setFocusable(true);

        // Load the custom font
        try {
            gameFont = Font.createFont(Font.TRUETYPE_FONT, new File("PressStart2P.ttf")).deriveFont(Font.PLAIN, 24);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(gameFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            gameFont = new Font("SansSerif", Font.PLAIN, 16); // Fallback font
        }

        initializeGame();
    }

    private void initializeGame() {
        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<>();

        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        velocityX = 0;
        velocityY = 0;

        gameOver = false;
        promptRestart = false;
        currentScore = 0;

        gameLoop = new Timer(100, this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.setFont(gameFont);

        if (inMenu) {
            // Draw menu
            g.setColor(Color.WHITE);
            g.drawString("SNAKE GAME", boardWidth / 2 - tileSize * 5, boardHeight / 2 - tileSize * 4);

            for (int i = 0; i < menuOptions.length; i++) {
                if (i == selectedMenuOption) {
                    g.setColor(Color.YELLOW);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.drawString(menuOptions[i], boardWidth / 2 - tileSize * 5 + i * tileSize * 3, boardHeight / 2 + tileSize * (2 + i * 2));
            }
        } else if (gameOver) {
            g.setColor(Color.WHITE);
            g.drawString("Game Over", boardWidth / 2 - tileSize * 4, boardHeight / 2 - tileSize * 2);
            g.drawString(" Score:" + currentScore, boardWidth / 2 - tileSize * 4, boardHeight / 2);
            g.drawString("Max Score:" + maxScore, boardWidth / 2 - tileSize * 5, boardHeight / 2 + tileSize * 2);

            // Draw restart options
            for (int i = 0; i < options.length; i++) {
                if (i == selectedOption) {
                    g.setColor(Color.YELLOW);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.drawString(options[i], boardWidth / 2 - tileSize * 3 + i * tileSize * 4, boardHeight / 2 + tileSize * 4);
            }
        } else {
            // Food
            g.setColor(Color.RED);
            g.fill3DRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize, true);

            // Snake Head
            g.setColor(Color.GREEN);
            g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);

            // Snake Body
            for (Tile snakePart : snakeBody) {
                g.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize, true);
            }

            // Score
            g.setColor(Color.WHITE);
            g.drawString("Score: " + currentScore, 25, 50);
        }
    }

    public void placeFood() {
        boolean validPosition = false;
        while (!validPosition) {
            food.x = random.nextInt(boardWidth / tileSize);
            food.y = random.nextInt(boardHeight / tileSize);
            validPosition = true;

            if (collision(snakeHead, food)) {
                validPosition = false;
            }

            for (Tile snakePart : snakeBody) {
                if (collision(snakePart, food)) {
                    validPosition = false;
                    break;
                }
            }
        }
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void move() {
        // Eat food
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
            currentScore++;
            if (currentScore > maxScore) {
                maxScore = currentScore;
            }
        }

        // Snake Body
        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) {
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {
                Tile prevSnakePart = snakeBody.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        // Snake Head
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        // Game Over conditions
        for (Tile snakePart : snakeBody) {
            if (collision(snakeHead, snakePart)) {
                gameOver = true;
                promptRestart = true;
            }
        }

        if (snakeHead.x < 0 || snakeHead.x >= boardWidth / tileSize || snakeHead.y < 0 || snakeHead.y >= boardHeight / tileSize) {
            gameOver = true;
            promptRestart = true;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver && !inMenu) {
            move();
            repaint();
        }
    }

    private void resetGame() {
        snakeHead = new Tile(5, 5);
        snakeBody.clear();
        currentScore = 0;
        placeFood();
        velocityX = 0;
        velocityY = 0;
        gameOver = false;
        promptRestart = false;
        selectedOption = 0;
        gameLoop.start();
        requestFocusInWindow();
    }

    private void startGame() {
        inMenu = false;
        resetGame();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (inMenu) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    selectedMenuOption = (selectedMenuOption - 1 + menuOptions.length) % menuOptions.length;
                    repaint();
                    break;
                case KeyEvent.VK_DOWN:
                    selectedMenuOption = (selectedMenuOption + 1) % menuOptions.length;
                    repaint();
                    break;
                case KeyEvent.VK_SPACE:
                    if (selectedMenuOption == 0) {
                        startGame();
                    } else {
                        System.exit(0);
                    }
                    break;
            }
            return;
        }

        if (gameOver && promptRestart) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    selectedOption = (selectedOption - 1 + options.length) % options.length;
                    repaint();
                    break;
                case KeyEvent.VK_RIGHT:
                    selectedOption = (selectedOption + 1) % options.length;
                    repaint();
                    break;
                case KeyEvent.VK_SPACE:
                    if (selectedOption == 0) {
                        resetGame();
                    } else {
                        System.exit(0);
                    }
                    break;
            }
            return;
        } else {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    if (velocityY != 1) {
                        velocityX = 0;
                        velocityY = -1;
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (velocityY != -1) {
                        velocityX = 0;
                        velocityY = 1;
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    if (velocityX != 1) {
                        velocityX = -1;
                        velocityY = 0;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (velocityX != -1) {
                        velocityX = 1;
                        velocityY = 0;
                    }
                    break;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}