/*
Class to throw odd one off helper methods, there is not much special to say about this area
I kind of just throw any 1 off methods i need in here -Luke-F-W
*/

import java.io.IOException;

public class HelperMethods {

    // This takes a long (which are KibiBytes) and returns them as GibiBytes
    // Made By Luke-F-W
    public static double kibibytesToGibibyes(double kib) {
        double gib = kib / 1048576;
        return gib;
    }

    public static double[][] workloadPerCore() {
        Core[] cpuCores = new Core[CpuInfo.getCount()];

        for (int i = 0; i < CpuInfo.getCount(); i++) {
            cpuCores[i] = new Core(CpuInfo.getBusy(i), CpuInfo.getIdle(i));
        }

        double[][] cpuarray = new double[CpuInfo.getCount()][3];

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restores interrupted status
        }

        for (int i = 0; i < CpuInfo.getCount(); i++) {
            long beforeBusy = cpuCores[i].beforeBusy;
            long beforeIdle = cpuCores[i].beforeIdle;

            long secondBusy = CpuInfo.getBusy(i) - beforeBusy; // counts how many ticks a core was busy in 1s
            long secondIdle = CpuInfo.getIdle(i) - beforeIdle; // counts how many ticks a core was idle in 1s

            double busyRatio = ((double) secondBusy / (secondBusy + secondIdle)) * 100; // multiplied by 100 to get a percent

            // get final numbers for busy and idle, will add to 2d array for html presentation
            cpuarray[i][0] = secondBusy + beforeBusy;
            cpuarray[i][1] = secondIdle + beforeIdle;
            cpuarray[i][2] = busyRatio;
        }
        return cpuarray;
    }

    public static String[] getRAMInfo() {
        String[] raminfo = new String[3];

        double percentageUsed = ((double) MemInfo.getUsed() / (double) MemInfo.getTotal()) * 100.0;
        String memoryTotal = String.format(
            "%.2f",
            HelperMethods.kibibytesToGibibyes(MemInfo.getTotal())
        );
        String memoryUsed = String.format(
            "%.2f",
            HelperMethods.kibibytesToGibibyes(MemInfo.getUsed())
        );
        String percentUsed = String.format("%.2f", percentageUsed);

        raminfo[0] = memoryTotal;
        raminfo[1] = memoryUsed;
        raminfo[2] = percentUsed;
        return raminfo;
    }

    public static String[][] printUSBInfo() {
        String[][] usbarray = new String[UsbInfo.getCount()][6];
        IdLookup usb = null;

        try {
            usb = IdLookup.usb();
        } catch (IOException e) {
            return usbarray;
        }

        for (int i = 0; i < UsbInfo.getCount(); i++) {
            int vendor = UsbInfo.getVendorId(i);
            int product = UsbInfo.getProductId(i);

            usbarray[i][0] = String.valueOf(UsbInfo.getBusNumber(i)); // Bus Number
            usbarray[i][1] = String.valueOf(UsbInfo.getDeviceNumber(i)); // Device Number
            usbarray[i][2] = String.format("%04x", vendor); // vendor
            usbarray[i][3] = String.format("%04x", product); // product
            usbarray[i][4] = usb.vendorName(vendor); // Vendor as name
            usbarray[i][5] = usb.deviceName(vendor, product); // Vendor and Product
        }
        return usbarray;
    }

    public static String[][] getPciInfo() {
        IdLookup pci = null;
        String[][] pciArray = new String[PciInfo.getCount()][5];
        try {
            pci = IdLookup.pci();
        } catch (IOException e) {
            return pciArray;
        }

        for (int i = 0; i < PciInfo.getCount(); i++) {
            int vendorId = PciInfo.getVendorId(i);
            int deviceId = PciInfo.getDeviceId(i);
            String address = PciInfo.getAddress(i);
            String name = pci.vendorName(vendorId);

            pciArray[i][0] = address; // address
            pciArray[i][1] = String.format("%04x", vendorId); // vendorid
            pciArray[i][2] = String.format("%04x", deviceId); // deviceid
            pciArray[i][3] = name; // name
            pciArray[i][4] = pci.deviceName(vendorId, deviceId); // devicename
        }
        return pciArray;
    }
}
