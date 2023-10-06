import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class Demineur {

  

    JFrame frame = new JFrame("Demineur");
    public int rowSize = 10; // le nombres de cellul dans un row 
    public int colSize = 10; // le nombres de cellul dans une colonne 
    public int nombresMines = 20; // le nombres de mines 

    public int tailleCellule = 50; // la taille d'une cellul 
    public int boardHeight = rowSize * tailleCellule+1; // le height selon le nombres de cellul 
    public int boardWidth = colSize * tailleCellule+1; // le widh selon le nombres de cellul

    public int nbCelluleCliquer = 0; // le nomrbres de cellul cliqué 
    public boolean partieTerminer; // pour voir si la partie et toujour en cours 
    public int tempsEcoule = 0; // Variable pour stocker le temps écoulé en secondes
    public JLabel tempsLabel; // JLabel pour afficher le temps écoulé
    JPanel gamePanel = new JPanel();
    JPanel panel = new JPanel();
    public Timer timer; // le timer pour le chrono 

      public void setValue(){ 
        tailleCellule = 50; 
        boardHeight = rowSize * tailleCellule+1;
        boardWidth = colSize * tailleCellule+1;
      }
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
    public void afficherCell(){ 
        for(int i=0 ; i <rowSize;i++){ 
            for(int j=0; j<colSize;j++){ 
                if(!grid[i][j].isEtat()){ 
                    if (grid[i][j].isMine()) {

                        grid[i][j].setText("💣");
                        grid[i][j].setEnabled(false);

                    }
                }
                
            }
        }
    }

    private Cellule[][] grid;

    public void setDeficulter(String type){ 
        switch (type) {
            case "Facile":
                rowSize = 5;
                colSize = 5;
                nombresMines = 4;
            break;
            case "Moyen":
                rowSize = 7;
                colSize = 7;
                nombresMines = 5;
            break;
            case "Difficile":
                rowSize = 11;
                colSize = 11;
                nombresMines = 10;
            break;
            // Ajoutez d'autres niveaux de difficulté si nécessaire
        }
    }

    public Demineur(String type){
        setDeficulter(type);
        setValue();
        // Configuration initiale de la fenêtre
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        // Panel principal
        panel.setLayout(new BorderLayout());
    
        // JComboBox pour choisir la difficulté
        String[] difficultes = { "Facile", "Moyen", "Difficile" };
        JComboBox<String> comboBoxDifficulte = new JComboBox<>(difficultes);
        comboBoxDifficulte.setSelectedItem(dificulter);
        comboBoxDifficulte.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedDifficulty = (String) comboBoxDifficulte.getSelectedItem();
                // Appelez une méthode pour redémarrer le jeu en fonction de la difficulté choisie
                restartGame(selectedDifficulty);
            }
        });
        panel.add(comboBoxDifficulte, BorderLayout.NORTH);
    
        
        gamePanel.setLayout(new GridLayout(rowSize, colSize));
    
        grid = new Cellule[rowSize][colSize];
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) {
                grid[i][j] = new Cellule();
                gamePanel.add(grid[i][j]);
                grid[i][j].addActionListener(new CelluleListener(i, j));
            }
        }
    
        // Ajouter le panneau du jeu à la fenêtre
        panel.add(gamePanel, BorderLayout.CENTER);
    
        // Label pour afficher le temps écoulé
        tempsLabel = new JLabel("Temps écoulé : 0 secondes");
        panel.add(tempsLabel, BorderLayout.SOUTH);
    
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
    private void restartGame(String selectedDifficulty) {
        // Réinitialisez les variables en fonction de la difficulté choisie
        setDeficulter(selectedDifficulty);

        frame.dispose();
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Réinitialisez les variables du jeu
        nbCelluleCliquer = 0;
        tempsEcoule = 0;
        partieTerminer = false;
    
        // Arrêtez le timer s'il est en cours
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        main(new String[]{ selectedDifficulty } );
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
                    cellule.setEnabled(false);

                    // afficher toutes les bombe plus fenetre game over 
                    partieTerminer=true;
                    afficherCell();
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
    
    public static String dificulter = "Facile"; 

    public static void main(String[] args) {
        if(args.length != 0 ){ 
           dificulter=args[0];
        }
        SwingUtilities.invokeLater(() -> {
            Demineur demineur = new Demineur(dificulter);
            demineur.frame.setVisible(true);
        });

    }
}
