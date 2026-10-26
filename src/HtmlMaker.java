import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HtmlMaker {
    public static void makeHtml() throws IOException{

        // THIS SECTION DOES ALL THE HTML STUFF

        // get CPU information for silly html file
        String cpuModel = cpuInfo.getModel();
        String cpuCores = String.valueOf(cpuInfo.getCount());
        double[][] workload = helpermethods.workloadPerCore();

        
        String html = Files.readString(Path.of("src/HtmlTemp.html"));
        html = html.replace("{{CPU_MODEL}}", cpuModel);
        html = html.replace("{{CPU_CORES}}", cpuCores);

        // goes through each cpu core nd displays each ones busy + idle numbers
        String jsonCpuCores = "{\"Cores\":[";
        for(int i = 0; i < workload.length; i++){
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

            if (i < workload.length - 1){
                jsonCpuCores += ",";
            }
        }
        jsonCpuCores += "]}";

        // This handles memory
        String[] memoryInfo = helpermethods.getRAMInfo();
        html = html.replace("{{MEM_USED}}", memoryInfo[1]);
        html = html.replace("{{MEM_TOTAL}}", memoryInfo[0]);
        html = html.replace("{{MEM_PERCENT}}", memoryInfo[2]);

        // This handles USBInfo
        int usbCountint = usbInfo.getCount();
        String usbCountString = String.format("%d", usbCountint);
        html = html.replace("{{USB_COUNT}}", usbCountString);

        String[][] usbinfo = helpermethods.printUSBInfo();

        String jsonUsbDevices = "{\"Usb Devices\":[";
        for(int i = 0; i < usbinfo.length; i++){

            String busNo = usbinfo[i][0];
            String deviceNo = usbinfo[i][1];
            String vendor = usbinfo[i][2];
            String product = usbinfo[i][3];
            String vendorName = usbinfo[i][4];
            String vendorProduct = usbinfo[i][5];

            html += "<div class='centeredDiv'>";
            html += "<h2>USB " + (i+1) + "</h2>";
            html += "<p>USB Bus Number: " + busNo + "</p>";
            html += "<p>USB Device Number: " + deviceNo + "</p>";
            html += "<p>Vendor & Product: " + vendor + ":" + product+ "%</p>";
            html += "<p>Vendor Name: " + vendorName + "</p>";
            html += "<p>Vendor Product: " + vendorProduct + "</p>";
            html += "</div>";

            jsonUsbDevices += "{";
            jsonUsbDevices += "\"USB\":" + (i+1) + ",";
            jsonUsbDevices += "\"Bus Number\": " + busNo + ",";
            jsonUsbDevices += "\"Device Number\": " + deviceNo + ",";
            jsonUsbDevices += "\"Vendor & Product Number\": \"" + vendor + ":" + product + "\",";
            jsonUsbDevices += "\"Vendor Name\": \"" + vendorName + "\",";
            jsonUsbDevices += "\"Vendor Product\": \"" + vendorProduct + "\"";
            jsonUsbDevices += "}";


            if (i < usbinfo.length - 1){
                jsonUsbDevices += ",";
            }
        }
        jsonUsbDevices += "]}";

        // This handles PCI Devices
        int pciCountInt = pciInfo.getCount();
        String pciCountStr = String.format("%d", pciCountInt);
        html = html.replace("{{PCI_COUNT}}", pciCountStr);

        String[][] pciInfo = helpermethods.getPciInfo();


        String jsonPCIDevices = "{\"PCI Devices\":[";
        for(int i = 0; i < pciInfo.length; i++){

            String address = pciInfo[i][0];
            String vendorID = pciInfo[i][1];
            String deviceID = pciInfo[i][2];
            String name = pciInfo[i][3];
            String deviceName = pciInfo[i][4];

            html += "<div class='centeredDiv'>";
            html += "<h2>PCI Device " + (i+1) + "</h2>";
            html += "<p>PCI Address: " + address + "</p>";
            html += "<p>PCI Vendor and Device ID: " + vendorID + ":" + deviceID + "</p>";
            html += "<p>PCI Name: " + name + "</p>";
            html += "<p>PCI Device Name: " + deviceName + "</p>";
            html += "</div>";

            jsonPCIDevices += "{";
            jsonPCIDevices += "\"PCI\" : " + (i+1) + ",";
            jsonPCIDevices += "\"PCI Address\": \"" + address + "\",";
            jsonPCIDevices += "\"PCI Vendor & Device ID\": \"" + vendorID + ":" + deviceID + "\",";
            jsonPCIDevices += "\"PCI Name\": \"" + name + "\",";
            jsonPCIDevices += "\"PCI Device Name\": \"" + deviceName + "\"";
            jsonPCIDevices += "}";

            if (i < pciInfo.length - 1){
                jsonPCIDevices += ",";
            }
        }
        jsonPCIDevices += "]}";



        Files.writeString(Path.of("HtmlReport.html"), html);

        // THIS SECTION DOES ALL THE CSV STUFF

        String csv = "CPU_Model,CPU_Cores,CPU_Usage_per_core,Total_Memory,Memory_Used,Memory_Used_As_A_Percentage,Number_Of_USB_Devices,USB_Devices,Number_of_PCI_Devices,PCI_Devices\n"
           + "\"" + cpuModel + "\"," + "\"" + cpuCores + "\"," + "\"" + jsonCpuCores.replace("\"", "\"\"") + "\"," + "\"" + memoryInfo[0] + "GB" + "\"," + "\"" + memoryInfo[1] + "GB" + "\"," + "\"" + memoryInfo[2] + "%" + "\"," + "\"" + usbCountString + "\"," + "\"" + jsonUsbDevices.replace("\"", "\"\"") + "\"," + "\"" + pciCountInt + "\"," + "\"" + jsonPCIDevices.replace("\"", "\"\"") + "\"\n";

        Files.writeString(Path.of("cpu_data.csv"), csv);
        
    }
}

