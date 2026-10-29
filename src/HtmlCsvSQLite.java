import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class HtmlCsvSQLite {

    public static void makeHtml() throws IOException {
        String ANSI_RESET = "\u001B[0m"; //reset
        String ANSI_GREEN = "\u001B[48;2;124;204;87m"; //headers
        // THIS SECTION DOES ALL THE HTML STUFF

        // get CPU information for silly html file
        String cpuModel = CpuInfo.getModel();
        String cpuCores = String.valueOf(CpuInfo.getCount());
        double[][] workload = HelperMethods.workloadPerCore();
    
        String html = Files.readString(Path.of("src/HtmlTemp.html"));

        DateTimeFormatter datef = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter timef = DateTimeFormatter.ofPattern("HH:mm");

        LocalDateTime now = LocalDateTime.now();
        String date = now.format(datef);
        String time = now.format(timef);



        html = html.replace("{{CPU_MODEL}}", cpuModel);
        html = html.replace("{{CPU_CORES}}", cpuCores);

        html = html.replace("{{DATE}}", date);
        html = html.replace("{{TIME}}", time);

        // goes through each cpu core nd displays each ones busy + idle numbers
        String jsonCpuCores = "{\"Cores\":[";
        for (int i = 0; i < workload.length; i++) {
            double busy = workload[i][0];
            double idle = workload[i][1];
            double percent = workload[i][2];
            String percentRounded = String.format("%.2f", percent);

            html += "<div class='centeredDiv'>";
            html += "<h2>CPU Core " + i + "</h2>";
            html += "<p>Busy: " + busy + "</p>";
            html += "<p>Idle: " + idle + "</p>";
            html += "<p>Percentage Busy: " + percentRounded + "%</p>";
            html += "</div>";

            jsonCpuCores += "{";
            jsonCpuCores += "\"Core\": " + i + ",";
            jsonCpuCores += "\"Busy\": " + busy + ",";
            jsonCpuCores += "\"Idle\": " + idle + ",";
            jsonCpuCores += "\"Percentage Busy\": " + percentRounded;
            jsonCpuCores += "}";

            if (i < workload.length - 1) {
                jsonCpuCores += ",";
            }
        }
        jsonCpuCores += "]}";

        // This handles memory
        String[] memoryInfo = HelperMethods.getRAMInfo();
        html = html.replace("{{MEM_USED}}", memoryInfo[1]);
        html = html.replace("{{MEM_TOTAL}}", memoryInfo[0]);
        html = html.replace("{{MEM_PERCENT}}", memoryInfo[2]);

        // This handles USBInfo
        int usbCountint = UsbInfo.getCount();
        String usbCountString = String.format("%d", usbCountint);
        html = html.replace("{{USB_COUNT}}", usbCountString);

        String[][] usbinfo = HelperMethods.printUSBInfo();

        String jsonUsbDevices = "{\"Usb Devices\":[";

        for (int i = 0; i < usbinfo.length; i++) {
            String busNo = usbinfo[i][0];
            String deviceNo = usbinfo[i][1];
            String vendor = usbinfo[i][2];
            String product = usbinfo[i][3];
            String vendorName = usbinfo[i][4];
            String vendorProduct = usbinfo[i][5];

            html += "<div class='centeredDiv'>";
            html += "<h2>USB " + (i + 1) + "</h2>";
            html += "<p>USB Bus Number: " + busNo + "</p>";
            html += "<p>USB Device Number: " + deviceNo + "</p>";
            html += "<p>Vendor Id: " + vendor + "</p>";
            html += "<p>Product Id: " + product + "</p>";
            html += "<p>Vendor Name: " + vendorName + "</p>";
            html += "<p>Vendor Product: " + vendorProduct + "</p>";
            html += "</div>";

            jsonUsbDevices += "{";
            jsonUsbDevices += "\"USB\":" + (i + 1) + ",";
            jsonUsbDevices += "\"Bus Number\": " + busNo + ",";
            jsonUsbDevices += "\"Device Number\": " + deviceNo + ",";
            jsonUsbDevices += "\"Vendor & Product Number\": \"" + vendor + ":" + product + "\",";
            jsonUsbDevices += "\"Vendor Name\": \"" + vendorName + "\",";
            jsonUsbDevices += "\"Vendor Product\": \"" + vendorProduct + "\"";
            jsonUsbDevices += "}";

            if (i < usbinfo.length - 1) {
                jsonUsbDevices += ",";
            }
        }
        jsonUsbDevices += "]}";

        // This handles PCI Devices
        int pciCountInt = PciInfo.getCount();
        String pciCountStr = String.format("%d", pciCountInt);
        html = html.replace("{{PCI_COUNT}}", pciCountStr);

        String[][] pciInfo = HelperMethods.getPciInfo();

        String jsonPCIDevices = "{\"PCI Devices\":[";
        for (int i = 0; i < pciInfo.length; i++) {
            String address = pciInfo[i][0];
            String vendorID = pciInfo[i][1];
            String deviceID = pciInfo[i][2];
            String name = pciInfo[i][3];
            String deviceName = pciInfo[i][4];

            html += "<div class='centeredDiv'>";
            html += "<h2>PCI Device " + (i + 1) + "</h2>";
            html += "<p>PCI Address: " + address + "</p>";
            html += "<p>PCI Vendor ID: " + vendorID + "</p>";
            html += "<p>PCI Device ID: " + deviceID + "</p>";
            html += "<p>PCI Name: " + name + "</p>";
            html += "<p>PCI Device Name: " + deviceName + "</p>";
            html += "</div>";

            jsonPCIDevices += "{";
            jsonPCIDevices += "\"PCI\" : " + (i + 1) + ",";
            jsonPCIDevices += "\"PCI Address\": \"" + address + "\",";
            jsonPCIDevices += "\"PCI Vendor & Device ID\": \"" + vendorID + ":" + deviceID + "\",";
            jsonPCIDevices += "\"PCI Name\": \"" + name + "\",";
            jsonPCIDevices += "\"PCI Device Name\": \"" + deviceName + "\"";
            jsonPCIDevices += "}";

            if (i < pciInfo.length - 1) {
                jsonPCIDevices += ",";
            }
        }
        jsonPCIDevices += "]}";

        Files.writeString(Path.of("HtmlReport.html"), html);

        // THIS SECTION DOES ALL THE CSV STUFF
        System.out.println(ANSI_GREEN + "This Machine has " + cpuCores + " and " + memoryInfo[0] + " GiB of RAM, of which" + memoryInfo[1] +"GiB is in use"+ ANSI_RESET);

        String csv =
            "CPU_Model,CPU_Cores,CPU_Usage_per_core,Total_Memory,Memory_Used,Memory_Used_As_A_Percentage,Number_Of_USB_Devices,USB_Devices,Number_of_PCI_Devices,PCI_Devices,Date,Time\n" +
            "\"" +
            cpuModel +
            "\"," +
            "\"" +
            cpuCores +
            "\"," +
            "\"" +
            jsonCpuCores.replace("\"", "\"\"") +
            "\"," +
            "\"" +
            memoryInfo[0] +
            "GB" +
            "\"," +
            "\"" +
            memoryInfo[1] +
            "GB" +
            "\"," +
            "\"" +
            memoryInfo[2] +
            "%" +
            "\"," +
            "\"" +
            usbCountString +
            "\"," +
            "\"" +
            jsonUsbDevices.replace("\"", "\"\"") +
            "\"," +
            "\"" +
            pciCountInt +
            "\"," +
            "\"" +
            jsonPCIDevices.replace("\"", "\"\"") +
            "\"," +
            "\"" +
            date +
            "\"," +
            "\"" +
            time +
            "\"\n";

        Files.writeString(Path.of("cpu_data.csv"), csv);

        // THIS SECTION DOES ALL THE SQLITE STUFF

        String url = "jdbc:sqlite:system.db";
        

        String sql = "INSERT INTO systemlogs (cpu_model, cpu_cores, cpu_cores_json, total_memory, memory_used, memory_used_percent, usb_device_count, usb_devices, pci_device_count, pci_devices, date, time) " +
        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

        try (var connect = DriverManager.getConnection(url);
            PreparedStatement ps = connect.prepareStatement(sql)){

            ps.setString(1, cpuModel);
            ps.setInt(2, Integer.parseInt(cpuCores));
            ps.setString(3, jsonCpuCores);
            ps.setDouble(4, Double.parseDouble(memoryInfo[0]));
            ps.setDouble(5, Double.parseDouble(memoryInfo[1]));
            ps.setDouble(6, Double.parseDouble(memoryInfo[2]));
            ps.setInt(7, Integer.parseInt(usbCountString));
            ps.setString(8, jsonUsbDevices);
            ps.setDouble(9, pciCountInt);
            ps.setString(10, jsonPCIDevices);
            ps.setString(11, date);
            ps.setString(12, time);
            ps.executeUpdate();
            }
        catch(Exception e){
            e.printStackTrace();
        }



//CREATE TABLE systemlogs (
//    id INTEGER PRIMARY KEY AUTOINCREMENT,
//    cpu_model TEXT,
//    cpu_cores INTEGER,
//    cpu_cores_json TEXT,
//    total_memory TEXT,
//    memory_used TEXT,
//    memory_used_percent TEXT,
//    usb_device_count INTEGER,
//    usb_devices TEXT,
//    pci_device_count REAL,
//    pci_devices TEXT,
//    date TEXT,
//    time TEXT
//);
    }
}
