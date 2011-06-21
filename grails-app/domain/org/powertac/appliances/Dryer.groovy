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

class Dryer extends SemiShiftingAppliance {

  @ Override
  def initialize(String household,ConfigObject conf,Random gen) {


    // Filling the base variables
    name = household + " Dryer"
    saturation = conf.household.appliances.dryer.DryerSaturation
    consumptionShare = (float) (Constants.PERCENTAGE * (Constants.DRYER_CONSUMPTION_SHARE_VARIANCE * gen.nextGaussian() + Constants.DRYER_CONSUMPTION_SHARE_MEAN))
    baseLoadShare = Constants.PERCENTAGE * Constants.DRYER_BASE_LOAD_SHARE
    power = (int) (Constants.DRYER_POWER_VARIANCE * gen.nextGaussian() + Constants.DRYER_POWER_MEAN)
    cycleDuration = Constants.DRYER_DURATION_CYCLE
    od = false
    inUse = false
    probabilitySeason = fillSeason(Constants.DRYER_POSSIBILITY_SEASON_1,Constants.DRYER_POSSIBILITY_SEASON_2,Constants.DRYER_POSSIBILITY_SEASON_3)
    probabilityWeekday = fillDay(Constants.DRYER_POSSIBILITY_DAY_1,Constants.DRYER_POSSIBILITY_DAY_2,Constants.DRYER_POSSIBILITY_DAY_3)
    times = conf.household.appliances.dryer.DryerWeeklyTimes + (int)(applianceOf.members.size() / 2)

    // Inform the washing machine for the existence of the dryer
    this.applianceOf.appliances.each {
      Object o = (Object) it
      if (o instanceof WashingMachine) {
        o.dryerFlag = true
        o.dryerPower = power
      }
    }

    createWeeklyOperationVector(times,gen)
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
    int start = washingEnds(weekday)
    if (start > 0) {
      for (int i = start;i < Constants.QUARTERS_OF_DAY - 1;i++) {
        if (applianceOf.isEmpty(weekday,i) == false) {
          operation.set(i, true)
          for (int j = i;j < i + Constants.DRYER_SECOND_PHASE;j++) {
            loadVector.set(j,power)
            dailyOperation.set(j,true)
          }
          for (int k = i+Constants.DRYER_SECOND_PHASE;k < i+Constants.DRYER_THIRD_PHASE;k++) {
            loadVector.set(k,loadVector.get(k-1)-Constants.DRYER_THIRD_PHASE_LOAD)
            dailyOperation.set(k,true)
            if (k == Constants.QUARTERS_OF_DAY-1) break
          }
          i = Constants.QUARTERS_OF_DAY
        }
      }
      weeklyLoadVector.add(loadVector)
      weeklyOperation.add(dailyOperation)
      operationVector.set(weekday, operation)
    } else  {
      weeklyLoadVector.add(loadVector)
      weeklyOperation.add(dailyOperation)
      operationVector.set(weekday, operation)
    }
  }

  @Override
  def createDailyPossibilityOperationVector(int day) {

    def possibilityDailyOperation = new Vector()

    for (int j = 0;j < Constants.QUARTERS_OF_DAY;j++) {
      // The dishwasher needs for someone to be in the house at the beginning of its function
      if (applianceOf.isEmpty(day,j) == false) possibilityDailyOperation.add(true)
      else possibilityDailyOperation.add(false)
    }

    return possibilityDailyOperation
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

    // Search for the washing machine to take its schedule in consideration
    this.applianceOf.appliances.each {
      Object o = (Object) it
      if (o instanceof WashingMachine) v = o.getWeeklyOperation().get(weekday)
    }
    for (int i = (Constants.QUARTERS_OF_DAY - 1);i > 0;i--) {
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
    log.info("Name = " + name)
    log.info("Saturation = " + saturation)
    log.info("Consumption Share = " + consumptionShare)
    log.info("Base Load Share = " + baseLoadShare)
    log.info("Power = " + power)
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

    // Printing Function Day Vector
    ListIterator iter = days.listIterator();
    log.info("Days Vector = ")
    while (iter.hasNext()) log.info("Day  " + iter.next())

    // Printing Operation Vector
    iter = operationVector.listIterator();
    log.info("Operation Vector = ")
    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {
      log.info("Day " + (i))
      iter =operationVector.get(i).listIterator()
      for (int j = 0;j < Constants.QUARTERS_OF_DAY; j++) log.info("Quarter : " + (j+1) + "  " + iter.next())
    }

    // Printing Weekly Operation Vector and Load Vector
    log.info("Weekly Operation Vector and Load = ")
    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {
      log.info("Day " + (i))
      iter = weeklyOperation.get(i).listIterator();
      ListIterator iter2 = weeklyLoadVector.get(i).listIterator();
      for (int j = 0;j < Constants.QUARTERS_OF_DAY; j++) log.info("Quarter " + (j+1) + " = " + iter.next() + "   Load = " + iter2.next())
    }
  }

  /** In this function we take the days of function of the washing machine in order
   * to make dryer work the same days.
   * @param times
   * @return
   */
  def fillDays(int times) {
    // Creating auxiliary variable
    boolean flag = true

    // Check the appliances one by one to find the washing machine
    this.applianceOf.appliances.each {
      Object o = it
      if (o instanceof WashingMachine) {
        days = o.getDays()
        flag = false
      }
    }
  }

  @ Override
  def dailyShifting(Random gen,Tariff tariff,Instant now, int day){
    // Dryer's daily shifting is done by the washing machine for safety
    BigInteger[] newControllableLoad = new BigInteger[Constants.HOURS_OF_DAY]
    for (int j=0;j < Constants.HOURS_OF_DAY;j++) newControllableLoad[j] = 0

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
