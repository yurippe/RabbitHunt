import java.io.*;
import javax.net.ssl.*;
import java.net.*;
import java.util.ArrayList;
/**
 * Write a description of class Benchmark here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Benchmark
{
    public static final int NUMBER_OF_TRIALS = 100000;
    public static final int NUMBER_OF_COLUMNS = 20;
    public static final int NUMBER_OF_ROWS = 20;

    private static Model model;
    private static final String creator = (new Rabbit(null, null)).getCreator();

    public static void runBenchmark() {
        model = new Model(NUMBER_OF_COLUMNS, NUMBER_OF_ROWS);
        countGames();
    }
    
    public static void runBenchmark(int times){
        for(int i = 0; i<times; i++){
            runBenchmark();
        }
    }

    /**
     * Runs NUMBER_OF_TRIALS rabbit hunts, and prints out the
     * results as a percentage of times the rabbit escapes.
     */
    private static final void countGames() {
        ArrayList<Integer> stats = new ArrayList<Integer>();
        for (int i = 0; i < NUMBER_OF_TRIALS; i++) {
            model.reset(1, 2, 20, 5);
            while (!model.isGameOver()) {
                model.allowSingleMove();
            }
            if (model.isRabbitAlive()) {
                stats.add(model.getStepsTake());
            } else {
                stats.add(-1);
            }
        }
        submitScore(stats);
    }

    private static void submitScore(ArrayList<Integer> stats) {
        if(stats.size() == 0) {
            System.err.println("Kunne ikke indsende score da der ikke er målt nogen");
            return;
        }

        String source = readFile();
        if(source.equals("")) {
            return;
        }

        int sum = 0;
        int lossCount = 0;
        for(int i : stats) {
            if(i < 0) {
                lossCount++;
                sum += 50000;
            } else {
                sum += i;
            }
        }
        String score = "" + (sum * 1.0 / stats.size());

        String data = "";
        try{
            data += URLEncoder.encode("creator", "UTF-8") + "=" + URLEncoder.encode(creator, "UTF-8");
            data += "&" + URLEncoder.encode("score", "UTF-8") + "=" + URLEncoder.encode(score, "UTF-8");
            data += "&" + URLEncoder.encode("source", "UTF-8") + "=" + URLEncoder.encode(source, "UTF-8");
        } catch(UnsupportedEncodingException uee) {
            System.err.println("Fejl opstod i kodningen af dataen");
            return;
        }
        
        String reply = post("https://users-cs.au.dk/dintprog/e15/rabbithunt.php", data);
        System.out.println("\nThe rabbit won " + (NUMBER_OF_TRIALS-lossCount) + " time(s)" + reply);
    }

    private static String readFile() {
        String fileName = "Rabbit.java";
        System.out.println("Læser fil: " + fileName);
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = in.readLine()) != null) {
                sb.append(line + "\n");
            }
            in.close();

            return sb.toString();
        } catch(IOException ioe) {
            System.err.println("Kunne ikke læse " + fileName);
            return "";
        }
    }

    private static String post(String url, String data) {
        try {
            HttpsURLConnection conn = (HttpsURLConnection) (new URL(url)).openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(data);
            out.flush();
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = in.readLine()) != null) {
                sb.append(line + "\n");
            }
            in.close();

            return sb.toString();
        } catch(IOException ioe) {
            System.err.println("Kunne ikke uploade score. Er du på nettet?");
            return "";
        }
    }
}
