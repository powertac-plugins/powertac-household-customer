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
import java.util.Random;

import org.powertac.common.configurations.Constants

/**
 * ICT are the appliances that are utilized mainly for work or to communicate with others
 * from the household such as computers, telephone devices etc. They works only when
 * someone is at home and uses them. So it's a not shifting appliance.
 *
 * @author Antonios Chrysopoulos
 * @version 1, 13/02/2011
 */

class ICT extends NotShiftingAppliance{

  @ Override
  def initialize(HashMap hm, Random gen) {

    // Filling the base variables
    name = "ICT"
    saturation = (float)hm.get("ICTSaturation")
    consumptionShare = (float) (Constants.PERCENTAGE * (Constants.ICT_CONSUMPTION_SHARE_VARIANCE * gen.nextGaussian() + Constants.ICT_CONSUMPTION_SHARE_MEAN))
    baseLoadShare = Constants.PERCENTAGE * Constants.ICT_BASE_LOAD_SHARE
    power = (int) (Constants.ICT_POWER_VARIANCE * gen.nextGaussian() + Constants.ICT_POWER_MEAN)
    cycleDuration = Constants.ICT_DURATION_CYCLE
    times = (float)hm.get("ICTDailyTimes")
    od = false
    inUse = false
    probabilitySeason = fillSeason(Constants.ICT_POSSIBILITY_SEASON_1,Constants.ICT_POSSIBILITY_SEASON_2,Constants.ICT_POSSIBILITY_SEASON_3)
    probabilityWeekday = fillDay(Constants.ICT_POSSIBILITY_DAY_1,Constants.ICT_POSSIBILITY_DAY_2,Constants.ICT_POSSIBILITY_DAY_3)
    createWeeklyOperationVector(cycleDuration + applianceOf.members.size(), gen)
  }

  @ Override
  def fillDailyFunction(int weekday,Random gen) {
    // Initializing and Creating auxiliary variables
    loadVector = new Vector()
    dailyOperation = new Vector()
    Vector operation = operationVector.get(weekday)

    // For each quarter of a day
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
  }

  @ Override
  def refresh(Random gen) {
    createWeeklyOperationVector(times + applianceOf.members.size(), gen)
    fillWeeklyFunction(gen)
    log.info "ICT refreshed"
  }

  static constraints = {
  }
}
