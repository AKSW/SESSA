
package org.aksw.sessa.main.Initializer;

import org.aksw.sessa.main.Initializer.interfaces.EnergyFunction;

public class EnergyFactory {
    public EnergyFunction getEnergyFunction(String EnergyType){
        if(EnergyType.equals("LevDist")){
         return new LevDist();   
        }
        else
            return null;
    }
}
