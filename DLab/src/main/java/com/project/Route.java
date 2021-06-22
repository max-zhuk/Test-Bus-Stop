package com.project;

public class Route {
    private String name;
    private Time dep;
    private Time arr;


    public Route(String name, Time dep, Time arr) {
        this.name = name;
        this.dep = dep;
        this.arr = arr;
    }

    public String getString() {
        String minsD="";
        String minsA ="";
        if(dep.getMins()<10){
            minsD ="0"+dep.getMins();
        } else minsD =String.valueOf(dep.getMins());
        if (arr.getMins()<10){
            minsA="0"+arr.getMins();
        } else minsA =String.valueOf(arr.getMins());

        return name + " "+dep.getHours() +":" +minsD +" "+arr.getHours()+":"+minsA;
    }
    public boolean equals(Route anotherRoute){
        if (this.getName() == anotherRoute.getName() && this.getDep() == anotherRoute.getDep() && this.getArr() == anotherRoute.getArr()) {
            return true;
        }
        return false;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Time getDep() {
        return dep;
    }

    public void setDep(Time dep) {
        this.dep = dep;
    }

    public Time getArr() {
        return arr;
    }

    public void setArr(Time arr) {
        this.arr = arr;
    }
}
