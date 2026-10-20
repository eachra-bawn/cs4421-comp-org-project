//class Core - used for measuring CPU core loads, has uses in src/demo.java inside the printCpuInfo method
public class Core {
    long beforeBusy;
    long beforeIdle;
    public  Core(long lBeforeBusy, long lBeforeIdle){
        this.beforeBusy=lBeforeBusy;
        this.beforeIdle=lBeforeIdle;
    }
}
