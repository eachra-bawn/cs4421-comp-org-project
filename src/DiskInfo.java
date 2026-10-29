import java.io.File;
import java.io.IOException;
import java.util.Arrays;

//class DiskInfo - prints drives, their partitions and how much space each one uses, has uses in src/Demo.java inside the main method
//Reads /sys/block, which has one folder per drive (sda, nvme0n1 ...) with a folder per partition inside it (sda1, nvme0n1p1 ...)
public class DiskInfo {

    //sizes in /sys are always counted in 512 byte sectors, long because a big drive has more sectors than an int can hold
    private static final long SECTOR = 512;

    public static void printDisks() {
        System.out.println("Drives:");

        //SysFile.path lets -Dhw.root testing work like the other classes
        File[] disks = SysFile.path("/sys/block").toFile().listFiles();

        if (disks == null) { //no /sys/block (windows, mac), listFiles gives null
            System.out.println("  no drives found.\n");
            return;
        }
        Arrays.sort(disks); //folder order is random, sort so the output is always the same

        //pci.ids turns a vendor number into a name, only needed for nvme drives, null means no lookup
        IdLookup pci = null;
        try {
            pci = IdLookup.pci();
        } catch (IOException e) {
            System.out.println("  (could not load the PCI ID list, NVMe vendor names will be missing)");
        }

        for (File disk : disks) {
            String name = disk.getName();

            //loop, ram and zram are not real drives so skip them
            if (name.startsWith("loop") || name.startsWith("ram") || name.startsWith("zram")) continue;

            //vendor and model are read from the drive itself
            String vendor = SysFile.text(disk.toPath().resolve("device/vendor"));
            String model = SysFile.text(disk.toPath().resolve("device/model"));

            //nvme drives have no vendor file, so get the PCI vendor number and look up its name
            if (vendor.isEmpty() && pci != null) {
                long vendorId = SysFile.number(disk.toPath().resolve("device/device/vendor"));
                if (vendorId >= 0) {
                    String found = pci.vendorName((int) vendorId); //null if the number is not in the list
                    if (found != null) vendor = found;
                }
            }

            String label = (vendor + " " + model).strip();
            if (label.isEmpty()) label = "unknown";

            long diskBytes = SysFile.number(disk.toPath().resolve("size")) * SECTOR;
            System.out.println("  " + name + "  " + label + "  " + toGiB(diskBytes));

            //partitions are folders inside the drive folder that start with the drive name
            //allocated = space reserved by partitions, used = space filled with files in mounted partitions
            long allocated = 0;
            long used = 0;
            for (File part : disk.listFiles()) {
                if (!part.getName().startsWith(name)) continue;

                long partBytes = SysFile.number(part.toPath().resolve("size")) * SECTOR;
                allocated += partBytes;
                System.out.println("      " + part.getName() + ": " + toGiB(partBytes));

                String mountPoint = getMountPoint(part.getName()); //null if not mounted
                if (mountPoint == null) {
                    System.out.println("         not mounted, usage unknown");
                    continue;
                }

                //the filesystem mounted there knows its size and free space, total - free = used
                //its total is a bit smaller than the partition, the filesystem keeps some room for itself
                File mount = new File(mountPoint);
                long total = mount.getTotalSpace();
                long partUsed = total - mount.getFreeSpace();

                if (total == 0) { //folder could not be read, avoids dividing by 0
                    System.out.println("         usage unreadable");
                    continue;
                }

                used += partUsed;
                double partPercent = ((double) partUsed / total) * 100;
                System.out.println(
                    "         used = " + toGiB(partUsed) +
                        " of " + toGiB(total) + " (" + String.format("%.2f", partPercent) + "%)"
                );
            }

            System.out.println(
                "      Allocated = " + toGiB(allocated) +
                    ", Unallocated = " + toGiB(diskBytes - allocated)
            );

            //unmounted partitions count as 0 here, so this can be lower than the truth
            if (diskBytes > 0) {
                double diskPercent = ((double) used / diskBytes) * 100;
                System.out.println(
                    "      Drive used = " + toGiB(used) +
                        " of " + toGiB(diskBytes) + " (" + String.format("%.2f", diskPercent) + "%)"
                );
            }
        }
        System.out.println();
    }

    //finds where a partition is attached in the folder tree, /proc/mounts has one line per mount like
    //"/dev/sda2 /home ext4 rw 0 0", the first word is the partition and the second word is the folder
    private static String getMountPoint(String partition) {
        for (String line : SysFile.lines(SysFile.path("/proc/mounts"))) {
            String[] words = line.split(" ");
            if (words[0].equals("/dev/" + partition)) return words[1];
        }
        return null; //partition was not in the file, so it is not mounted
    }

    //bytes to GiB, same unit that printMemoryInfo uses
    private static String toGiB(long bytes) {
        return String.format("%.2f", bytes / 1024.0 / 1024.0 / 1024.0) + " GiB";
    }
}
