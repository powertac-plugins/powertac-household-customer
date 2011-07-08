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
import org.powertac.common.TimeService
import org.powertac.common.configurations.HouseholdConstants


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
    consumptionShare = (float) (HouseholdConstants.PERCENTAGE * (HouseholdConstants.STOVE_CONSUMPTION_SHARE_VARIANCE * gen.nextGaussian() + HouseholdConstants.STOVE_CONSUMPTION_SHARE_MEAN))
    baseLoadShare = HouseholdConstants.PERCENTAGE * HouseholdConstants.STOVE_BASE_LOAD_SHARE
    power = (int) (HouseholdConstants.STOVE_POWER_VARIANCE * gen.nextGaussian() + HouseholdConstants.STOVE_POWER_MEAN)
    cycleDuration = HouseholdConstants.STOVE_DURATION_CYCLE
    od = false
    inUse = false
    probabilitySeason = fillSeason(HouseholdConstants.STOVE_POSSIBILITY_SEASON_1,HouseholdConstants.STOVE_POSSIBILITY_SEASON_2,HouseholdConstants.STOVE_POSSIBILITY_SEASON_3)
    probabilityWeekday = fillDay(HouseholdConstants.STOVE_POSSIBILITY_DAY_1,HouseholdConstants.STOVE_POSSIBILITY_DAY_2,HouseholdConstants.STOVE_POSSIBILITY_DAY_3)
    times = conf.household.appliances.stove.StoveDailyTimes
    createWeeklyOperationVector(times,gen)

  }

  @ Override
  def createDailyOperationVector(int times, Random gen) {

    // Creating Auxiliary Variables
    Random rand = new Random()
    Vector v = new Vector(HouseholdConstants.QUARTERS_OF_DAY)

    // First initialize all to false
    for (int i = 0;i < HouseholdConstants.QUARTERS_OF_DAY;i++) v.add(false)
    for (int i = 0;i < times;i++) {
      int quarter = gen.nextInt(HouseholdConstants.QUARTERS_OF_DAY - cycleDuration)
      if (v.get(quarter)== false) v.set(quarter,true)
      else v.set(quarter+2,true)
    }
    return v
  }

  @ Override
  def createWeeklyOperationVector(int times, Random gen)
  {
    for (int i=0;i < HouseholdConstants.DAYS_OF_WEEK;i++) operationVector.add(createDailyOperationVector(times,gen))
  }

  @ Override
  def fillDailyFunction(int weekday, Random gen) {

    // Initializing Variables
    loadVector = new Vector()
    dailyOperation = new Vector()
    Vector operation = operationVector.get(weekday)

    // Check all quarters of the day
    for (int i = 0;i < HouseholdConstants.QUARTERS_OF_DAY;i++) {
      if (operation.get(i) == true) {
        boolean flag = true
        int counter = 0
        while ((flag) && (i < HouseholdConstants.QUARTERS_OF_DAY) && (counter >= 0)) {
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

  @Override
  def createDailyPossibilityOperationVector(int day) {

    def possibilityDailyOperation = new Vector()

    // In order for stove to work someone must be in the house for half hour
    for (int j = 0;j < HouseholdConstants.QUARTERS_OF_DAY - 1;j++) {
      if (applianceOf.isEmpty(day,j) == false && applianceOf.isEmpty(day,j+1) == false) possibilityDailyOperation.add(true)
      else possibilityDailyOperation.add(false)
    }

    // For the last time, without check because it is the next day
    possibilityDailyOperation.add(false)
    return possibilityDailyOperation
  }

  @ Override
  def dailyShifting(Random gen,Tariff tariff,Instant now, int day){

    long[] newControllableLoad = new long[HouseholdConstants.HOURS_OF_DAY]

    def minindex = 0
    def minvalue = Double.POSITIVE_INFINITY
    def functionMatrix = createShiftingOperationMatrix(day)
    Instant hour1 = now
    long sumPower = 0

    // Gather the Load Summary of the day
    for (int i=0;i< HouseholdConstants.QUARTERS_OF_DAY;i++) sumPower += householdConsumersService.getApplianceLoads(this,day,i)

    // If we have a fixed tariff rate
    if ((tariff.tariffSpec.rates.size() == 1) && (tariff.tariffSpec.rates.getAt(0).isFixed)) {
      def possibleHours = new Vector()

      // find the all the available functioning hours of the appliance
      for (int i=0;i < HouseholdConstants.HOURS_OF_DAY;i++){
        if (functionMatrix[i]) possibleHours.add(i)
      }
      log.debug("Stove Bag Size: ${possibleHours.size()}")
      if (possibleHours.size() > 0) minindex = possibleHours.get(gen.nextInt(possibleHours.size()))
    }
    // case of variable tariff rate
    else {
      // find the all the available functioning hours of the appliance
      for (int i=0;i < HouseholdConstants.HOURS_OF_DAY;i++){
        if (functionMatrix[i]){
          if (minvalue >= tariff.getUsageCharge(hour1)){
            minvalue = tariff.getUsageCharge(hour1)
            minindex = i
          }
        }
        hour1 = hour1 + TimeService.HOUR
      }
    }
    newControllableLoad[minindex] = sumPower
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
