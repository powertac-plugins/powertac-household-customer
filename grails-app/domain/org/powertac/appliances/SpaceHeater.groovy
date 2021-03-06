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
import java.util.Random

import org.joda.time.Instant
import org.powertac.common.Tariff
import org.powertac.common.configurations.HouseholdConstants


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
  def initialize(String household,ConfigObject conf, Random gen) {

    // Filling the base variables
    name = household + " SpaceHeater"
    saturation = conf.household.appliances.spaceHeater.SpaceHeaterSaturation
    percentage = conf.household.appliances.spaceHeater.SpaceHeaterPercentage
    consumptionShare = (float) (HouseholdConstants.PERCENTAGE * (HouseholdConstants.SPACE_HEATER_CONSUMPTION_SHARE_VARIANCE * gen.nextGaussian() + HouseholdConstants.SPACE_HEATER_CONSUMPTION_SHARE_MEAN))
    baseLoadShare = HouseholdConstants.PERCENTAGE * HouseholdConstants.SPACE_HEATER_BASE_LOAD_SHARE
    power = (int) (HouseholdConstants.SPACE_HEATER_POWER_VARIANCE * gen.nextGaussian() + HouseholdConstants.SPACE_HEATER_POWER_MEAN)
    cycleDuration = HouseholdConstants.SPACE_HEATER_DURATION_CYCLE
    od = false
    inUse = false
    probabilitySeason = fillSeason(HouseholdConstants.SPACE_HEATER_POSSIBILITY_SEASON_1,HouseholdConstants.SPACE_HEATER_POSSIBILITY_SEASON_2,HouseholdConstants.SPACE_HEATER_POSSIBILITY_SEASON_3)
    probabilityWeekday = fillDay(HouseholdConstants.SPACE_HEATER_POSSIBILITY_DAY_1,HouseholdConstants.SPACE_HEATER_POSSIBILITY_DAY_2,HouseholdConstants.SPACE_HEATER_POSSIBILITY_DAY_3)
  }

  @ Override
  def fillDailyFunction(int weekday,Random gen) {

    // Initializing Variables
    loadVector = new Vector()
    dailyOperation = new Vector()
    if (applianceOf.isOnVacation(weekday,0) || gen.nextFloat() > percentage) {
      for (int i = 0;i < HouseholdConstants.QUARTERS_OF_DAY;i++) {
        loadVector.add(0)
        dailyOperation.add(false)
      }
      weeklyLoadVector.add(loadVector)
      weeklyOperation.add(dailyOperation)
      operationVector.add(dailyOperation)
    } else  {
      for (int i = 0;i < HouseholdConstants.QUARTERS_OF_DAY;i++) {
        loadVector.add(0)
        dailyOperation.add(true)
      }
      for (int i = 0;i < HouseholdConstants.SPACE_HEATER_PHASE_1;i++) loadVector.set(i,power)
      for (int i = HouseholdConstants.SPACE_HEATER_PHASE_1;i < HouseholdConstants.SPACE_HEATER_PHASE_2;i++) loadVector.set(i,loadVector.get(i-1) - HouseholdConstants.SPACE_HEATER_PHASE_LOAD)
      for (int i = HouseholdConstants.SPACE_HEATER_PHASE_2;i < HouseholdConstants.SPACE_HEATER_PHASE_3; i++) loadVector.set(i,loadVector.get(i-1))
      for (int i=HouseholdConstants.SPACE_HEATER_PHASE_3;i < HouseholdConstants.SPACE_HEATER_PHASE_4;i++) loadVector.set(i, loadVector.get(i-1) + 2*HouseholdConstants.SPACE_HEATER_PHASE_LOAD)
      for (int i= HouseholdConstants.SPACE_HEATER_PHASE_4;i < HouseholdConstants.QUARTERS_OF_DAY;i++) loadVector.set(i,power)
      weeklyLoadVector.add(loadVector)
      weeklyOperation.add(dailyOperation)
      operationVector.add(dailyOperation)
    }
  }

  @Override
  def createDailyPossibilityOperationVector(int day) {

    def possibilityDailyOperation = new Vector()

    // In case the attenants are not in vacation, the spaceheater works all day
    if (applianceOf.isOnVacation(day,0)) {
      for (int j = 0;j < HouseholdConstants.QUARTERS_OF_DAY;j++) {
        possibilityDailyOperation.add(false)
      }
    }
    else {
      for (int j = 0;j < HouseholdConstants.QUARTERS_OF_DAY;j++) {
        possibilityDailyOperation.add(true)
      }
    }
    return possibilityDailyOperation
  }

  @ Override
  def dailyShifting(Random gen,Tariff tariff,Instant now, int day){

    long[] newControllableLoad = new long[HouseholdConstants.HOURS_OF_DAY]

    // In this case the daily shifting is useless because it works all day
    for (int i=0;i < HouseholdConstants.HOURS_OF_DAY;i++){
      for (int j=0; j < HouseholdConstants.QUARTERS_OF_HOUR;j++) newControllableLoad[i] += householdConsumersService.getApplianceLoads(this,day,(i*HouseholdConstants.QUARTERS_OF_HOUR)+j)
    }
    return newControllableLoad
  }

  @ Override
  def refresh(Random gen) {
    fillWeeklyFunction(gen)
    createWeeklyPossibilityOperationVector()
  }

  static constraints = {
  }
}
