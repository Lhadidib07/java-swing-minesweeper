import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class Demineur {

    JFrame frame = new JFrame("Demineur");
    public int rowSize = 10; // le nombres de cellul dans un row 
    public int colSize = 10; // le nombres de cellul dans une colonne 
    public int tailleCellule = 50; // la taille d'une cellul 
    public int boardHeight = rowSize * tailleCellule; // le height selon le nombres de cellul 
    public int boardWidth = colSize * tailleCellule; // le widh selon le nombres de cellul
    public int nbCelluleCliquer = 0; // le nomrbres de cellul cliqué 
    public int nombresMines = 10; // le nombres de mines 
    public boolean partieTerminer; // pour voir si la partie et toujour en cours 
    public int tempsEcoule = 0; // Variable pour stocker le temps écoulé en secondes
    public JLabel tempsLabel; // JLabel pour afficher le temps écoulé
    public Timer timer; // le timer pour le chrono 

    private class Cellule extends JButton {
        private boolean etat=false;
        private boolean mine;

        public boolean isEtat() {
            return etat;
        }

        public void setEtat(boolean etat) {
            this.etat = etat;
        }

        public boolean isMine() {
            return mine;
        }

        public void setMine(boolean mine) {
            this.mine = mine;
        }

    }

    private Cellule[][] grid;

    public Demineur() {
        
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //  JComboBox pour choisir la difficulté
        // String[] difficultes = { "Facile", "Moyen", "Difficile" };
        // JComboBox<String> comboBoxDifficulte = new JComboBox<>(difficultes);
        // Ajoutez un ActionListener au JComboBox pour détecter les changements de sélection
        // frame.add(comboBoxDifficulte, BorderLayout.NORTH);
    
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
    
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(rowSize, colSize));
    
        grid = new Cellule[rowSize][colSize];
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) {
                grid[i][j] = new Cellule();
                gamePanel.add(grid[i][j]);
                grid[i][j].addActionListener(new CelluleListener(i, j));
            }
        }
    
        panel.add(gamePanel, BorderLayout.CENTER);
    
        tempsLabel = new JLabel("Temps écoulé : 0 secondes");
        panel.add(tempsLabel, BorderLayout.NORTH);
    
        frame.add(panel);
    
        placerMines();
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tempsEcoule++; // Incrémente le temps écoulé chaque seconde
                tempsLabel.setText("Temps écoulé : " + tempsEcoule + " secondes "+" et il ya "+nombresMines+" mines ");
            }
        });
    
        timer.start();
    }
    /* a utiliser lors du changemnt de dificulté 
    private void restartGame() {
        // Réinitialisez toutes les variables du jeu pour recommencer une nouvelle partie
        nbCelluleCliquer = 0;
        tempsEcoule = 0;
        partieTerminer = false;
        frame.setSize(boardWidth, boardHeight);

        // Réinitialisez l'état des cellules
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) {
                grid[i][j].setEtat(false);
                grid[i][j].setMine(false);
                grid[i][j].setText("");
                grid[i][j].setEnabled(true);
            }
        }

        // Réinitialisez le label du temps écoulé
        tempsLabel.setText("Temps écoulé : 0 secondes");
    }

     */
    private void placerMines() {
        Random random = new Random();
        int minesPlacées = 0;

        while (minesPlacées < nombresMines) {
            int randRow = random.nextInt(rowSize);
            int randCol = random.nextInt(colSize);

            if (!grid[randRow][randCol].isMine()) {
                grid[randRow][randCol].setMine(true);
                
                minesPlacées++;
            }
        }
    }

    private class CelluleListener implements ActionListener {
        private int row;
        private int col;

        public CelluleListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Cellule cellule = grid[row][col];
            if(partieTerminer){ 
                return; 
            }

            if (!cellule.isEtat()) {
                cellule.setEtat(true);
                if (cellule.isMine()) {
                    // Game Over
                    cellule.setText("💣");
                    // afficher toutes les bombe plus fenetre game over 
                    partieTerminer=true;
                    JOptionPane.showMessageDialog(frame, "Vous avez perdu en "+tempsEcoule+" s !");
                    timer.stop();
                    return;
                } else {
                    System.out.println(nbCelluleCliquer);
                    if(nbCelluleCliquer+1 >= colSize*rowSize-nombresMines){
                        timer.stop();
                        JOptionPane.showMessageDialog(frame, "Vous avez gagné en "+tempsEcoule+" s !" );
                        partieTerminer=true;
                    }
                    // Comptez les mines voisines et affichez le nombre
                    int minesVoisines = compterMinesVoisines(row, col);
                    if (minesVoisines > 0) {
                        cellule.setText(Integer.toString(minesVoisines));
                    } else {
                        // Révélez les cellules voisines vides en cascade
                        revelerCellulesVides(row, col);
                    }
                }
            }
        }

        private int compterMinesVoisines(int row, int col) {
            int minesVoisines = 0;
            
            // Logique pour compter les mines voisines
            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    if (i >= 0 && i < rowSize && j >= 0 && j < colSize && !(i == row && j == col) && grid[i][j].isMine()) {
                        minesVoisines++;
                    }
                }
            }
            if(grid[row][col].isEnabled()){ 
                nbCelluleCliquer++;
                grid[row][col].setEnabled(false);
            }
            

            return minesVoisines;
        }
        
        private void revelerCellulesVides(int row, int col) {
            // Logique pour révéler les cellules vides en cascade
            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    if (i >= 0 && i < rowSize && j >= 0 && j < colSize && !(i == row && j == col) && !grid[i][j].isMine()) {
                        int minesVoisines = compterMinesVoisines(i, j);
                        if (minesVoisines == 0) {
                            if (!grid[i][j].isEtat()) {
                               // grid[i][j].setEtat(true);
                                grid[i][j].setText("");
                                grid[row][col].setEtat(true); 
                                revelerCellulesVides(i, j);
                            }
                        } else {
                            grid[i][j].setText(Integer.toString(minesVoisines));
                        }
                    }
                }
            }
        }
        

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Demineur demineur = new Demineur();
            demineur.frame.setVisible(true);
        });
    }
}
