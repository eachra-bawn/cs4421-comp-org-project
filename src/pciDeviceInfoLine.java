import java.util.ArrayList;

public class pciDeviceInfoLine {

    ArrayList<Integer> vendorIds;
    ArrayList<Integer> deviceIds;
    ArrayList<String> addresses;
    ArrayList<String> vendorNames;
    ArrayList<String> deviceNames;

    public pciDeviceInfoLine() {
        this.vendorIds = new ArrayList<>();
        this.deviceIds = new ArrayList<>();
        this.addresses = new ArrayList<>();
        this.vendorNames = new ArrayList<>();
        this.deviceNames = new ArrayList<>();
    }

    public void addVendorId(Integer vendorId) {
        this.vendorIds.add(vendorId);
    }

    public void addDeviceId(Integer deviceId) {
        this.deviceIds.add(deviceId);
    }

    public void addAddress(String address) {
        this.addresses.add(address);
    }

    public void addVendorName(String vendorName) {
        this.vendorNames.add(vendorName);
    }

    public void addDeviceName(String deviceName) {
        this.deviceNames.add(deviceName);
    }

    public void sortVendorNames() {
        this.vendorNames.sort(null);
    }

    public ArrayList<Integer> getVendorIds() {
        return this.vendorIds;
    }

    public ArrayList<Integer> getDeviceIds() {
        return this.deviceIds;
    }

    public ArrayList<String> getAddresses() {
        return this.addresses;
    }

    public ArrayList<String> getVendorNames() {
        return this.vendorNames;
    }

    public ArrayList<String> getDeviceNames() {
        return this.deviceNames;
    }

    public void getVendorSummary() {
        System.out.println("PCI Summary:");
        int count = 0;
        ArrayList<String> previous = new ArrayList<>();

        for (String vendorNameOuter : this.vendorNames) {
            for (String vendorNameInner : this.vendorNames) {
                if (vendorNameInner == vendorNameOuter) {
                    count++;
                }
            }

            if (!previous.contains(vendorNameOuter)) {
                System.out.println("  " + vendorNameOuter + ": " + count + " devices");
            }

            previous.add(vendorNameOuter);
            count = 0;
        }
    }
}
