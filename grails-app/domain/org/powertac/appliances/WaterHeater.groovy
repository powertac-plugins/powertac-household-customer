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
  def fillDailyFunction(int weekday) {
    // Initializing And Creating Auxiliary Variables
    int start = 0
    int temp = 0
    Random gen = ensureRandomSeed()
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
            if (applianceOf.isEmpty(i+1) == false) {
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
      for (int i = 0;i < Constants.QUARTERS_OF_DAY;i++) {
        operation.add(false)
        dailyOperation.add(false)
        loadVector.add(0)
      }
      if (gen.nextFloat() > Constants.STORAGE_HEATER_POSSIBILITY) start = (Constants.STORAGE_HEATER_START + 1) + gen.nextInt(Constants.STORAGE_HEATER_START - 1)
      else start = 1 + gen.nextInt(Constants.STORAGE_HEATER_START)

      for (int i = start;i < start + 2 * Constants.STORAGE_HEATER_PHASES;i++) {
        operation.set(i,true)
        dailyOperation.set(i,true)
        loadVector.set(i, power)
        temp = i
      }
      for (int j = 1;j < Constants.STORAGE_HEATER_PHASES; j++) {
        operation.set((temp + Constants.STORAGE_HEATER_PHASE_LOAD*j),true)
        dailyOperation.set((temp + Constants.STORAGE_HEATER_PHASE_LOAD*j),true)
        loadVector.set((temp + Constants.STORAGE_HEATER_PHASE_LOAD*j), power)
      }
      weeklyLoadVector.add(loadVector)
      weeklyOperation.add(dailyOperation)
      operationVector.add(operation)
    }
  }

  @ Override
  def showStatus() {
    // Printing basic variables
    System.out.println("Name = " + name)
    System.out.println("Saturation = " + saturation)
    System.out.println("Consumption Share = " + consumptionShare)
    System.out.println("Base Load Share = " + baseLoadShare)
    System.out.println("Power = " + power)
    System.out.println("Heater Type = " + type)
    System.out.println("Cycle Duration = "+ cycleDuration)
    System.out.println("Occupancy Dependence = "+ od)
    System.out.println("In Use = " + inUse)

    // Printing Season Possibility
    Set set = probabilitySeason.entrySet();
    Iterator it = set.iterator();
    System.out.println("Probability Season = ")
    while (it.hasNext()) {
      Map.Entry me = (Map.Entry)it.next();
      System.out.println(me.getKey() + " : " + me.getValue() );
    }

    // Printing Weekday Possibility
    set = probabilityWeekday.entrySet();
    it = set.iterator();
    System.out.println("Probability Weekday = ")
    while (it.hasNext()) {
      Map.Entry me = (Map.Entry)it.next();
      System.out.println(me.getKey() + " : " + me.getValue() );
    }

    // Printing Operation Vector
    def iter = operationVector.listIterator();
    System.out.println("Operation Vector = ")
    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {
      System.out.println("Day " + (i+1))
      iter = operationVector.get(i).listIterator();
      for (int j = 0;j < Constants.QUARTERS_OF_DAY; j++) System.out.println("Quarter : " + (j+1) + "  " + iter.next())
    }

    // Printing Weekly Operation Vector and Load Vector
    System.out.println("Weekly Operation Vector and Load = ")

    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {
      System.out.println("Day " + (i+1))
      iter = weeklyOperation.get(i).listIterator();
      ListIterator iter2 = weeklyLoadVector.get(i).listIterator();
      for (int j = 0;j < Constants.QUARTERS_OF_DAY; j++) System.out.println("Quarter " + (j+1) + " = " + iter.next() + "   Load = " + iter2.next())
    }
  }

  @ Override
  def initialize(HashMap hm) {
    // Creating Auxiliary Variables
    Random gen = ensureRandomSeed()
    int x = 1 + gen.nextInt(Constants.PERCENTAGE)
    int limit = (int)hm.get("InstantHeater")

    // Filling the base variables
    name = "WaterHeater"
    saturation = (float)hm.get("WaterHeaterSaturation")
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
      times = (float)hm.get("InstantHeaterDailyTimes")
      createWeeklyOperationVector( (int)(times + applianceOf.members.size()/2))
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
  def refresh() {
    // case the Water Heater is Instant
    if (type == HeaterType.InstantHeater) {
      createWeeklyOperationVector( (int)(times + applianceOf.members.size()/2))
      fillWeeklyFunction()
      log.info "Instant Water Heater refreshed"
    } else  {
      fillWeeklyFunction()
      log.info "Storage Water Heater refreshed"
    }
  }

  static constraints = {
  }
}
