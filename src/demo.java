/**
 * A short example that shows how to call each provided class. It prints raw
 * numbers and a few names. It is NOT a solution to the assignment: you still
 * have to measure load, store the devices, sort them, analyse them and present
 * the results.
 *
 * Build:  javac *.java
 * Run:    java demo        (run it from the folder that contains the ids folder)
 */

import java.io.IOException;
import java.nio.file.Path;

public class demo {

    public static void main(String[] args) throws Exception {
        printCpuInfo();
        printMemoryInfo();
        printPCIInfo();
        printUSBInfo();
    }

    private static void printCpuInfo() {
        System.out.println("cores: " + cpuInfo.getCount());
        System.out.println("model: " + cpuInfo.getModel());

        Core[] cpuCores = new Core[cpuInfo.getCount()];

        for (int i = 0; i < cpuInfo.getCount(); i++){
            cpuCores[i] = new Core(cpuInfo.getBusy(i),cpuInfo.getIdle(i));
        }

        System.out.println("GATHERING DATA --- 1s");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); //restores interrupted status
        }

        System.out.print("\033[1A\033[2K"); //moves the cursor up by one line and clears it
        System.out.flush(); //forces the text out of the buffer onto the screen immediately

        double sumLoad = 0;
        for (int i = 0; i < cpuInfo.getCount(); i++){
            long beforeBusy = cpuCores[i].beforeBusy;
            long beforeIdle = cpuCores[i].beforeIdle;
            long secondBusy = cpuInfo.getBusy(i) - beforeBusy; //counts how many ticks a core was busy in 1s
            long secondIdle = cpuInfo.getIdle(i) - beforeIdle; //counts how many ticks a core was idle in 1s
            double busyRatio = (((double) secondBusy / (secondBusy + secondIdle)) * 100); //multiplied by 100 to get a percentage

            System.out.println("core " + i + ": busy=" + (secondBusy + beforeBusy) + " idle=" +
                    (secondIdle + beforeIdle) + "  (" + String.format("%.2f", busyRatio) + "%)"
            );

            sumLoad += busyRatio;
        }

        double avgLoad = sumLoad/cpuInfo.getCount();
        System.out.println("Average load across all cores is " + String.format("%.5f", avgLoad) + "%.");
    }

    // printMemory now displays data as GibiBytes instead of kibibytes, because kibibytes is a silly way to display memory in 2026, - Luke-F-W
    private static void printMemoryInfo() {
        double percentageUsed = ((double) memInfo.getUsed() / (double) memInfo.getTotal()) * 100.0;
        System.out.println(
            "memory: total = " +
                helpermethods.kibibytesToGibibyes(memInfo.getTotal()) +
                " GiB, used = " +
                helpermethods.kibibytesToGibibyes(memInfo.getUsed()) +
                " GiB (" +
                String.format("%.2f", percentageUsed) + 
                "%)"
        );
    }

    private static void printPCIInfo() {
        idLookup pci = null;
        try {
            pci = idLookup.pci();
        } catch (IOException e) {
            System.out.println("Could not look up PCI devices: " + e);
            return;
        }
        System.out.println("PCI devices: " + pciInfo.getCount());
        for (int i = 0; i < pciInfo.getCount(); i++) {
            int vendor = pciInfo.getVendorId(i);
            int device = pciInfo.getDeviceId(i);
            System.out.printf(
                "  %s  %04x:%04x  %s %s%n",
                pciInfo.getAddress(i),
                vendor,
                device,
                pci.vendorName(vendor),
                pci.deviceName(vendor, device)
            );
        }
    }

    private static void printUSBInfo() {
        idLookup usb = null;
        try {
            usb = idLookup.usb();
        } catch (IOException e) {
            System.out.println("Could not look up USBs: " + e);
            return;
        }
        System.out.println("USB devices: " + usbInfo.getCount());
        for (int i = 0; i < usbInfo.getCount(); i++) {
            int vendor = usbInfo.getVendorId(i);
            int product = usbInfo.getProductId(i);
            System.out.printf(
                "  Bus %03d Device %03d: ID %04x:%04x  %s %s%n",
                usbInfo.getBusNumber(i),
                usbInfo.getDeviceNumber(i),
                vendor,
                product,
                usb.vendorName(vendor),
                usb.deviceName(vendor, product)
            );
        }
    }
}
