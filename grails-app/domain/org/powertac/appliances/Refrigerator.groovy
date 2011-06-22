
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

import org.joda.time.Instant
import org.powertac.common.Tariff
import org.powertac.common.TimeService
import org.powertac.common.configurations.Constants

/**
 * Refrigerator is the fridge we all use in our households. This appliance
 * can automatically change the freezing cyles in order to save energy,
 * without problem without tenants manipulation. So this is a fully shifting
 * appliance.
 *
 * @author Antonios Chrysopoulos
 * @version 1, 13/02/2011
 */

class Refrigerator extends FullyShiftingAppliance {

  @ Override
  def initialize(String household, ConfigObject conf,Random gen) {

    // Filling the base variables
    name = household + " Refrigerator"
    saturation =  conf.household.appliances.refrigerator.RefrigeratorSaturation
    consumptionShare = (float) (Constants.PERCENTAGE * (Constants.REFRIGERATOR_CONSUMPTION_SHARE_VARIANCE * gen.nextGaussian() + Constants.REFRIGERATOR_CONSUMPTION_SHARE_MEAN))
    baseLoadShare = Constants.PERCENTAGE * Constants.REFRIGERATOR_BASE_LOAD_SHARE
    power = (int) (Constants.REFRIGERATOR_POWER_VARIANCE * gen.nextGaussian() + Constants.REFRIGERATOR_POWER_MEAN)
    cycleDuration = Constants.REFRIGERATOR_DURATION_CYCLE
    od = false
    inUse = false
    probabilitySeason = fillSeason(Constants.REFRIGERATOR_POSSIBILITY_SEASON_1,Constants.REFRIGERATOR_POSSIBILITY_SEASON_2,Constants.REFRIGERATOR_POSSIBILITY_SEASON_3)
    probabilityWeekday = fillDay(Constants.REFRIGERATOR_POSSIBILITY_DAY_1,Constants.REFRIGERATOR_POSSIBILITY_DAY_2,Constants.REFRIGERATOR_POSSIBILITY_DAY_3)
  }

  @Override
  def createDailyPossibilityOperationVector(int day) {

    def possibilityDailyOperation = new Vector()

    // Freezer can work anytime
    for (int j = 0;j < Constants.QUARTERS_OF_DAY;j++) {
      possibilityDailyOperation.add(true)
    }

    return possibilityDailyOperation
  }

  @ Override
  def fillDailyFunction(int weekday, Random gen) {
    // Initializing Variables
    loadVector = new Vector()
    dailyOperation = new Vector()

    for (int i = 0;i < Constants.QUARTERS_OF_DAY;i++) {
      if (i % cycleDuration == 0) {
        loadVector.add(power)
        dailyOperation.add(true)
      } else  {
        loadVector.add(0)
        dailyOperation.add(false)
      }
    }
    weeklyLoadVector.add(loadVector)
    weeklyOperation.add(dailyOperation)
    operationVector.add(dailyOperation)
  }

  @ Override
  def dailyShifting(Random gen,Tariff tariff,Instant now, int day){

    long[] newControllableLoad = new long[Constants.HOURS_OF_DAY]

    Instant now2 = now

    // Daily operation is seperated in shifting periods
    for (int i=0;i < Constants.REFRIGERATOR_SHIFTING_PERIODS;i++){
      def minvalue = Double.POSITIVE_INFINITY
      def minindex = 0;

      // For each shifting period we search the best value
      for (int j =0;j < Constants.REFRIGERATOR_SHIFTING_INTERVAL;j++){
        if ((minvalue > tariff.getUsageCharge(now2)) || (minvalue == tariff.getUsageCharge(now2) && gen.nextFloat() > Constants.HALF)) {
          minvalue = tariff.getUsageCharge(now2)
          minindex = j
        }
        now2 = now2 + TimeService.HOUR
      }
      newControllableLoad[Constants.REFRIGERATOR_SHIFTING_INTERVAL*i+minindex] = Constants.QUARTERS_OF_HOUR*power
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
