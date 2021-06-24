
package com.project;

import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import com.project.Route;
import com.project.Time;
public class Main {

    private static final List<Route> routeList = new ArrayList<>();
    private static String path = null;
    private static String outpath = null;

    public static void main(String[] args) {
        path = args[0];
        System.out.println("Started");
        if(checkFiles()!=0){       // "checkFiles()" checks if sourse file exists and deletes output.txt if it exists (to create new)
            System.out.print("Failure");
            return;
        }

        List<String> list = readLinesFromFile();    //all data from file now represented as list
        List<Route> buffer = null;

       if(getRoutes(list)==1){ //method forms List<Routes> from the lines (parse string for data)
            return;
        }     

        int err = checkIfCorrect(routeList); //check if there are some errors; returns 0 if everything is ok
                                                                            // and index of line with error if not
        if(err!=0){
            System.out.println("Check the time in this string and try again:\n"+routeList.get(--err).getString());
            System.out.print("Failure");
            return;
        }

        filterRoutes(); //checks for unnecessary lines and delete them

        buffer = divideByName("Posh"); //filters list by company name
        sortRightOrder(buffer); //sorting by time
        write(buffer);         //writing to a file

        buffer = divideByName("Grotty");
        sortRightOrder(buffer);
        write(buffer);
        System.out.print("Success: File generated: "+outpath);
        return;
    }
    public static List<String>  readLinesFromFile(){
        List<String> list=null;
        try {
            list = Files.readAllLines(Path.of(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
    public static int checkFiles(){

        if (!Files.exists(Path.of(path))) {
            System.out.println("File doesn't exists, check arguments");
            return 1;
        }

        File f = new File(path);
        outpath = f.getAbsolutePath();
        outpath = outpath.replace(f.getName(),"output.txt");

        if (Files.exists(Path.of(outpath))) {

            try {
                Files.delete(Path.of(outpath));

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return 0;
    }
    public static int checkIfCorrect(List<Route> list){
        int counter=1;
        for(Route route:list){
            //check for correct time format
           if(route.getDep().getHours()>=24 || route.getDep().getMins()>=60 ||route.getArr().getHours()>=24 || route.getArr().getMins()>=60 ) return counter;
            //departure time must be less than the arrival time
           if(calculateTimeInMins(route.getDep())>calculateTimeInMins(route.getArr())) return counter;
           //departure time equals to the arrival time
            if(calculateTimeInMins(route.getDep())==calculateTimeInMins(route.getArr())) return counter;
           counter++;
        }
        return 0;
    }
    public static int getRoutes(List<String> list) {


        for (String str : list) {
            int blockCount = str.split(" +").length;
            if (blockCount > 3) {
                System.out.println("Wrong arguments: more words then expected.");
            } else {

                String[] words = str.split(" ");
                try {
                    routeList.add(new Route(words[0], new Time(words[1]), new Time(words[2])));
                } catch (Exception e) {
                    System.out.println("Failed: Check time format");
                    return 1;
                }
            }
        }
        return 0;
    }

    public static void filterRoutes() {
        int counter = 0;


        Iterator<Route> itr = routeList.iterator();
        Iterator<Route> itr2 = routeList.iterator();
        ;
        int routeTime = 0;
        int routeTimeArr = 0;
        int anotherRouteTime = 0;
        int anotherRouteTimeArr = 0;
        while (itr.hasNext()) {
            Route route = itr.next();
            routeTime = calculateTimeInMins(route.getDep());
            routeTimeArr = calculateTimeInMins(route.getArr());

            while (itr2.hasNext()) {
                Route anotherRoute = itr2.next();
                // .equals
                if (route.equals(anotherRoute)){
                    continue;
                }


                anotherRouteTime = calculateTimeInMins(anotherRoute.getDep());
                anotherRouteTimeArr = calculateTimeInMins(anotherRoute.getArr());

                if(anotherRouteTimeArr-anotherRouteTime>60){
                    itr2.remove();
                    itr = routeList.iterator();
                    continue;
                }

                if (anotherRouteTime <= routeTime && anotherRouteTimeArr >= routeTimeArr) { //check if route#1 fits in route#2
                    if (anotherRouteTime == routeTime && anotherRouteTimeArr == routeTimeArr){
                        if(anotherRoute.getName().equals("Grotty")){ //priority to Posh
                            itr2.remove();
                            itr = routeList.iterator();
                        }else{
                            continue;
                        }
                    }else{
                        itr2.remove();      //removing unnecessary route from list
                        itr = routeList.iterator();
                    }

                }


            }
            itr2 = routeList.iterator();

        }
    }

    public static List<Route> divideByName(String name) {
        List<Route> retList = new ArrayList<>();
        retList.addAll(routeList.stream().filter(obj -> obj.getName().equals(name)).collect(Collectors.toList()));
        return retList;
    }

    public static void printRouteList(List<Route> list) {
        for (Route n : list)
            System.out.println(n.getName() + " " + n.getDep().getHours() + ":" + n.getDep().getMins() + " " + n.getArr().getHours() + ":" + n.getArr().getMins());


    }

    public static void sortRightOrder(List<Route> list) {

        Collections.sort(list, new Comparator<Route>() {
            public int compare(Route o1, Route o2) {
                return calculateTimeInMins(o1.getDep()) - calculateTimeInMins(o2.getDep());
            }
        });

    }

    public static int calculateTimeInMins(Time time) {
        return time.getHours() * 60 + time.getMins();
    }

    public static void write(List<Route> list) {
        if (!Files.exists(Path.of(outpath))) {

            try {
                Files.createFile(Path.of(outpath));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else { //if file is already exists it means that it is second call of "write" method, so i need insert a blank line to divide Posh and Grotty
            try {
                Files.write(Path.of(outpath), "\n".getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        for (Route route : list) {

            try {
                Files.write(Path.of(outpath), (route.getString()).getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


}


