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

public class Demo {
public static final String ANSI_RESET = "\u001B[0m"; //reset
public static final String ANSI_GREEN = "\u001B[48;2;124;204;87m"; //headers
public static final String ANSI_DARK_BLUE = "\u001B[34m"; //cpu
public static final String ANSI_PINK = "\u001B[38;2;255;20;147m"; //memory
public static final String ANSI_CYAN = "\u001B[38;2;0;255;255m"; //USB devices
public static final String ANSI_YELLOW = "\u001B[38;2;255;215;0m"; //pci

public static final String ANSI_RED = "\u001B[48;2;179;1;30m"; 


    public static void main(String[] args) throws Exception {
        printCpuInfo();
        printMemoryInfo();
        printUSBInfo();

        if (args.length > 0) {
            switch (args[1]) {
                // Sort PCI devices by vendor name alphabetically
                case "-vendor":
                    printPCIInfoByVendor();
                    break;
                // Sort PCI devices by bus, device and function numbers numerically
                case "-bus":
                    printPCIInfoByBus();
                    break;
                default:
                    printPCIInfoByBus();
                    break;
            }
        } else {
            printPCIInfoByBus();
        }
        HtmlCsvSQLite.makeHtml();
    }

    private static void printCpuInfo() {
        System.out.println(Demo.ANSI_GREEN + "CPU Info:" + Demo.ANSI_RESET);
        System.out.println(Demo.ANSI_DARK_BLUE + "  Model: " + CpuInfo.getModel() + Demo.ANSI_RESET);
        System.out.println(Demo.ANSI_DARK_BLUE + "  Cores: " + CpuInfo.getCount() + Demo.ANSI_RESET);   

        Core[] cpuCores = new Core[CpuInfo.getCount()];

        for (int i = 0; i < CpuInfo.getCount(); i++) {
            cpuCores[i] = new Core(CpuInfo.getBusy(i), CpuInfo.getIdle(i));
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restores interrupted status
        }

        double sumLoad = 0;
        for (int i = 0; i < CpuInfo.getCount(); i++) {
            long beforeBusy = cpuCores[i].beforeBusy;
            long beforeIdle = cpuCores[i].beforeIdle;
            long secondBusy = CpuInfo.getBusy(i) - beforeBusy; // counts how many ticks a core was busy in 1s
            long secondIdle = CpuInfo.getIdle(i) - beforeIdle; // counts how many ticks a core was idle in 1s
            double busyRatio = ((double) secondBusy / (secondBusy + secondIdle)) * 100; // multiplied by 100 to get a
            // percentage

            System.out.println(
                Demo.ANSI_DARK_BLUE + "  Core " +
                    i +
                    ": busy=" +
                    (secondBusy + beforeBusy) +
                    " idle=" +
                    (secondIdle + beforeIdle) +
                    "  (" +
                    String.format("%.2f", busyRatio) +
                    "%)" + ANSI_RESET
            );

            sumLoad += busyRatio;
        }

        double avgLoad = sumLoad / CpuInfo.getCount();
        System.out.println(
            Demo.ANSI_DARK_BLUE + "  Average load across all cores is " + String.format("%.2f", avgLoad) + "%." + ANSI_RESET + "\n"
        );
    }

    // printMemory now displays data as GibiBytes instead of kibibytes, because
    // kibibytes is a silly way to display memory in 2026, - Luke-F-W
    private static void printMemoryInfo() {
        System.out.println(Demo.ANSI_GREEN + "Memory Info:" + Demo.ANSI_RESET);
        double percentageUsed = ((double) MemInfo.getUsed() / (double) MemInfo.getTotal()) * 100.0;
        System.out.println(
            Demo.ANSI_PINK + "  Memory: Total = " +
                String.format("%.2f", HelperMethods.kibibytesToGibibyes(MemInfo.getTotal())) +
                " GiB, Used = " +
                String.format("%.2f", HelperMethods.kibibytesToGibibyes(MemInfo.getUsed())) +
                " GiB (" +
                String.format("%.2f", percentageUsed) +
                "%)" + Demo.ANSI_RESET + "\n"
        );
    }

    private static void printUSBInfo() {
        IdLookup usb = null;

        try {
            usb = IdLookup.usb();
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Could not look up USBs: " + e + ANSI_RESET);
            return;
        }

        if (UsbInfo.getCount() == 0) {
            System.out.println(ANSI_RED + "USB devices: no USB devices found." + ANSI_RESET);
        } else {
            System.out.println(ANSI_GREEN + "USB devices: " + UsbInfo.getCount() + ANSI_RESET);

            for (int i = 0; i < UsbInfo.getCount(); i++) {
                int vendor = UsbInfo.getVendorId(i);
                int product = UsbInfo.getProductId(i);
                String vendorName = usb.vendorName(vendor);
                String deviceName = usb.deviceName(vendor, product);

                if (vendorName == null) {
                    System.out.printf(
                        ANSI_CYAN + "  Bus %03d Device %03d: ID %04x:%04x  %d%n" + ANSI_RESET,
                        UsbInfo.getBusNumber(i),
                        UsbInfo.getDeviceNumber(i),
                        vendor,
                        product,
                        vendorName
                    );
                } else {
                    System.out.printf(
                        ANSI_CYAN + "  Bus %03d Device %03d: ID %04x:%04x  %s %s%n" + ANSI_RESET,
                        UsbInfo.getBusNumber(i),
                        UsbInfo.getDeviceNumber(i),
                        vendor,
                        product,
                        vendorName,
                        deviceName
                    );
                }
            }
        }

        System.out.println();
    }

