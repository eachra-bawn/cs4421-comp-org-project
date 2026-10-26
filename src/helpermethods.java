/*
Class to throw odd one off helper methods, there is not much special to say about this area
*/

import java.io.IOException;

public class helpermethods {
    
    // This takes a long (which are KibiBytes) and returns them as GibiBytes
    // Made By Luke-F-W
    public static double kibibytesToGibibyes(double kib){
        double gib = kib / 1048576;
        return gib;
    }


    public static double[][] workloadPerCore() {
        Core[] cpuCores = new Core[cpuInfo.getCount()];

        for (int i = 0; i < cpuInfo.getCount(); i++) {
            cpuCores[i] = new Core(cpuInfo.getBusy(i), cpuInfo.getIdle(i));
        }

        double[][] cpuarray = new double[cpuInfo.getCount()][3];

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restores interrupted status
        }

        for (int i = 0; i < cpuInfo.getCount(); i++) {
            long beforeBusy = cpuCores[i].beforeBusy;
            long beforeIdle = cpuCores[i].beforeIdle;


            long secondBusy = cpuInfo.getBusy(i) - beforeBusy; // counts how many ticks a core was busy in 1s
            long secondIdle = cpuInfo.getIdle(i) - beforeIdle; // counts how many ticks a core was idle in 1s

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

        double percentageUsed = ((double) memInfo.getUsed() / (double) memInfo.getTotal()) * 100.0;
        String memoryTotal = String.format("%.2f", helpermethods.kibibytesToGibibyes(memInfo.getTotal()));
        String memoryUsed = String.format("%.2f", helpermethods.kibibytesToGibibyes(memInfo.getUsed()));
        String percentUsed = String.format("%.2f", percentageUsed);

        raminfo[0] = memoryTotal;
        raminfo[1] = memoryUsed;
        raminfo[2] = percentUsed;
        return raminfo;
    }

    public  static String[][] printUSBInfo() {
        String[][] usbarray = new String[usbInfo.getCount()][6];
        idLookup usb = null;

        try {
            usb = idLookup.usb();
        } 
        catch (IOException e) {
            return usbarray;
        }

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
            usbarray[i][0] = String.valueOf(usbInfo.getBusNumber(i)); // Bus Number
            usbarray[i][1] = String.valueOf(usbInfo.getDeviceNumber(i)); // Device Number
            usbarray[i][2] = String.format("%04x", vendor); // vendor
            usbarray[i][3] = String.format("%04x", product); // product
            usbarray[i][4] = usb.vendorName(vendor); // Vendor as name
            usbarray[i][5] = usb.deviceName(vendor, product); // Vendor and Product
        }
        return usbarray;
        }

    public static String[][] getPciInfo() {
        idLookup pci = null;
        String[][] pciArray = new String[pciInfo.getCount()][5];
        try {
            pci = idLookup.pci();
        } catch (IOException e) {
            return pciArray;
        }

        for (int i = 0; i < pciInfo.getCount(); i++) {
            int vendorId = pciInfo.getVendorId(i);
            int deviceId = pciInfo.getDeviceId(i);
            String address = pciInfo.getAddress(i);
            String name = pci.vendorName(vendorId);



            System.out.printf(
                "  %s  %04x:%04x  %s %s%n",
                address,
                vendorId,
                deviceId,
                name,
                pci.deviceName(vendorId, deviceId)
            );
            pciArray[i][0] = address; // address
            pciArray[i][1] = String.format("%04x", vendorId); // vendorid
            pciArray[i][2] = String.format("%04x", deviceId); // deviceid
            pciArray[i][3] = name; // name
            pciArray[i][4] = pci.deviceName(vendorId, deviceId); // devicename
        }
        return pciArray;
    }
}

