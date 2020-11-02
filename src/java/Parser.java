import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Parser {
    public static Problem parse(String path) throws IOException {
        File file = new File(path);

        if (file.isDirectory() || !file.exists())
            throw new IOException("file does not exist '" + path + "'");

        Problem problem = new Problem();

        Scanner scanner = new Scanner(file);
        int line = 1;

        if (!scanner.hasNextInt())
            throw new IOException("cannot parse line " + line);

        problem.setPieceSize(scanner.nextFloat());

        while (scanner.hasNextInt()) {
            float size = scanner.nextFloat();

            if (!scanner.hasNextInt())
                throw new IOException("cannot parse line " + line);

            if (size > problem.getPieceSize())
                throw new IOException("cut size cannot be greater than the piece size, line " + line);

            int count = scanner.nextInt();

            problem.addOrder(new Order(size, count));

            line++;
        }

        return problem;
    }

}
