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
        HtmlMaker.makeHtml();
    }

    private static void printCpuInfo() {
        System.out.println("cores: " + CpuInfo.getCount());
        System.out.println("model: " + CpuInfo.getModel());

        Core[] cpuCores = new Core[CpuInfo.getCount()];

        for (int i = 0; i < CpuInfo.getCount(); i++) {
            cpuCores[i] = new Core(CpuInfo.getBusy(i), CpuInfo.getIdle(i));
        }

        System.out.println("GATHERING DATA --- 1s");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restores interrupted status
        }

        System.out.print("\033[1A\033[2K"); // moves the cursor up by one line and clears it
        System.out.flush(); // forces the text out of the buffer onto the screen immediately

        double sumLoad = 0;
        for (int i = 0; i < CpuInfo.getCount(); i++) {
            long beforeBusy = cpuCores[i].beforeBusy;
            long beforeIdle = cpuCores[i].beforeIdle;
            long secondBusy = CpuInfo.getBusy(i) - beforeBusy; // counts how many ticks a core was busy in 1s
            long secondIdle = CpuInfo.getIdle(i) - beforeIdle; // counts how many ticks a core was idle in 1s
            double busyRatio = ((double) secondBusy / (secondBusy + secondIdle)) * 100; // multiplied by 100 to get a
            // percentage

            System.out.println(
                "core " +
                    i +
                    ": busy=" +
                    (secondBusy + beforeBusy) +
                    " idle=" +
                    (secondIdle + beforeIdle) +
                    "  (" +
                    String.format("%.2f", busyRatio) +
                    "%)"
            );

            sumLoad += busyRatio;
        }

        double avgLoad = sumLoad / CpuInfo.getCount();
        System.out.println(
            "Average load across all cores is " + String.format("%.2f", avgLoad) + "%."
        );
    }

    // printMemory now displays data as GibiBytes instead of kibibytes, because
    // kibibytes is a silly way to display memory in 2026, - Luke-F-W
    private static void printMemoryInfo() {
        double percentageUsed = ((double) MemInfo.getUsed() / (double) MemInfo.getTotal()) * 100.0;
        System.out.println(
            "memory: total = " +
                String.format("%.2f", HelperMethods.kibibytesToGibibyes(MemInfo.getTotal())) +
                " GiB, used = " +
                String.format("%.2f", HelperMethods.kibibytesToGibibyes(MemInfo.getUsed())) +
                " GiB (" +
                String.format("%.2f", percentageUsed) +
                "%)"
        );
    }

    private static void printPCIInfoByVendor() {
        IdLookup pci = null;

        try {
            pci = IdLookup.pci();
        } catch (IOException e) {
            System.out.println("Could not look up PCI devices: " + e);
            return;
        }

        System.out.println("PCI devices: " + PciInfo.getCount());

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
                    "  %s  %04x:%04x  %04x%n",
                    pciDeviceInfo.getAddresses().get(i),
                    pciDeviceInfo.getVendorIds().get(i),
                    pciDeviceInfo.getDeviceIds().get(i),
                    pciDeviceInfo.getVendorNames().get(i),
                    pciDeviceInfo.getVendorIds().get(i)
                );
            } else {
                System.out.printf(
                    "  %s  %04x:%04x  %s %s%n",
                    pciDeviceInfo.getAddresses().get(i),
                    pciDeviceInfo.getVendorIds().get(i),
                    pciDeviceInfo.getDeviceIds().get(i),
                    pciDeviceInfo.getVendorNames().get(i),
                    pciDeviceInfo.getDeviceNames().get(i)
                );
            }
        }

        pciDeviceInfo.getVendorSummary();
    }

    private static void printPCIInfoByBus() {
        IdLookup pci = null;

        try {
            pci = IdLookup.pci();
        } catch (IOException e) {
            System.out.println("Could not look up PCI devices: " + e);
            return;
        }

        System.out.println("PCI devices: " + PciInfo.getCount());

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
                    "  %s  %04x:%04x  %04x%n",
                    pciDeviceInfo.getAddresses().get(i),
                    pciDeviceInfo.getVendorIds().get(i),
                    pciDeviceInfo.getDeviceIds().get(i),
                    pciDeviceInfo.getVendorIds().get(i)
                );
            } else {
                System.out.printf(
                    "  %s  %04x:%04x  %s %s%n",
                    pciDeviceInfo.getAddresses().get(i),
                    pciDeviceInfo.getVendorIds().get(i),
                    pciDeviceInfo.getDeviceIds().get(i),
                    pciDeviceInfo.getVendorNames().get(i),
                    pciDeviceInfo.getDeviceNames().get(i)
                );
            }
        }

        pciDeviceInfo.getVendorSummary();
    }

    private static void printUSBInfo() {
        IdLookup usb = null;

        try {
            usb = IdLookup.usb();
        } catch (IOException e) {
            System.out.println("Could not look up USBs: " + e);
            return;
        }

        if (UsbInfo.getCount() == 0) {
            System.out.println("USB devices: no USB devices found.");
        } else {
            System.out.println("USB devices: " + UsbInfo.getCount());

            for (int i = 0; i < UsbInfo.getCount(); i++) {
                int vendor = UsbInfo.getVendorId(i);
                int product = UsbInfo.getProductId(i);
                String vendorName = usb.vendorName(vendor);
                String deviceName = usb.deviceName(vendor, product);

                if (vendorName == null) {
                    System.out.printf(
                        "  Bus %03d Device %03d: ID %04x:%04x  %d%n",
                        UsbInfo.getBusNumber(i),
                        UsbInfo.getDeviceNumber(i),
                        vendor,
                        product,
                        vendorName
                    );
                } else {
                    System.out.printf(
                        "  Bus %03d Device %03d: ID %04x:%04x  %s %s%n",
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
    }
}
