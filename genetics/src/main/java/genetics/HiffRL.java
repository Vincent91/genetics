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
        File f = new File("results.out");
        Scanner scanner = new Scanner(f);
        while (scanner.hasNext()){
            double a = scanner.nextDouble();
            double b = scanner.nextDouble();
            double c = scanner.nextDouble();
            double step = scanner.nextDouble();
            double exp = scanner.nextDouble();
            double ran = scanner.nextDouble();
            //System.out.format("%.2f\n", ran - exp);
            if (ran - exp > 70){
                System.out.println(a + " " + b + " " + c + " " + step + " " + exp + " " + ran);
            }
        }
    }
}
