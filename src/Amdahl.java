public class Amdahl {
    public static void calcAmdahl(){
        String ANSI_RESET = "\u001B[0m"; //reset
        String ANSI_GREEN = "\u001B[48;2;124;204;87m";
        String ANSI_ORANGE = "\u001B[38;2;255;103;0m";
        try{
            System.out.println(ANSI_GREEN + "Amdahl's Law:" + ANSI_RESET);
            // run multi
            Process multi = new ProcessBuilder("./amdahl/src/bin/multi").start();
            String multiSpeedStr = new String(multi.getInputStream().readAllBytes());

            Double multiSpeed = convTime(multiSpeedStr);

            double multiSpeedAsSecond = multiSpeed / 1000;
            System.out.printf(ANSI_ORANGE + "  At 8 threads the speed is: %.2fs\n" + ANSI_RESET, multiSpeedAsSecond);

            // run single
            Process single = new ProcessBuilder("./amdahl/src/bin/single").start();
            String singleSpeedStr = new String(single.getInputStream().readAllBytes());

            Double singleSpeed = convTime(singleSpeedStr);

            double singleSpeedAsSecond = singleSpeed / 1000;
            System.out.printf(ANSI_ORANGE + "  At 1 thread the speed is: %.2fs\n" + ANSI_RESET, singleSpeedAsSecond);


            // calculate amdahls law

            double speedup = singleSpeed / multiSpeed;
            double threads = 8;

            double onedivspeed = 1 / speedup;
            double onedivthreads = 1 / threads;
            
            double topfraction = onedivspeed - onedivthreads;
            double bottomfraction = 1 - onedivthreads;

            double programMustRunSequentially = topfraction / bottomfraction;

           
            System.out.printf(ANSI_ORANGE + "  Programs That Must Run Sequentially: %.2f%%\n" + ANSI_RESET, programMustRunSequentially * 100);

            double maximumSpeedup = 1 / programMustRunSequentially;

            System.out.printf(ANSI_ORANGE + "  Maximum Theoretical Speedup: %.2fX\n" + ANSI_RESET, maximumSpeedup);


        }
        catch(Exception e){
            System.out.println(e);
        }

    }

    public static double convTime(String time){
        time = time.replace("Time: ", "");
        if (time.contains("ns")){
            time = time.replace("ns", "");
            Double finalTime = Double.parseDouble(time) / 1000000.0;
            return finalTime;
        }
        else if (time.contains("µs")){
            time = time.replace("µs", "");
            Double finalTime = Double.parseDouble(time) / 1000.0;
            return finalTime;
        }
        else if (time.contains("ms")){
            time = time.replace("ms", "");
            Double finalTime = Double.parseDouble(time);
            return finalTime;
        }
        else if (time.contains("s")){
            time = time.replace("s", "");
            Double finalTime = Double.parseDouble(time) * 1000.0;
            return finalTime;
        }
        return 0.0;
    }
}