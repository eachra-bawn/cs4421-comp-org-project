import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class SQLiteReader {

    public static void main(String[] args) {
        String ANSI_RESET = "\u001B[0m"; // reset
        String ANSI_GREEN = "\u001B[48;2;124;204;87m"; // headers
        String ANSI_DARK_BLUE = "\u001B[34m"; // cpu
        String ANSI_PINK = "\u001B[38;2;255;20;147m"; // memory
        String ANSI_CYAN = "\u001B[38;2;0;255;255m"; // USB devices
        String ANSI_YELLOW = "\u001B[38;2;255;215;0m"; // pci
        
        Scanner obj = new Scanner(System.in);
        String option = "x";
        String url = "jdbc:sqlite:system.db";

        while(!option.equals("1") && !option.equals("0") && !option.equals("q")){
            System.out.println(ANSI_GREEN + "Welcome to the database file reader!" + ANSI_RESET);
            System.out.println(ANSI_DARK_BLUE + "Press 1 to see database records" + ANSI_RESET);
            System.out.println(ANSI_DARK_BLUE + "Press 0 to clear database" + ANSI_RESET);
            System.out.println(ANSI_DARK_BLUE + "Press q to quit" + ANSI_RESET);
            option = obj.nextLine();
        }

        if(option.equals("1")){
            System.out.println("Printing Records:");
            
            String sql = "SELECT * FROM systemlogs";
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
                        String date = rs.getString("date");
                        String time = rs.getString("time");

                        System.out.println(ANSI_GREEN + "RecordNo: " + entry + ANSI_RESET);
                        System.out.println(ANSI_GREEN + "Date: " + date + "| Time: " + time + ANSI_RESET);
                        System.out.println(ANSI_DARK_BLUE + "CPU Info: CPU Model: " + cpuModel + " | CPU Cores: " + cpuCores + ANSI_RESET);
                        System.out.println(ANSI_PINK + "Memory Info: Total Memory: " + totalMemory + "GB | Memory Used: " + memoryUsed + "GB | Memory Used As A Percentage: " + memoryPercent + "%" + ANSI_RESET);
                        System.out.println(ANSI_YELLOW + "PCI Info: Number of PCI Devices Connected: " + pciDeviceCount + ANSI_RESET);
                        System.out.println(ANSI_CYAN + "Number of USB Devices Connected: " + usbDeviceCount + ANSI_RESET);
                        System.out.println("============================================");
                    }

                }
                
            
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else if(option.equals("0")){
            System.out.println("Deleting Records");
            String sql = "DELETE FROM systemlogs";

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
    obj.close();
    }
}
