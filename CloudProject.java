
package org.cloudbus.cloudsim.examples;

import java.awt.SystemColor;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class CloudProject {

    /** The cloudlet list. */
    private static List<Cloudlet> cloudletList;

    /** The vmlist. */
    private static List<Vm> vmlist;

    private static boolean printDebug = false;

    private static Integer vmType[];

    private static List<Vm> createVM(int userId, int vms) {
        Random rand = new Random();
        rand.setSeed(10);

        // Creates a container to store VMs.
        LinkedList<Vm> list = new LinkedList<Vm>();

        int pPowers[] = { 512, 1024, 2048, 4096 };

        // VM Parameters
        vmType = new Integer[vms];
        long size = 10000; // image size (MB)
        int ram = 512; // vm memory (MB)
        long bw = 1000;
        int pesNumber = 1; // number of cpus
        String vmm = "Xen"; // VMM name

        // create VMs
        Vm[] vm = new Vm[vms];

        for (int i = 0; i < vms; i++) {

            int mips = rand.nextInt(4);
            vm[i] = new Vm(i, userId, pPowers[mips], pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            vmType[i] = (mips);
            list.add(vm[i]);
        }

        return list;
    }

    private static List<Cloudlet> createCloudlet(int userId, int cloudlets) {
        // Creates a container to store Cloudlets
        LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();

        // random State
        Random rand = new Random();
        rand.setSeed(99);

        // cloudlet parameters
        long length = 1000; // million inst
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        Cloudlet[] cloudlet = new Cloudlet[cloudlets];
        for (int i = 0; i < cloudlets; i++) {
            cloudlet[i] = new Cloudlet(i, length + rand.nextInt(2000), pesNumber, fileSize + rand.nextInt(200),
                    outputSize + rand.nextInt(200), utilizationModel, utilizationModel, utilizationModel);
            // setting the owner of these Cloudlets
            cloudlet[i].setUserId(userId);
            list.add(cloudlet[i]);
        }
        return list;
    }

    /**
     * main() to run
     */
    public static void main(String[] args) {
        Log.printLine("Starting CloudProject...");

        int vmCount = 15;
        int cloudletCount = 400;
        Scanner myObjin = new Scanner(System.in); // Create a Scanner object
        System.out.println("Fuzzy Mode :(true/false) : ");
        boolean isFuzzyMode = myObjin.nextBoolean();
        System.out.println("Number of VM's : ");
        vmCount = myObjin.nextInt();

        System.out.println("Number of Cloudlet's : ");
        cloudletCount = myObjin.nextInt();

        try {
            File myObj = new File("filename.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try {
            FileWriter myWriter = new FileWriter("filename.txt");
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try {
            // First step: Initialize the CloudSim package. It should be called
            // before creating any entities.
            int num_user = 1; // number of grid users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false; // mean trace events

            // Initialize the CloudSim library
            CloudSim.init(num_user, calendar, trace_flag);

            // Second step: Create Datacenters
            @SuppressWarnings("unused")
            Datacenter datacenter0 = createDatacenter("Datacenter_0");

            // Third step: Create Broker
            DatacenterBroker broker = createBroker();
            int brokerId = broker.getId();

            // Fourth step: Create VMs and Cloudlets and send them to broker
            vmlist = createVM(brokerId, vmCount); // creating 20 vms
            cloudletList = createCloudlet(brokerId, cloudletCount); // creating 40 cloudlets

            broker.submitVmList(vmlist);
            broker.submitCloudletList(cloudletList);

            int size = cloudletList.size();
            try {
                FileWriter myWriter = new FileWriter("filename.txt");

                Cloudlet cloudlet;
                myWriter.write(size + "\n");

                for (int i = 0; i < size; i++) {

                    cloudlet = cloudletList.get(i);
                    long a = cloudlet.getCloudletLength(),
                            b = cloudlet.getCloudletFileSize() + cloudlet.getCloudletOutputSize();
                    myWriter.write(a + " " + b + "\n");

                }

                myWriter.close();
                System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            try {
                // Command to create an external process
                String command = "D:\\Labs\\Sem6\\Cloudproject\\cloudsim-3.0.3\\fuzzyQoS.exe";

                // Running the above command
                Runtime run = Runtime.getRuntime();
                Process proc = run.exec(command);
            }

            catch (IOException e) {
                e.printStackTrace();
            }
            Thread.sleep(1000);

            int cloudletVmType[] = new int[size];
            int i = 0;
            try {
                File myObj = new File("out.txt");
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    String s1 = "nan";
                    if (data.equals(s1))
                        cloudletVmType[i] = 3;
                    else {
                        cloudletVmType[i] = (int) (Double.parseDouble(data));
                    }

                    i++;
                }
                myReader.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            if (printDebug) {
                System.out.println("Allocated VM's : ");
                for (i = 0; i < size; i++) {
                    System.out.println(cloudletVmType[i]);
                }
            }

            int typeCount[] = { 0, 0, 0, 0 };
            for (i = 0; i < vmCount; i++) {
                typeCount[vmType[i]]++;
            }

            int vmPool[][] = new int[4][];
            for (i = 0; i < 4; i++) {
                int k = 0;
                vmPool[i] = new int[typeCount[i]];
                for (int j = 0; j < vmCount; j++) {

                    if (vmType[j] == i) {
                        vmPool[i][k] = j;
                        k++;
                    }
                }
            }
            System.out.println("***");
            System.out.println("Each VM's Type : ");
            for (i = 0; i < 4; i++) {
                System.out.print("Type " + i + " VM's : ");
                for (int j : vmPool[i]) {
                    System.out.print(j + " ");
                }
                System.out.println(" ");
            }

            System.out.println(" Beganning Resource Allocation ");
            int vmTypeInd[] = { 0, 0, 0, 0 };

            if (isFuzzyMode)
                for (i = 0; i < size; i++) {
                    int type = cloudletVmType[i];
                    broker.bindCloudletToVm(i, vmPool[type][vmTypeInd[type]]);
                    vmTypeInd[type] = (vmTypeInd[type] + 1) % typeCount[type];

                }

            // Fifth step: Starts the simulation
            CloudSim.startSimulation();

            // Final step: Print results when simulation is over
            List<Cloudlet> newList = broker.getCloudletReceivedList();

            CloudSim.stopSimulation();

            printCloudletList(newList);

            Log.printLine("CloudProject finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }
    }

    private static Datacenter createDatacenter(String name) {

        // 1. We need to create a list to store one or more
        // Machines
        List<Host> hostList = new ArrayList<Host>();

        // 2. A Machine contains one or more PEs or CPUs/Cores. 
        List<Pe> peList1 = new ArrayList<Pe>();

        int mips = 9000;

        // 3. Create PEs and add these into the list.
        peList1.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
        peList1.add(new Pe(1, new PeProvisionerSimple(mips)));
        peList1.add(new Pe(2, new PeProvisionerSimple(mips)));
        peList1.add(new Pe(3, new PeProvisionerSimple(mips)));

        // 4. Create Hosts with its id and list of PEs and add them to the list of
        // machines
        int hostId = 0;
        int ram = 40048; // host memory (MB)
        long storage = 10000000; // host storage
        int bw = 100000;

        hostList.add(new Host(hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList1,
                new VmSchedulerTimeShared(peList1))); // This is our first machine

        // 5. Create a DatacenterCharacteristics object that stores the
        // properties of a data center: architecture, OS, list of
        String arch = "x86"; // system architecture
        String os = "Linux"; // operating system
        String vmm = "Xen";
        double time_zone = 10.0; // time zone this resource located
        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.1; // the cost of using storage in this resource
        double costPerBw = 0.1; // the cost of using bw in this resource
        LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN devices by now

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(arch, os, vmm, hostList, time_zone,
                cost, costPerMem, costPerStorage, costPerBw);

        // 6. Finally, we need to create a PowerDatacenter object.
        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datacenter;
    }

    private static DatacenterBroker createBroker() {

        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return broker;
    }

    private static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;

        try {
            FileWriter myWriter = new FileWriter("fuzzy.csv");

            myWriter.write(size + "\n");

            String indent = "    ";
            Log.printLine();
            Log.printLine("========== OUTPUT ==========");
            Log.printLine("Cloudlet ID" + indent + "STATUS" + indent + "VM ID" + indent + indent + "Execution Time");

            DecimalFormat dft = new DecimalFormat("###.##");
            double sum = 0, count = 0, maxx = 0;
            for (int i = 0; i < size; i++) {
                cloudlet = list.get(i);
                Log.print(indent + cloudlet.getCloudletId() + indent + indent);

                if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                    myWriter.write(cloudlet.getFinishTime() + "\n");

                    Log.print("SUCCESS");
                    sum += cloudlet.getFinishTime();
                    count += 1;

                    maxx = cloudlet.getFinishTime();

                    Log.printLine(indent + indent + cloudlet.getVmId() + indent + indent + indent
                            + dft.format(cloudlet.getActualCPUTime()));
                }
            }

            Log.print("\n\n*** Summary Stats ***\n\n");

            System.out.println("Average Compeletion Time(Sec) : " + sum / count);
            System.out.println("Process Completion Time : " + maxx);

            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
}
