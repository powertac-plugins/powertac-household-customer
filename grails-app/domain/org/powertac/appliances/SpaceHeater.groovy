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

import java.util.HashMap

import org.powertac.common.configurations.Constants


/**
 * Spaceheater is a electric appliance utilized to keep the rooms of a household
 * warm when needed. These devices can work automatically in order to save as
 * much energy as possible, knowing when the room must be warm.
 * So this is a fully shifting appliance.
 *
 * @author Antonios Chrysopoulos
 * @version 1, 13/02/2011
 */

class SpaceHeater extends FullyShiftingAppliance{

  /** Variable that presents the mean possibility to utilize the appliance each hour of the day that someone is present in the housesold.*/
  float percentage

  @ Override
  def initialize(HashMap hm, Random gen) {

    // Filling the base variables
    name = "SpaceHeater"
    saturation = (float)hm.get("SpaceHeaterSaturation")
    float percentage = (float)hm.get("SpaceHeaterPercentage")
    consumptionShare = (float) (Constants.PERCENTAGE * (Constants.SPACE_HEATER_CONSUMPTION_SHARE_VARIANCE * gen.nextGaussian() + Constants.SPACE_HEATER_CONSUMPTION_SHARE_MEAN))
    baseLoadShare = Constants.PERCENTAGE * Constants.SPACE_HEATER_BASE_LOAD_SHARE
    power = (int) (Constants.SPACE_HEATER_POWER_VARIANCE * gen.nextGaussian() + Constants.SPACE_HEATER_POWER_MEAN)
    cycleDuration = Constants.SPACE_HEATER_DURATION_CYCLE
    od = false
    inUse = false
    probabilitySeason = fillSeason(Constants.SPACE_HEATER_POSSIBILITY_SEASON_1,Constants.SPACE_HEATER_POSSIBILITY_SEASON_2,Constants.SPACE_HEATER_POSSIBILITY_SEASON_3)
    probabilityWeekday = fillDay(Constants.SPACE_HEATER_POSSIBILITY_DAY_1,Constants.SPACE_HEATER_POSSIBILITY_DAY_2,Constants.SPACE_HEATER_POSSIBILITY_DAY_3)
  }

  @ Override
  def fillDailyFunction(int weekday,Random gen) {

    // Initializing Variables
    loadVector = new Vector()
    dailyOperation = new Vector()
    if (applianceOf.isOnVacation(1) || gen.nextFloat() > percentage) {
      for (int i = 0;i < Constants.QUARTERS_OF_DAY;i++) {
        loadVector.add(0)
        dailyOperation.add(false)
      }
      weeklyLoadVector.add(loadVector)
      weeklyOperation.add(dailyOperation)
      operationVector.add(dailyOperation)
    } else  {
      for (int i = 0;i < Constants.QUARTERS_OF_DAY;i++) {
        loadVector.add(0)
        dailyOperation.add(true)
      }
      for (int i = 0;i < Constants.SPACE_HEATER_PHASE_1;i++) loadVector.set(i,power)
      for (int i = Constants.SPACE_HEATER_PHASE_1;i < Constants.SPACE_HEATER_PHASE_2;i++) loadVector.set(i,power - Constants.SPACE_HEATER_PHASE_LOAD * (i - Constants.SPACE_HEATER_PHASE_4 - 1))
      for (int i = Constants.SPACE_HEATER_PHASE_2;i < Constants.SPACE_HEATER_PHASE_3; i++) loadVector.set(i,power - Constants.SPACE_HEATER_PHASE_LOAD * (Constants.SPACE_HEATER_PHASE_4 - 1))
      for (int i=Constants.SPACE_HEATER_PHASE_3;i < Constants.SPACE_HEATER_PHASE_4;i++) loadVector.set(i, power - 2 * Constants.SPACE_HEATER_PHASE_LOAD*(Constants.SPACE_HEATER_PHASE_4 - 1 - i))
      for (int i= Constants.SPACE_HEATER_PHASE_4;i < Constants.QUARTERS_OF_DAY;i++) loadVector.set(i,power)
      weeklyLoadVector.add(loadVector)
      weeklyOperation.add(dailyOperation)
      operationVector.add(dailyOperation)
    }
  }

  @ Override
  def refresh(Random gen) {
    fillWeeklyFunction(gen)
  }

  static constraints = {
  }
}
