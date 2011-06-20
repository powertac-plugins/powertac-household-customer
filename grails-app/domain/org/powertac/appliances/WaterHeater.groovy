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
import org.powertac.common.configurations.Constants
import org.powertac.common.enumerations.HeaterType

/**
 * Circulation pump is the appliance that brings water to the household. It works most
 * of the hours of the day, but always when someone is at home in need of water. So it's
 * a not shifting appliance.
 *
 * @author Antonios Chrysopoulos
 * @version 1, 13/02/2011
 */

class WaterHeater extends FullyShiftingAppliance{

  /** The type of the water heater. For more info, read the details in the enumerations.HeaterType java file **/
  HeaterType type

  @ Override
  def fillDailyFunction(int weekday,Random gen) {
    // Initializing And Creating Auxiliary Variables
    loadVector = new Vector()
    dailyOperation = new Vector()
    Vector operation = new Vector()

    if (type == HeaterType.InstantHeater) {
      operation = operationVector.get(weekday)
      for (int i = 0;i < Constants.QUARTERS_OF_DAY;i++) {
        if (operation.get(i) == true) {
          boolean flag = true
          int counter = 0
          while ((flag) && (i < Constants.QUARTERS_OF_DAY) && (counter >= 0)) {
            if (applianceOf.isEmpty(weekday,i) == false) {
              loadVector.add(power)
              dailyOperation.add(true)
              counter--
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

    } else  {

      int start = 0
      int temp = 0

      for (int i = 0;i < Constants.QUARTERS_OF_DAY;i++) {
        operation.add(false)
        dailyOperation.add(false)
        loadVector.add(0)
      }

      if (gen.nextFloat() > Constants.STORAGE_HEATER_POSSIBILITY) start = (Constants.STORAGE_HEATER_START + 1) + gen.nextInt(Constants.STORAGE_HEATER_START - 1)
      else start = 1 + gen.nextInt(Constants.STORAGE_HEATER_START)

      for (int i = start;i < start + Constants.STORAGE_HEATER_PHASE_LOAD;i++) {
        operation.set(i,true)
        dailyOperation.set(i,true)
        loadVector.set(i, power)
      }

      temp = start + Constants.STORAGE_HEATER_PHASE_LOAD

      for (int j = 0;j < Constants.STORAGE_HEATER_PHASES-1; j++) {
        operation.set((temp + Constants.STORAGE_HEATER_PHASES*j),true)
        dailyOperation.set((temp + Constants.STORAGE_HEATER_PHASES*j),true)
        loadVector.set((temp + Constants.STORAGE_HEATER_PHASES*j), power)
      }
      weeklyLoadVector.add(loadVector)
      weeklyOperation.add(dailyOperation)
      operationVector.add(operation)
    }
  }

  @Override
  def createDailyPossibilityOperationVector(int day) {

    def possibilityDailyOperation = new Vector()

    if (type == HeaterType.InstantHeater) {
      for (int j = 0;j < Constants.QUARTERS_OF_DAY;j++) {
        if (applianceOf.isEmpty(day,j) == false) possibilityDailyOperation.add(true)
        else possibilityDailyOperation.add(false)
      }
    }
    else {
      for (int j = 0;j < Constants.QUARTERS_OF_DAY;j++) {
        possibilityDailyOperation.add(true)
      }
    }
    return possibilityDailyOperation
  }

  @ Override
  def showStatus() {
    // Printing basic variables
    log.info("Name = " + name)
    log.info("Saturation = " + saturation)
    log.info("Consumption Share = " + consumptionShare)
    log.info("Base Load Share = " + baseLoadShare)
    log.info("Power = " + power)
    log.info("Heater Type = " + type)
    log.info("Cycle Duration = "+ cycleDuration)
    log.info("Occupancy Dependence = "+ od)
    log.info("In Use = " + inUse)

    // Printing Season Possibility
    Set set = probabilitySeason.entrySet();
    Iterator it = set.iterator();
    log.info("Probability Season = ")
    while (it.hasNext()) {
      Map.Entry me = (Map.Entry)it.next();
      log.info(me.getKey() + " : " + me.getValue() );
    }

    // Printing Weekday Possibility
    set = probabilityWeekday.entrySet();
    it = set.iterator();
    log.info("Probability Weekday = ")
    while (it.hasNext()) {
      Map.Entry me = (Map.Entry)it.next();
      log.info(me.getKey() + " : " + me.getValue() );
    }

    // Printing Operation Vector
    def iter = operationVector.listIterator();
    log.info("Operation Vector = ")
    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {
      log.info("Day " + (i+1))
      iter = operationVector.get(i).listIterator();
      for (int j = 0;j < Constants.QUARTERS_OF_DAY; j++) log.info("Quarter : " + (j+1) + "  " + iter.next())
    }

    // Printing Weekly Operation Vector and Load Vector
    log.info("Weekly Operation Vector and Load = ")

    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {
      log.info("Day " + (i+1))
      iter = weeklyOperation.get(i).listIterator();
      ListIterator iter2 = weeklyLoadVector.get(i).listIterator();
      for (int j = 0;j < Constants.QUARTERS_OF_DAY; j++) log.info("Quarter " + (j+1) + " = " + iter.next() + "   Load = " + iter2.next())
    }
  }

  @ Override
  def initialize(String household, ConfigObject conf, Random gen) {
    // Creating Auxiliary Variables
    int x = 1 + gen.nextInt(Constants.PERCENTAGE)
    int limit = conf.household.appliances.waterHeater.InstantHeater
    // Filling the base variables
    name = household + " WaterHeater"
    saturation = conf.household.appliances.waterHeater.WaterHeaterSaturation
    if ( x < limit) {
      consumptionShare = (float) (Constants.PERCENTAGE * (Constants.INSTANT_HEATER_CONSUMPTION_SHARE_VARIANCE * gen.nextGaussian() + Constants.INSTANT_HEATER_CONSUMPTION_SHARE_MEAN))
      baseLoadShare = Constants.PERCENTAGE * Constants.INSTANT_HEATER_BASE_LOAD_SHARE
      power = (int) (Constants.INSTANT_HEATER_POWER_VARIANCE * gen.nextGaussian() + Constants.INSTANT_HEATER_POWER_MEAN)
      cycleDuration = Constants.INSTANT_HEATER_DURATION_CYCLE
      od = false
      inUse = false
      probabilitySeason = fillSeason(Constants.INSTANT_HEATER_POSSIBILITY_SEASON_1,Constants.INSTANT_HEATER_POSSIBILITY_SEASON_2,Constants.INSTANT_HEATER_POSSIBILITY_SEASON_3)
      probabilityWeekday = fillDay(Constants.INSTANT_HEATER_POSSIBILITY_DAY_1,Constants.INSTANT_HEATER_POSSIBILITY_DAY_2,Constants.INSTANT_HEATER_POSSIBILITY_DAY_3)
      setType(HeaterType.InstantHeater)
      times = conf.household.appliances.waterHeater.InstantHeaterDailyTimes + (int)(applianceOf.members.size()/2)
      createWeeklyOperationVector(times, gen)
    } else  {
      consumptionShare = (float) (Constants.PERCENTAGE * (Constants.STORAGE_HEATER_CONSUMPTION_SHARE_VARIANCE * gen.nextGaussian() + Constants.STORAGE_HEATER_CONSUMPTION_SHARE_MEAN))
      baseLoadShare = Constants.PERCENTAGE * Constants.STORAGE_HEATER_BASE_LOAD_SHARE
      power = (int) (Constants.STORAGE_HEATER_POWER_VARIANCE * gen.nextGaussian() + Constants.STORAGE_HEATER_POWER_MEAN)
      cycleDuration = Constants.STORAGE_HEATER_DURATION_CYCLE
      od = false
      inUse = false
      probabilitySeason = fillSeason(Constants.STORAGE_HEATER_POSSIBILITY_SEASON_1,Constants.STORAGE_HEATER_POSSIBILITY_SEASON_2,Constants.STORAGE_HEATER_POSSIBILITY_SEASON_3)
      probabilityWeekday = fillDay(Constants.STORAGE_HEATER_POSSIBILITY_DAY_1,Constants.STORAGE_HEATER_POSSIBILITY_DAY_2,Constants.STORAGE_HEATER_POSSIBILITY_DAY_3)
      setType(HeaterType.StorageHeater)
    }
  }

  @ Override
  def dailyShifting(Random gen,Tariff tariff,Instant now, int day){

    BigInteger[] newControllableLoad = new BigInteger[Constants.HOURS_OF_DAY]
    for (int j=0;j < Constants.HOURS_OF_DAY;j++) newControllableLoad[j] = 0

    if (type == HeaterType.InstantHeater) {
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
        newControllableLoad[minindex] = times*power
      }
    }
    else {

      if (householdConsumersService.getApplianceOperationDays(this,day)) {
        def minindex = 0
        def minvalue = Double.POSITIVE_INFINITY
        def functionMatrix = createShiftingOperationMatrix(day)
        Instant hour1 = now

        for (int i=0;i < Constants.STORAGE_HEATER_SHIFTING_END;i++){
          if (functionMatrix[i]){
            if (minvalue >= tariff.getUsageCharge(hour1)+tariff.getUsageCharge(hour1 + TimeService.HOUR)+tariff.getUsageCharge(hour1+ 2*TimeService.HOUR)+tariff.getUsageCharge(hour1 + 3*TimeService.HOUR)+tariff.getUsageCharge(hour1 + 4*TimeService.HOUR)){
              minvalue = tariff.getUsageCharge(hour1)+tariff.getUsageCharge(hour1 + TimeService.HOUR)+tariff.getUsageCharge(hour1+ 2*TimeService.HOUR)+tariff.getUsageCharge(hour1 + 3*TimeService.HOUR)+tariff.getUsageCharge(hour1 + 4*TimeService.HOUR)
              minindex = i
            }
          }
          hour1 = hour1 + TimeService.HOUR
        }

        for (int i=0; i <= Constants.STORAGE_HEATER_PHASES ;i++){
          newControllableLoad[minindex+i] = Constants.QUARTERS_OF_HOUR*power
        }

        for (int i=1; i < Constants.STORAGE_HEATER_PHASES;i++){
          newControllableLoad[Constants.STORAGE_HEATER_PHASES+minindex+i] = power
        }
      }
    }
    return newControllableLoad
  }


  @ Override
  def refresh(Random gen) {
    // case the Water Heater is Instant
    if (type == HeaterType.InstantHeater) createWeeklyOperationVector(times,gen)
    fillWeeklyFunction(gen)
    createWeeklyPossibilityOperationVector()
  }


  static constraints = {
  }
}