    private static void printPCIInfoByVendor() {
        IdLookup pci = null;

        try {
            pci = IdLookup.pci();
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Could not look up PCI devices: " + e + ANSI_RESET);
            return;
        }

        System.out.println(ANSI_GREEN + "PCI devices: " + PciInfo.getCount() + ANSI_RESET);

        PciDeviceInfoLine pciDeviceInfo = new PciDeviceInfoLine();

        for (int i = 0; i < PciInfo.getCount(); i++) {
            int vendorId = PciInfo.getVendorId(i);
            int deviceId = PciInfo.getDeviceId(i);
            String address = PciInfo.getAddress(i);
            String vendorName = pci.vendorName(vendorId);
            String deviceName = pci.deviceName(vendorId, deviceId);

            pciDeviceInfo.addVendorId(vendorId);
            pciDeviceInfo.addDeviceId(deviceId);
            pciDeviceInfo.addAddress(address);
            pciDeviceInfo.addVendorName(vendorName);
            pciDeviceInfo.addDeviceName(deviceName);

            pciDeviceInfo.sortVendorNames();
        }

        for (int i = 0; i < PciInfo.getCount(); i++) {
            if (pciDeviceInfo.getVendorNames().get(i) == null) {
                System.out.printf(
                    ANSI_YELLOW + "  %s  %04x:%04x  %04x%n" + ANSI_RESET,
                    pciDeviceInfo.getAddresses().get(i),
                    pciDeviceInfo.getVendorIds().get(i),
                    pciDeviceInfo.getDeviceIds().get(i),
                    pciDeviceInfo.getVendorNames().get(i),
                    pciDeviceInfo.getVendorIds().get(i)
                );
            } else {
                System.out.printf(
                    ANSI_YELLOW + "  %s  %04x:%04x  %s %s%n" + ANSI_RESET,
                    pciDeviceInfo.getAddresses().get(i),
                    pciDeviceInfo.getVendorIds().get(i),
                    pciDeviceInfo.getDeviceIds().get(i),
                    pciDeviceInfo.getVendorNames().get(i),
                    pciDeviceInfo.getDeviceNames().get(i)
                );
            }
        }

        System.out.println();

        pciDeviceInfo.getVendorSummary();
    }

    private static void printPCIInfoByBus() {
        IdLookup pci = null;

        try {
            pci = IdLookup.pci();
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Could not look up PCI devices: " + e + ANSI_RESET);
            return;
        }

        System.out.println(ANSI_GREEN + "PCI devices: " + PciInfo.getCount() + ANSI_RESET);

        PciDeviceInfoLine pciDeviceInfo = new PciDeviceInfoLine();

        for (int i = 0; i < PciInfo.getCount(); i++) {
            int vendorId = PciInfo.getVendorId(i);
            int deviceId = PciInfo.getDeviceId(i);
            String address = PciInfo.getAddress(i);
            String vendorName = pci.vendorName(vendorId);
            String deviceName = pci.deviceName(vendorId, deviceId);

            pciDeviceInfo.addVendorId(vendorId);
            pciDeviceInfo.addDeviceId(deviceId);
            pciDeviceInfo.addAddress(address);
            pciDeviceInfo.addVendorName(vendorName);
            pciDeviceInfo.addDeviceName(deviceName);

            if (vendorName == null) {
                System.out.printf(
                    ANSI_YELLOW + "  %s  %04x:%04x  %04x%n" + ANSI_RESET,
                    pciDeviceInfo.getAddresses().get(i),
                    pciDeviceInfo.getVendorIds().get(i),
                    pciDeviceInfo.getDeviceIds().get(i),
                    pciDeviceInfo.getVendorIds().get(i)
                );
            } else {
                System.out.printf(
                    ANSI_YELLOW + "  %s  %04x:%04x  %s %s%n" + ANSI_RESET,
                    pciDeviceInfo.getAddresses().get(i),
                    pciDeviceInfo.getVendorIds().get(i),
                    pciDeviceInfo.getDeviceIds().get(i),
                    pciDeviceInfo.getVendorNames().get(i),
                    pciDeviceInfo.getDeviceNames().get(i)
                );
            }
        }

        System.out.println();

        pciDeviceInfo.getVendorSummary();
    }
}
