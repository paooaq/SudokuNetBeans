/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author ducho
 */
public class MainGame {

    public static void main(String args[]) throws FileNotFoundException, IOException {
        File archivo = new File("board.txt");
        View v = new View(archivo);
        Model m = new Model();

        Controller c = new Controller(m, v);
        c.initController();
    }
}
