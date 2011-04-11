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
 * Circulation pump is the appliance that brings water to the household. It works most
 * of the hours of the day, but always when someone is at home in need of water. So it's
 * a not shifting appliance.
 *
 * @author Antonios Chrysopoulos
 * @version 1, 13/02/2011
 */

class CirculationPump extends NotShiftingAppliance 
{

  /** Variable that presents the mean possibility to utilize the appliance each hour of the day 
   * that someone is present in the housesold.
   */
  BigDecimal percentage


  @ Override
  def initialize(HashMap hm) 
  {

    // Creating Auxiliary Variables
    Random gen = ensureRandomSeed()

    // Filling the base variables
    name = "CirculationPump"
      saturation = (float)hm.get("CirculationPumpSaturation")
      percentage = ((float)hm.get("CirculationPumpPercentage"))
      consumptionShare = (float) (Constants.PERCENTAGE * (Constants.CIRCULATION_PUMP_CONSUMPTION_SHARE_VARIANCE * gen.nextGaussian() + Constants.CIRCULATION_PUMP_CONSUMPTION_SHARE_MEAN))
      baseLoadShare = Constants.PERCENTAGE * Constants.CIRCULATION_PUMP_BASE_LOAD_SHARE
      power = (int) (Constants.CIRCULATION_PUMP_POWER_VARIANCE * gen.nextGaussian() + Constants.CIRCULATION_PUMP_POWER_MEAN)
      cycleDuration = Constants.CIRCULATION_PUMP_DURATION_CYCLE
      od = false
      inUse = false
      probabilitySeason = fillSeason(Constants.CIRCULATION_PUMP_POSSIBILITY_SEASON_1,Constants.CIRCULATION_PUMP_POSSIBILITY_SEASON_2,Constants.CIRCULATION_PUMP_POSSIBILITY_SEASON_3)
      probabilityWeekday = fillDay(Constants.CIRCULATION_PUMP_POSSIBILITY_DAY_1,Constants.CIRCULATION_PUMP_POSSIBILITY_DAY_2,Constants.CIRCULATION_PUMP_POSSIBILITY_DAY_3)

  }

  @ Override
  def fillDailyFunction(int weekday) 
  {

    // Initializing and Creating auxiliary variables
    loadVector = new Vector()
    dailyOperation = new Vector()
    Vector v = new Vector()
    Random gen = ensureRandomSeed()

    // For each quarter of a day
    for (int i = 0;i < Constants.QUARTERS_OF_DAY;i++) {
      if (applianceOf.isEmpty(i+1) == false && (gen.nextFloat() > percentage)) {
        loadVector.add(power)
        dailyOperation.add(true)
        v.add(true)
      } else  {
        loadVector.add(0)
        dailyOperation.add(false)
        v.add(false)
      }
    }
    weeklyLoadVector.add(loadVector)
    weeklyOperation.add(dailyOperation)
    operationVector.add(v)

  }

  @ Override
  def refresh() 
  {
    fillWeeklyFunction()
    System.out.println("Circulation Pump refreshed")
  }


  static constraints = {

    name()
    saturation()
    cycleDuration()
    od()
    inUse()

  }

}
