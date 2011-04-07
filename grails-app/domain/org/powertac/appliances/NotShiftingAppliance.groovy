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

import org.powertac.common.configurations.Constants

/**
* This is the class for the appliance domain instances that cannot change / shift their load
* at all. Most of them are appliance that require the tenant's presence in order to begin 
* functioning
* @author Antonios Chrysopoulos
* @version 1, 13/02/2011
*/


class NotShiftingAppliance extends Appliance {

  /** This function creates the daily operation vector after the shifting
   * 
   * @param times
   * @return
   */
	def createDailyOperationVector(int times) {

    // Creating Auxiliary Variables
    Random gen = ensureRandomSeed()
    Vector v = new Vector(Constants.QUARTERS_OF_DAY)

    // First initialize all to false
    for (int i = 0;i < Constants.QUARTERS_OF_DAY;i++) {

      v.add(false)

    }


    // Then for the times it work add function quarters
    for (int i = 0;i < times;i++) {

      int quarter = gen.nextInt(Constants.QUARTERS_OF_DAY)
      v.set(quarter,true)

    }

    return v
        
  }
	
  
  /** This function creates the weekly operation vector after the shifting for each day of the week.
   * 
   * @param times
   * @return
   */
	def createWeeklyOperationVector(int times) {


    // This is a loop.
    for (int i = 0;i < Constants.DAYS_OF_WEEK; i++) {

      // This is a task.
      operationVector.add(createDailyOperationVector(times))

     }

  }
	
  /** This function fills out all the days of the appliance functions for each day of the week.
   * 
   * @return
   */
	def fillWeeklyFunction() {
		
		for (int i = 0;i < Constants.DAYS_OF_WEEK; i++) {
		
      fillDailyFunction(i)
		
		}
		
	}
	
	
  static constraints = {
  }
  
}