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

import groovy.util.ConfigObject

import java.util.HashMap

import org.powertac.common.configurations.HouseholdConstants

/**
 * Lights are utilized when the persons inhabiting the house have need 
 * for them.So it's a not shifting appliance.
 *
 * @author Antonios Chrysopoulos
 * @version 1, 13/02/2011
 */
class Lights extends NotShiftingAppliance{

  @ Override
  def initialize(String household,ConfigObject conf, Random gen) {

    // Filling the base variables
    name = household + " Lights"
    saturation = 1
    consumptionShare = (float) (HouseholdConstants.PERCENTAGE * (HouseholdConstants.LIGHTS_CONSUMPTION_SHARE_VARIANCE * gen.nextGaussian() + HouseholdConstants.LIGHTS_CONSUMPTION_SHARE_MEAN))
    baseLoadShare = HouseholdConstants.PERCENTAGE * HouseholdConstants.LIGHTS_BASE_LOAD_SHARE
    power = (int) (HouseholdConstants.LIGHTS_POWER_VARIANCE * gen.nextGaussian() + HouseholdConstants.LIGHTS_POWER_MEAN)
    cycleDuration = HouseholdConstants.LIGHTS_DURATION_CYCLE
    times = conf.household.appliances.lights.LightsDailyTimes + applianceOf.members.size()
    od = false
    inUse = false
    probabilitySeason = fillSeason(HouseholdConstants.LIGHTS_POSSIBILITY_SEASON_1,HouseholdConstants.LIGHTS_POSSIBILITY_SEASON_2,HouseholdConstants.LIGHTS_POSSIBILITY_SEASON_3)
    probabilityWeekday = fillDay(HouseholdConstants.LIGHTS_POSSIBILITY_DAY_1,HouseholdConstants.LIGHTS_POSSIBILITY_DAY_2,HouseholdConstants.LIGHTS_POSSIBILITY_DAY_3)
    createWeeklyOperationVector(times, gen)
  }

  @Override
  def createDailyPossibilityOperationVector(int day) {

    def possibilityDailyOperation = new Vector()

    // Lights need to operate only when someone is in the house
    for (int j = 0;j < HouseholdConstants.QUARTERS_OF_DAY;j++) {
      if (applianceOf.isEmpty(day,j) == false) possibilityDailyOperation.add(true)
      else possibilityDailyOperation.add(false)
    }

    return possibilityDailyOperation
  }

  @ Override
  def fillDailyFunction(int weekday,Random gen) {

    // Initializing and Creating auxiliary variables
    loadVector = new Vector()
    dailyOperation = new Vector()
    Vector operation = operationVector.get(weekday)

    // For each quarter of a day
    for (int i = 0;i < HouseholdConstants.QUARTERS_OF_DAY;i++) {
      if (operation.get(i) == true) {
        boolean flag = true
        int counter = 0
        while ((flag) && (i < HouseholdConstants.QUARTERS_OF_DAY) && (counter >= 0)) {
          if (applianceOf.isEmpty(weekday,i) == false) {
            loadVector.add(power)
            dailyOperation.add(true)
            counter--
            if (counter < 0) flag = false
          } else  {
            loadVector.add(0)
            dailyOperation.add(false)
            i++
            if (i < HouseholdConstants.QUARTERS_OF_DAY && operation.get(i) == true) counter++
          }
        }
      } else  {
        loadVector.add(0)
        dailyOperation.add(false)
      }
    }
    weeklyLoadVector.add(loadVector)
    weeklyOperation.add(dailyOperation)
  }

  @ Override
  def refresh(Random gen) {
    createWeeklyOperationVector(times,gen)
    fillWeeklyFunction(gen)
    createWeeklyPossibilityOperationVector()
  }

  static constraints = {
  }
}
