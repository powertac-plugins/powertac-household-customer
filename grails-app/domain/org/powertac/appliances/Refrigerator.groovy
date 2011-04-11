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
 * Refrigerator is the fridge we all use in our households. This appliance
 * can automatically change the freezing cyles in order to save energy,
 * without problem without tenants manipulation. So this is a fully shifting
 * appliance.
 *
 * @author Antonios Chrysopoulos
 * @version 1, 13/02/2011
 */

class Refrigerator extends FullyShiftingAppliance {

  @ Override
  def initialize(HashMap hm) 
  {
    // Creating Auxiliary Variables
    Random gen = ensureRandomSeed()

    // Filling the base variables
    name = "Refrigerator"
      saturation =  (float)hm.get("RefrigeratorSaturation")
      consumptionShare = (float) (Constants.PERCENTAGE * (Constants.REFRIDGERATOR_CONSUMPTION_SHARE_VARIANCE * gen.nextGaussian() + Constants.REFRIDGERATOR_CONSUMPTION_SHARE_MEAN))
      baseLoadShare = Constants.PERCENTAGE * Constants.REFRIDGERATOR_BASE_LOAD_SHARE
      power = (int) (Constants.REFRIDGERATOR_POWER_VARIANCE * gen.nextGaussian() + Constants.REFRIDGERATOR_POWER_MEAN)
      cycleDuration = Constants.REFRIDGERATOR_DURATION_CYCLE
      od = false
      inUse = false
      probabilitySeason = fillSeason(Constants.REFRIDGERATOR_POSSIBILITY_SEASON_1,Constants.REFRIDGERATOR_POSSIBILITY_SEASON_2,Constants.REFRIDGERATOR_POSSIBILITY_SEASON_3)
      probabilityWeekday = fillDay(Constants.REFRIDGERATOR_POSSIBILITY_DAY_1,Constants.REFRIDGERATOR_POSSIBILITY_DAY_2,Constants.REFRIDGERATOR_POSSIBILITY_DAY_3)
  }

  @ Override
  def fillDailyFunction(int weekday) 
  {
    // Initializing Variables
    loadVector = new Vector()
    dailyOperation = new Vector()
    Random gen = ensureRandomSeed()
    int k = 0;
    if (gen.nextFloat() > 0.5) k = 1
    for (int i = 0;i < Constants.QUARTERS_OF_DAY;i++) {
      if (i+k % 2 == 0) {
        loadVector.add(power)
        dailyOperation.add(true)
      } else  {
        loadVector.add(0)
        dailyOperation.add(false)
      }
    }
    weeklyLoadVector.add(loadVector)
    weeklyOperation.add(dailyOperation)
    operationVector.add(dailyOperation)
  }

  @ Override
  def refresh() 
  {
    fillWeeklyFunction()
    System.out.println("Refridgerator refreshed")
  }

  static constraints = {
  }

}
