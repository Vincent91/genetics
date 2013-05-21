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
        File f = new File("synchr64_ver2.out");
        Scanner scanner = new Scanner(f);
        scanner.useLocale(Locale.ENGLISH);
        while (scanner.hasNext()){
            double alpha = scanner.nextDouble();
            double gamma = scanner.nextDouble();
            double betha = scanner.nextDouble();
            double step = scanner.nextDouble();
            double exp = scanner.nextDouble();
            double ranExp = scanner.nextDouble();
            double dev = scanner.nextDouble();
            double ranDev = scanner.nextDouble();
//            System.out.format("%.2f\n", (ranExp - exp));
            if (ranExp - exp > 60){
                System.out.format("%.2f %.2f %.2f\n", alpha, gamma, ranExp - exp);
            }
        }
    }
}
