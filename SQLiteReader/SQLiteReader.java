import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class SQLiteReader {
    public static void main(String[] args) {
        Scanner obj = new Scanner(System.in);
        String option = "x";
        String url = "jdbc:sqlite:system.db";

        while(!option.equals("1") && !option.equals("0") && !option.equals("q")){
            System.out.println("Welcome to the database file reader!");
            System.out.println("Press 1 to see database records");
            System.out.println("Press 0 to clear database");
            System.out.println("Press q to quit");
            option = obj.nextLine();
        }

        if(option.equals("1")){
            System.out.println("Printing Records:");
            
            String sql = "SELECT * FROM system_info";
            try (var connect = DriverManager.getConnection(url);
                    var st = connect.createStatement();
                    var rs = st.executeQuery(sql)){

                    while(rs.next()){
                        String entry = rs.getString("id");
                        String cpuModel = rs.getString("cpu_model");
                        String cpuCores = rs.getString("cpu_cores");

                        String totalMemory = rs.getString("total_memory");
                        String memoryUsed = rs.getString("memory_used");
                        String memoryPercent = rs.getString("memory_used_percent");
                        String usbDeviceCount = rs.getString("usb_device_count");
                        String pciDeviceCount = rs.getString("pci_device_count");

                        System.out.println("============================================");
                        System.out.println("RecordNo: " + entry);
                        System.out.println("CPU Info: CPU Model: " + cpuModel + " | CPU Cores: " + cpuCores);
                        System.out.println("Memory Info: Total Memory: " + totalMemory + "GB | Memory Used: " + memoryUsed + "GB | Memory Used As A Percentage: " + memoryPercent + "%");
                        System.out.println("PCI Info: Number of PCI Devices Connected: " + pciDeviceCount);
                        System.out.println("Number of USB Devices Connected: " + usbDeviceCount);

                    }

                }
                
            
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else if(option.equals("0")){
            System.out.println("Deleting Records");
            String sql = "DELETE FROM system_info";

            try (var connect = DriverManager.getConnection(url);
                PreparedStatement ps = connect.prepareStatement(sql)){
                ps.executeUpdate();

                System.out.println("Table has been cleared");
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else if(option.equals("q")){
            System.out.println("Goodbye");
            System.exit(0);
        }
    }
}
