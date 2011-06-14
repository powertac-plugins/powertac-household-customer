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

import org.joda.time.Instant
import org.powertac.common.Tariff
import org.powertac.common.TimeService
import org.powertac.common.configurations.Constants


/**
 * Stove is the kitchen utility we use for cooking. It is use at least twice a day
 * depending on the number of tenants. The tenants should be present when functioning
 * so this is a not shifting appliance.
 *
 * @author Antonios Chrysopoulos
 * @version 1, 13/02/2011
 */
class Stove extends SemiShiftingAppliance{

  @ Override
  def initialize(String household,ConfigObject conf, Random gen) {

    // Filling the base variables
    name = household + " Stove"
    saturation = conf.household.appliances.stove.StoveSaturation
    consumptionShare = (float) (Constants.PERCENTAGE * (Constants.STOVE_CONSUMPTION_SHARE_VARIANCE * gen.nextGaussian() + Constants.STOVE_CONSUMPTION_SHARE_MEAN))
    baseLoadShare = Constants.PERCENTAGE * Constants.STOVE_BASE_LOAD_SHARE
    power = (int) (Constants.STOVE_POWER_VARIANCE * gen.nextGaussian() + Constants.STOVE_POWER_MEAN)
    cycleDuration = Constants.STOVE_DURATION_CYCLE
    od = false
    inUse = false
    probabilitySeason = fillSeason(Constants.STOVE_POSSIBILITY_SEASON_1,Constants.STOVE_POSSIBILITY_SEASON_2,Constants.STOVE_POSSIBILITY_SEASON_3)
    probabilityWeekday = fillDay(Constants.STOVE_POSSIBILITY_DAY_1,Constants.STOVE_POSSIBILITY_DAY_2,Constants.STOVE_POSSIBILITY_DAY_3)
    times = conf.household.appliances.stove.StoveDailyTimes
    createWeeklyOperationVector(times,gen)
  }

  @ Override
  def createDailyOperationVector(int times, Random gen) {

    // Creating Auxiliary Variables
    Random rand = new Random()
    Vector v = new Vector(Constants.QUARTERS_OF_DAY)

    // First initialize all to false
    for (int i = 0;i < Constants.QUARTERS_OF_DAY;i++) v.add(false)
    for (int i = 0;i < times;i++) {
      int quarter = gen.nextInt(Constants.QUARTERS_OF_DAY - cycleDuration)
      v.set(quarter,true)
    }
    return v
  }

  @ Override
  def fillDailyFunction(int weekday, Random gen) {

    // Initializing Variables
    loadVector = new Vector()
    dailyOperation = new Vector()
    Vector operation = operationVector.get(weekday)

    // Check all quarters of the day
    for (int i = 0;i < Constants.QUARTERS_OF_DAY;i++) {
      if (operation.get(i) == true) {
        boolean flag = true
        int counter = 0
        while ((flag) && (i < Constants.QUARTERS_OF_DAY) && (counter >= 0)) {
          if (applianceOf.isEmpty(weekday,i) == false && applianceOf.isEmpty(weekday,i+1) == false) {
            loadVector.add(power)
            dailyOperation.add(true)
            loadVector.add(power)
            dailyOperation.add(true)
            counter--
            i = dailyOperation.size() - 1
            if (counter < 0) flag = false
          } else  {
            loadVector.add(0)
            dailyOperation.add(false)
            i++
            if (i < Constants.QUARTERS_OF_DAY && operation.get(i) == true) counter++
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

  @Override
  def createDailyPossibilityOperationVector(int day) {

    def possibilityDailyOperation = new Vector()

    for (int j = 0;j < Constants.QUARTERS_OF_DAY - 1;j++) {
      if (applianceOf.isEmpty(day,j) == false && applianceOf.isEmpty(day,j+1) == false) possibilityDailyOperation.add(true)
      else possibilityDailyOperation.add(false)
    }
    // For the last time, without check because it is the next day
    possibilityDailyOperation.add(false)
    return possibilityDailyOperation
  }

  @ Override
  def dailyShifting(Tariff tariff,Instant now, int day){

    long[] newControllableLoad = new long[Constants.HOURS_OF_DAY]

    if (householdConsumersService.getApplianceOperationDays(this,day)) {
      def minindex = 0
      def minvalue = Double.POSITIVE_INFINITY
      def functionMatrix = createShiftingOperationMatrix(day)
      Instant hour1 = now

      for (int i=0;i < Constants.HOURS_OF_DAY;i++){
        if (functionMatrix[i]){
          if (minvalue >= tariff.getUsageCharge(hour1)){
            minvalue = tariff.getUsageCharge(hour1)
            minindex = i
          }
        }
        hour1 = hour1 + TimeService.HOUR
      }
      newControllableLoad[minindex] = cycleDuration*power
    }
    return newControllableLoad
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
