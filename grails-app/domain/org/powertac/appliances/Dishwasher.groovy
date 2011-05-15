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
import java.util.Random

import org.powertac.common.configurations.Constants
import org.powertac.common.enumerations.Mode


/**
 * Dishwasher are used in order to wash easily the dishes after dinner. There are several
 * programs that help you automate the procedure in order to start at a less costly time, 
 * without problem, because it doesn't need emptying after utilization.
 * So this is a semi-shifting appliance.
 *
 * @author Antonios Chrysopoulos
 * @version 1, 13/02/2011
 */

class Dishwasher extends SemiShiftingAppliance {

  /** The function mode of the dishwasher. For more info, read the details in the enumerations.Mode java file **/
  Mode mode = Mode.One

  @ Override
  def initialize(HashMap hm,Random gen) {

    // Filling the base variables
    name = "Dishwasher"
    saturation = (float)hm.get("DishwasherSaturation")
    consumptionShare = (float) (Constants.PERCENTAGE * (Constants.DISHWASHER_CONSUMPTION_SHARE_VARIANCE * gen.nextGaussian() + Constants.DISHWASHER_CONSUMPTION_SHARE_MEAN))
    baseLoadShare = Constants.PERCENTAGE * Constants.DISHWASHER_BASE_LOAD_SHARE
    power = (int) (Constants.DISHWASHER_POWER_VARIANCE * gen.nextGaussian() + Constants.DISHWASHER_POWER_MEAN)
    cycleDuration = Constants.DISHWASHER_DURATION_CYCLE
    od = false
    inUse = false
    probabilitySeason = fillSeason(Constants.DISHWASHER_POSSIBILITY_SEASON_1,Constants.DISHWASHER_POSSIBILITY_SEASON_2,Constants.DISHWASHER_POSSIBILITY_SEASON_3)
    probabilityWeekday = fillDay(Constants.DISHWASHER_POSSIBILITY_DAY_1,Constants.DISHWASHER_POSSIBILITY_DAY_2,Constants.DISHWASHER_POSSIBILITY_DAY_3)
    times = (int)hm.get("DishwasherWeeklyTimes")
    createWeeklyOperationVector((int)(times + applianceOf.members.size()),gen)
  }

  @ Override
  def showStatus() {
    // Printing basic variables
    System.out.println("Name = " + name)
    System.out.println("Saturation = " + saturation)
    System.out.println("Consumption Share = " + consumptionShare)
    System.out.println("Base Load Share = " + baseLoadShare)
    System.out.println("Power = " + power)
    System.out.println("Operation Mode = " + mode)
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

    // Printing Function Day Vector
    ListIterator iter = days.listIterator();
    System.out.println("Days Vector = ")
    while (iter.hasNext()) System.out.println("Day  " + iter.next())

    // Printing Operation Vector
    iter = operationVector.listIterator();
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
  def fillDailyFunction(int weekday,Random gen) {

    // Initializing Variables
    loadVector = new Vector()
    dailyOperation = new Vector()
    Vector operation = operationVector.get(weekday)
    for (int l = 0;l < Constants.QUARTERS_OF_DAY; l++) {
      loadVector.add(0)
      dailyOperation.add(false)
    }

    // Check all quarters of the day
    for (int i = 0;i < Constants.QUARTERS_OF_DAY;i++) {
      if (operation.get(i) == true) {
        boolean flag = true
        while (flag && i < (Constants.QUARTERS_OF_DAY - Constants.DISHWASHER_DURATION_CYCLE + 1)) {
          boolean empty = checkHouse(i)
          if (empty == false) {
            for (int k = i;k < i + Constants.DISHWASHER_DURATION_CYCLE;k++) {
              loadVector.set(k,power)
              dailyOperation.set(k,true)
              if (k == Constants.QUARTERS_OF_DAY - 1) break
            }
            i = Constants.QUARTERS_OF_DAY
            flag = false
          } else  {
            i++
          }
        }
      }
    }
    weeklyLoadVector.add(loadVector)
    weeklyOperation.add(dailyOperation)
  }


  /** This function checks for the household to see when it is empty or not empty
   * for the duration of the operation	
   * @param hour
   * @return
   */
  def checkHouse(int hour) {
    // Creating auxiliary variable
    boolean empty = true
    int j = hour
    while ((j < hour + Constants.DISHWASHER_DURATION_CYCLE + 1) && (empty == true)) {
      empty = empty & applianceOf.isEmpty(j+1)
      j++
      if (j == Constants.QUARTERS_OF_DAY - 1) break
    }
    return empty
  }

  @ Override
  def refresh(Random gen) {
    createWeeklyOperationVector((int)(times + applianceOf.members.size()), gen)
    fillWeeklyFunction(gen)
    //log.info "Dishwasher refreshed"
  }

  static constraints = {
  }
}
