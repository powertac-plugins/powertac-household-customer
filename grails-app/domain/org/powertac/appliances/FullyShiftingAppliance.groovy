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

import org.powertac.common.configurations.HouseholdConstants

/**
 * This is the class for the appliance domain instances that can change / shift their load
 * without the need for the inhabitants interference. They see when it is best to shift their
 * load for the minimum cost of usage.
 * 
 * @author Antonios Chrysopoulos
 * @version 1, 13/02/2011
 */

class FullyShiftingAppliance extends Appliance{


  /** This function fills out all the days of the appliance functions for each day of the week
   * @param gen
   * @return
   */
  def fillWeeklyFunction(Random gen) {
    for (int i = 0;i < HouseholdConstants.DAYS_OF_WEEK; i++) fillDailyFunction(i,gen)
  }

  /** This function creates the weekly operation vector after the shifting for each day of the week
   * @param times
   * @param gen
   * @return
   */
  def createWeeklyOperationVector(int times,Random gen) {
    for (int i = 0;i < HouseholdConstants.DAYS_OF_WEEK; i++) operationVector.add(createDailyOperationVector(times,gen))
  }

  /** This function creates the daily operation vector after the shifting
   * @param times
   * @param gen
   * @return
   */
  def createDailyOperationVector(int times, Random gen) {

    // Creating Auxiliary Variables
    Vector v = new Vector(HouseholdConstants.QUARTERS_OF_DAY)

    // First initialize all to false
    for (int i = 0;i < HouseholdConstants.QUARTERS_OF_DAY;i++) v.add(false)

    // Then for the times it work add function quarters
    for (int i = 0;i < times;i++) {
      int quarter = gen.nextInt(HouseholdConstants.QUARTERS_OF_DAY)
      v.set(quarter,true)
    }
    return v
  }

  static constraints = {
  }
}
