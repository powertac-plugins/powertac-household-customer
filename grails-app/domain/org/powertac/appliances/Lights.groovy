/*
* Copyright 2009-2010 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an
* "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
* either express or implied. See the License for the specific language
* governing permissions and limitations under the License.
*/


package org.powertac.appliances

import java.util.HashMap;
import org.powertac.common.configurations.Constants

/**
* Lights are utilized when the persons inhabiting the house have need 
* for them.So it's a not shifting appliance.
*
* @author Antonios Chrysopoulos
* @version 1, 13/02/2011
*/
class Lights extends NotShiftingAppliance{
@ Override
  def initialize(HashMap hm) {
    
    // Creating Auxiliary Variables
    Random gen = ensureRandomSeed()
    
    // Filling the base variables
    name = "Lights"
    saturation = 1
   
    consumptionShare = (float) (Constants.PERCENTAGE * (Constants.LIGHTS_CONSUMPTION_SHARE_VARIANCE * gen.nextGaussian() + Constants.LIGHTS_CONSUMPTION_SHARE_MEAN))
    baseLoadShare = Constants.PERCENTAGE * Constants.LIGHTS_BASE_LOAD_SHARE
    power = (int) (Constants.LIGHTS_POWER_VARIANCE * gen.nextGaussian() + Constants.LIGHTS_POWER_MEAN)
    cycleDuration = Constants.LIGHTS_DURATION_CYCLE
    times = (float)hm.get("LightsDailyTimes")
    od = false
    inUse = false
    probabilitySeason = fillSeason(Constants.LIGHTS_POSSIBILITY_SEASON_1,Constants.LIGHTS_POSSIBILITY_SEASON_2,Constants.LIGHTS_POSSIBILITY_SEASON_3)
    probabilityWeekday = fillDay(Constants.LIGHTS_POSSIBILITY_DAY_1,Constants.LIGHTS_POSSIBILITY_DAY_2,Constants.LIGHTS_POSSIBILITY_DAY_3)
    
    createWeeklyOperationVector(times + applianceOf.members.size())
    
  }
  
  @ Override
  def fillDailyFunction(int weekday) {
    
    // Initializing and Creating auxiliary variables
    loadVector = new Vector()
    dailyOperation = new Vector()
    Vector operation = operationVector.get(weekday)
    
    // For each quarter of a day
    for (int i = 0;i < Constants.QUARTERS_OF_DAY;i++) {
    
      // case the appliance should begin functioning
      if (operation.get(i) == true) {
    
        // Creating auxiliary variables
        boolean flag = true
        int counter = 0
    
        // While it should be working
        while ((flag) && (i < Constants.QUARTERS_OF_DAY) && (counter >= 0)) {
    
          // case the house is not empty
          if (applianceOf.isEmpty(i+1) == false) {
    
            loadVector.add(power)
            dailyOperation.add(true)
            counter--
    
            // case it doesn't have shifting operations waiting
            if (counter < 0) {
    
              flag = false
    
            } 
    
          // 
          } else  {
    

            loadVector.add(0)
            dailyOperation.add(false)
            i++
    
            // case the appliance should function but the household is empty
            if (i < Constants.QUARTERS_OF_DAY && operation.get(i) == true) {
    
              // This is a task.
              counter++
    
            } 
      
          }
    
        }
      
      // the appliance isn't supposed to operate
      } else  {
    
      loadVector.add(0)
      dailyOperation.add(false)
    
      }
    
    }
    

    weeklyLoadVector.add(loadVector)
    weeklyOperation.add(dailyOperation)
    
  }
  
  @ Override
  def refresh() {
    
    createWeeklyOperationVector(times + applianceOf.members.size())
    fillWeeklyFunction()
    System.out.println("Consumer Electronics refreshed")
    
  }
  
  
  static constraints = {
  }

}