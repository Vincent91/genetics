package genetics;

import genetics.hiff.*;
import org.uncommons.maths.random.MersenneTwisterRNG;

import javax.swing.plaf.metal.MetalTheme;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

/**
 * @author Vladislav Kononov vincent@yandex-team.ru
 */
public class HiffRL {

    public static void main(String[] args) throws FileNotFoundException {
        File f = new File("save");
        double[] sorted = new double[300];
        int pos = 0;
        for (File temp : f.listFiles()){
            double[] store = new double[101];
            Scanner scanner = new Scanner(temp);
            scanner.useLocale(Locale.ENGLISH);
            try{
                for (int i = 0; i < 101; ++i){
                    store[i] = scanner.nextDouble();
                }
            } catch (Exception ioe){
                scanner.close();
                temp.delete();
                continue;
            }
            Arrays.sort(store);
            scanner.close();
            PrintWriter writer = new PrintWriter(temp);
            for (int i = 0; i < store.length; ++i){
                writer.println(store[i]);
            }
            writer.close();
            System.out.println("медиана: " + store[50] + " левая граница: " + store[5] +
                    " правая граница: " + store[96] + " " + temp.getName());
//            sorted[pos] = store[50];
//            ++pos;
        }
//        for (int i = pos; i < 300; ++i)
//            sorted[i] = 2000000;
//        Arrays.sort(sorted);
//        for (int i = 0; i < pos; ++i){
//            System.out.println(sorted[i]);
//        }

    }
}
