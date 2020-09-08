package sudoku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

public class Model {

    private Cell[][] cells = new Cell[9][9];

    private String[] shape = new String[9];

    public Model(Model a) {
        construirTablero();
        for (int i = 0; i < 9; i++) {
            this.shape[i] = a.getShape()[i];
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                cells[i][j].addObserver();
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (a.getCellValue(i, j) > 0) {
                    this.cells[i][j].setValue(a.getCellValue(i, j));
                }
            }
        }
    }

    public Model(File archivo) throws FileNotFoundException, IOException {
        construirTablero();
        FileReader fr = new FileReader(archivo);
        BufferedReader br = new BufferedReader(fr);
        StringTokenizer token;
        String linea;
        int cont = 0;
        int pos;
        int value;
        int iter = 0;
        while ((linea = br.readLine()) != null) {
            if (cont == 9) {
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        cells[i][j].addObserver();
                    }
                }
                cont++;
                continue;
            }
            if (cont < 9) {
                this.shape[cont] = linea;
                cont++;
            } else {
                token = new StringTokenizer(linea);
                pos = Integer.parseInt(token.nextToken());
                value = Integer.parseInt(token.nextToken());;
                this.cells[pos / 9][pos % 9].setValue(value);
                test(pos / 9, pos % 9);
            }
        }
        if (cont == 9) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    cells[i][j].addObserver();
                }
            }
        }
        fr.close();
    }

    //GETTERS
    public Model() {
    }

    public String[] getShape() {
        return shape;
    }

    public int getCellValue(int i, int j) {
        return cells[i][j].getValue();
    }

    public List<Integer> getCellValues(int i, int j) {
        return cells[i][j].getValues();
    }

    public Cell getCell(int i, int j) {
        return cells[i][j];
    }

    //SETTERS
    public void setCellValue(int i, int j, int value) {
        this.cells[i][j].setValue(value);

    }

    public void construirTablero() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
    }

    @Override
    public String toString() {
        String string = "";
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                string += String.format("%s ", Integer.toString(getCellValue(i, j)));
                if (j == 2 || j == 5) {
                    string += "| ";
                }
            }
            string += "\n";
            if (i == 2 || i == 5) {
                string += "---------------------\n";
            }
        }
        return string;
    }

    public void test(int row, int col) {
        int cellPos = row * 9 + col;
        System.out.println("Set " + cellPos + " " + cells[row][col].getValue());
        int mycell = Integer.parseInt(String.format("%c", shape[row].charAt(col)));
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                boolean isSame = (i == row) && (j == col);
                boolean isSameLine = (i == row) || (j == col);
                boolean isSecondary = false;
                int mycontext = Integer.parseInt(String.format("%c", shape[i].charAt(j)));
                if (mycell == mycontext) {
                    isSecondary = true;
                }
                if (!isSame && (isSameLine || isSecondary)) {
                    int contextPos = i * 9 + j;
                    System.out.println("Posibles: " + contextPos + " => " + cells[i][j]);
                }
            }
        }
    }

    public void clone(Model board) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.cells[i][j].clone(board.getCell(i, j));
            }
        }
    }

    public boolean isSolve() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (getCellValue(i, j) == 0 || getCellValue(i, j) == -1) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean backtracking() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.isNullCandidates()) {
                    return false;
                }
                if (!this.getCell(i, j).isSolve()) {
                    for (int x : this.getCell(i, j)) {
                        Model copia = new Model(this);
                        copia.setCellValue(i, j, x);
                        if (copia.backtracking()) {
                            this.clone(new Model(copia));
                            return true;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isNullCandidates() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (cells[i][j].getValue() == -1) {
                    return true;
                }
            }
        }
        return false;
    }

    //*************************************************************************
    public class Cell extends Observable implements Observer, Iterable<Integer> {

        private List<Integer> values = new ArrayList<Integer>();

        private boolean isSolved = false;

        private int row;

        private int col;

        public Cell(int row, int col) {
            this.row = row;
            this.col = col;
            for (int n = 1; n <= 9; n++) {
                values.add(n);
            }
        }

        public synchronized void addObserver() {
            int mycell = Integer.parseInt("" + shape[row].charAt(col));
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    boolean isSame = (i == row) && (j == col);
                    boolean isSameLine = (i == row) || (j == col);
                    boolean isSecondary = false;
                    int mycontext = Integer.parseInt("" + shape[i].charAt(j));
                    if (mycell == mycontext) {
                        isSecondary = true;
                    }
                    if (!isSame && (isSameLine || isSecondary)) {
                        super.addObserver(cells[i][j]);
                    }
                }
            }
        }

        public void setValue(int value) {
            values.clear();
            values.add(value);
            isSolved = true;
            super.setChanged();
            super.notifyObservers(value);
        }

        @Override
        public void update(Observable o, Object arg) {
            values.remove(arg);
            if (!isSolved && values.size() == 1) {
                isSolved = true;
                int value = getValues().get(0);
                setValue(value);
            }
        }

        public int getValue() {
            return (getValues().size() == 1) ? getValues().get(0) : (getValues().size() == 0) ? -1 : 0;
        }

        public boolean isSolve() {
            return this.isSolved;
        }

        public List<Integer> getValues() {
            return values;
        }

        @Override
        public String toString() {
            String cadena = "";
            for (int i = 0; i < values.size(); i++) {
                cadena += String.format("%s ", Integer.toString(values.get(i)));
            }
            return cadena;
        }

        public Iterator<Integer> iterator() {
            return values.iterator();
        }

        public void clone(Cell celda) {
            List<Integer> copia = new ArrayList<Integer>();
            copia.addAll(celda.getValues());
            values.clear();
            values.addAll(copia);
        }

    }

}
