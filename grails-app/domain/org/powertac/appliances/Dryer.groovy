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
 * Dryer appliances are utilized by the inhabitants to order to dry the freshly
 * washed clothes. That means that the household should contain an washiung machine
 * in order to have a dryer. Also, the clothes should be placed in the washing machine
 * shortly after the washing is finished. So this is a semi-shifting appliance.
 *
 * @author Antonios Chrysopoulos
 * @version 1, 13/02/2011
 */

class Dryer extends SemiShiftingAppliance{

  @ Override
  def initialize(HashMap hm) {

    // Creating Auxiliary Variables
    Random gen = ensureRandomSeed()

    // Filling the base variables
    name = "Dryer"
      saturation = (float)hm.get("DryerSaturation")

      consumptionShare = (float) (Constants.PERCENTAGE * (Constants.DRYER_CONSUMPTION_SHARE_VARIANCE * gen.nextGaussian() + Constants.DRYER_CONSUMPTION_SHARE_MEAN))
      baseLoadShare = Constants.PERCENTAGE * Constants.DRYER_BASE_LOAD_SHARE
      power = (int) (Constants.DRYER_POWER_VARIANCE * gen.nextGaussian() + Constants.DRYER_POWER_MEAN)
      cycleDuration = Constants.DRYER_DURATION_CYCLE

      od = false
      inUse = false
      probabilitySeason = fillSeason(Constants.DRYER_POSSIBILITY_SEASON_1,Constants.DRYER_POSSIBILITY_SEASON_2,Constants.DRYER_POSSIBILITY_SEASON_3)
      probabilityWeekday = fillDay(Constants.DRYER_POSSIBILITY_DAY_1,Constants.DRYER_POSSIBILITY_DAY_2,Constants.DRYER_POSSIBILITY_DAY_3)

      times = (int)hm.get("DishwasherWeeklyTimes")
      createWeeklyOperationVector((int)(times + applianceOf.members.size() / 2))

  }

  @ Override
  def fillDailyFunction(int weekday) {

    // Initializing Variables
    loadVector = new Vector()
    dailyOperation = new Vector()
    Vector operation = operationVector.get(weekday)

    for (int l = 0;l < Constants.QUARTERS_OF_DAY; l++) {

      loadVector.add(0)
      dailyOperation.add(false)

    }

    // Checking where the appliance's function should begin
    int start = washingEnds(weekday)

    // case there is function
    if (start > 0) {


      for (int i = start;i < Constants.QUARTERS_OF_DAY - 1;i++) {

        // case the household is not empty
        if (applianceOf.isEmpty(i+1) == false) {

          // Filling the dryer operation accordingly
          operation.set(i, true)

          for (int j = i;j < i + Constants.DRYER_SECOND_PHASE;j++) {

            loadVector.set(j,power)
            dailyOperation.set(j,true)

          }

          for (int k = i+Constants.DRYER_SECOND_PHASE;k < i+Constants.DRYER_THIRD_PHASE;k++) {

            loadVector.set(k,power - Constants.DRYER_THIRD_PHASE_LOAD *(k - (i + Constants.DRYER_SECOND_PHASE - 1)))
            dailyOperation.set(k,true)
            if (k == Constants.QUARTERS_OF_DAY) break
          }

          i = Constants.QUARTERS_OF_DAY

        } 

      }

      // Fill the vectors accordingly
      weeklyLoadVector.add(loadVector)
      weeklyOperation.add(dailyOperation)
      operationVector.set(weekday, operation)

    } else  {

      // Fill the vectors accordingly
      weeklyLoadVector.add(loadVector)
      weeklyOperation.add(dailyOperation)
      operationVector.set(weekday, operation)

    }

  }

  /** This function is utilized in order to find when the washing machine ends its function
   * in order to put the dryer in use soon afterwards.
   * @param weekday
   * @return
   */
  def washingEnds(int weekday) {


    // Creating auxiliary variables
    Vector v = new Vector()
    int start = 0


    // Search for washing machine in household appliances
    this.applianceOf.appliances.each {

      Object o = (Object) it

      // case washing machine found
      if (o instanceof WashingMachine) {

        v = o.getWeeklyOperation().get(weekday)

      } 
    }

    // search when the washing machine is operating for the last quarter
    for (int i = (Constants.QUARTERS_OF_DAY - 1);i > 0;i--) {


      // if we find the washing machine working
      if (v.get(i) == true) {

        start = i+1
        i = 0

      } 

    }

    return start

  }

  @ Override
  def showStatus() {

    // Printing basic variables
    System.out.println("Name = " + name)
    System.out.println("Saturation = " + saturation)
    System.out.println("Consumption Share = " + consumptionShare)
    System.out.println("Base Load Share = " + baseLoadShare)
    System.out.println("Power = " + power)

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

    while (iter.hasNext()) {

      System.out.println("Day  " + iter.next())

    }

    // Printing Operation Vector
    iter = operationVector.listIterator();
    System.out.println("Operation Vector = ")

    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {

      System.out.println("Day " + (i))
      iter =operationVector.get(i).listIterator();

      for (int j = 0;j < Constants.QUARTERS_OF_DAY; j++) {

        System.out.println("Quarter : " + (j+1) + "  " + iter.next())

      }


    }

    // Printing Weekly Operation Vector and Load Vector
    System.out.println("Weekly Operation Vector and Load = ")

    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {

      System.out.println("Day " + (i))
      iter = weeklyOperation.get(i).listIterator();
      ListIterator iter2 = weeklyLoadVector.get(i).listIterator();

      for (int j = 0;j < Constants.QUARTERS_OF_DAY; j++) {

        System.out.println("Quarter " + (j+1) + " = " + iter.next() + "   Load = " + iter2.next())

      }

    }

  }

  /** In this function we take the days of function of the washing machine in order
   * to make dryer work the same days.
   */
  def fillDays(int times) {

    // Creating auxiliary variable
    boolean flag = true

    // Check the appliances one by one to find the washing machine
    this.applianceOf.appliances.each {

      Object o = it

      // case washing machine found copy the function days vector
      if (o instanceof WashingMachine) {

        days = o.getDays()
        flag = false

      } 

    }

  }

  @ Override
  def refresh() {

    createWeeklyOperationVector((int)(times + applianceOf.members.size() / 2))
    fillWeeklyFunction()
    System.out.println("Dryer refreshed")

  }

  static constraints = {
  }

}
