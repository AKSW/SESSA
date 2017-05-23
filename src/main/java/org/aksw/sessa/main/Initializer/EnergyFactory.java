
package org.aksw.sessa.main.Initializer;


public class EnergyFactory {
    public EnergyFunction getEnergyFunction(String EnergyType){
        if(EnergyType.equals("LevDist")){
         return new LevDist();   
        }
        else
            return null;
    }
}
