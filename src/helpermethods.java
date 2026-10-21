/*
Class to throw odd one off helper methods, there is not much special to say about this area
*/

public class helpermethods {
    
    // This takes a long (which are KibiBytes) and returns them as GibiBytes
    // Made By Luke-F-W
    public static double kibibytesToGibibyes(double kib){
        double gib = kib / 1048576;
        return gib;
    }
}
