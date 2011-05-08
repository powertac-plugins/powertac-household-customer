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


package org.powertac.consumers

import java.util.HashMap
import java.util.Random
import java.util.Vector

import org.powertac.common.*
import org.powertac.common.configurations.Constants
import org.powertac.common.enumerations.*

/**
 * The environment domain class contains the villages the cities and the suburbs and so on.
 * You add as many of them as you want and use them as a different consumer or producer of
 * energy as you wish.
 *
 * @author Antonios Chrysopoulos
 * @version 1, 13/02/2011
 */

class Environment {

  // autowire
  def timeService
  def RandomSeedService

  /** This variable contains the HashMap created by the configuration file. **/
  HashMap hm = new HashMap()

  Random randomGen = null
  
  static hasMany = [villages:Village]

  
  /** This is the initialization function. It uses the variable values for the
   * configuration file to create the villages, cities or anything else that may
   * exist in the environment.
   *
   * @param hash
   * @return
   */
  def initialize(HashMap hash) {
    // Initializing basic variables
    Random gen = ensureRandomSeed()
    setHm(hash)
    int number = (int)hm.get("NumberOfVillages")
    for (int i = 1; i < number+1;i++){
      def villageInfo = new CustomerInfo(Name: "Village " + i,customerType: CustomerType.CustomerHousehold, powerTypes: [PowerType.CONSUMPTION])
      villageInfo.save()
      def village = new Village(CustomerInfo: villageInfo)
      village.initialize(hash,gen)
      village.init()
      village.save()
      this.addToVillages(village)
    }
    log.info "End of initialization"
    this.save()
  }

  /** This function represents the actions performed by each entity that exists in the 
   * environment in question.
   * @param counter
   * @return
   */
  def step() {
    // Finding time step
    int counter = ((timeService.currentTime.millis - timeService.start)/3600000) + 1
    int day = (int) (counter / Constants.QUARTERS_OF_DAY)+1
    int quarter = (int) (counter %  Constants.QUARTERS_OF_DAY)
    int week = (int) (day / Constants.DAYS_OF_WEEK)+1
    int weekday = (int) (day % Constants.DAYS_OF_WEEK)
    int dayOfWeek
    if (weekday == 0) {
      dayOfWeek = Constants.DAYS_OF_WEEK
    } else  {
      dayOfWeek = weekday
    }
    if (quarter == 0) {
      quarter = Constants.QUARTERS_OF_DAY
      dayOfWeek--
      day--
    }
    this.villages.each{
      System.out.print("Day: " + day + " Week: " + week + " Weekday: " + dayOfWeek + "  Quarter: " + quarter)
      System.out.println()
      it.step(weekday,quarter)
      if (quarter == Constants.QUARTERS_OF_DAY) {
        System.out.println()
        System.out.println("Summary of Daily Load for day " + day)
        it.printDailyLoad(weekday)
      }
      if (dayOfWeek == (Constants.DAYS_OF_WEEK-1) && quarter == Constants.QUARTERS_OF_DAY) {
        System.out.println("Refreshing Village Weekly Load")
        System.out.println()
        it.refresh(hm)
        it.fillAggWeeklyLoad()
        it.showAggWeeklyLoad()
      }
    }
  }

  private Random ensureRandomSeed ()
  {
    if (randomGen == null) {
      long randomSeed = randomSeedService.nextSeed('HouseholdCustomerService', 'household', 'model')
      randomGen = new Random(randomSeed)
    }
    return randomGen
  }
  

  static auditable = true

  static constraints = {
  }
}
