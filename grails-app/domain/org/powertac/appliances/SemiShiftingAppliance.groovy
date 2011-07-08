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

import java.util.Random
import java.util.Vector

import org.powertac.common.configurations.HouseholdConstants

/**
 * This is the class for the appliance domain instances that can change / shift their load
 * but need to be programmed by the tenants or require them to be there when start / end 
 * functioning. That's the reason it has restricted shifting capabilities.
 *
 * @author Antonios Chrysopoulos
 * @version 1, 13/02/2011
 */


class SemiShiftingAppliance extends Appliance {

  /** This vector contains the weekdays that the appliance will be functioning */
  Vector days = new Vector()

  /** This function creates the daily operation vector after the shifting.
   * @param weekday
   * @param gen
   * @return
   */
  def createDailyOperationVector(int weekday, Random gen) {

    // Creating Auxiliary Variables
    Vector v = new Vector(HouseholdConstants.QUARTERS_OF_DAY)

    // First initialize all to false
    for (int i = 0;i < HouseholdConstants.QUARTERS_OF_DAY;i++) v.add(false)
    if (days.contains(weekday) && ((this instanceof Dryer) == false)) {
      int quarter = gen.nextInt(HouseholdConstants.END_OF_FUNCTION)
      v.set(quarter,true)
    }
    return v
  }

  /** This function creates the weekly operation vector after the shifting for each day of the week.
   * @param times
   * @param gen
   * @return
   */
  def createWeeklyOperationVector(int times, Random gen)
  {
    fillDays(times, gen)
    for (int i=0;i < HouseholdConstants.DAYS_OF_WEEK;i++) operationVector.add(createDailyOperationVector(i,gen))
  }

  /** This function fills out all the days of the appliance functions for each day of the week.
   * 
   * @return
   */
  def fillWeeklyFunction(Random gen)
  {
    for (int i = 0;i < HouseholdConstants.DAYS_OF_WEEK; i++) fillDailyFunction(i,gen)
  }

  /** This function fills out the vector that contains the days of the week tha the appliance is functioning.
   * 
   * @param times
   * @return
   */
  def fillDays(int times, Random gen)
  {
    for (int i=0; i < times; i++) {
      int day = gen.nextInt(HouseholdConstants.DAYS_OF_WEEK - 1)
      ListIterator iter = days.listIterator();
      while (iter.hasNext()) {
        int temp = (int)iter.next()
        if (day == temp) {
          day = day + 1
          iter = days.listIterator();
        }
      }
      days.add(day)
      java.util.Collections.sort(days);
    }
    java.util.Collections.sort(days);
    ListIterator iter = days.listIterator();
  }

  static constraints = {
  }

}
