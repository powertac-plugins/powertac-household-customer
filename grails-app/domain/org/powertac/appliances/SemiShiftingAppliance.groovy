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

import java.util.Vector;
import org.powertac.common.configurations.Constants

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
   * 
   * @param weekday
   * @return
   */
	def createDailyOperationVector(int weekday) {
		
		// Creating Auxiliary Variables
		Vector v = new Vector(Constants.QUARTERS_OF_DAY)
    Random gen = ensureRandomSeed()
		
		// First initialize all to false
		for (int i = 0;i < Constants.QUARTERS_OF_DAY;i++) {
		
      v.add(false)
		
    }
	
    // case the appliance is working at that day
		if (days.contains(weekday) && ((this instanceof Dryer) == false)) {
		
			int quarter = gen.nextInt(Constants.END_OF_FUNCTION)
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
		
    // Filling days of function
		fillDays(times)
		
		for (int i=0;i < Constants.DAYS_OF_WEEK;i++) {
		
			operationVector.add(createDailyOperationVector(i))
		
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
	
  /** This function fills out the vector that contains the days of the week tha the appliance is functioning.
   * 
   * @param times
   * @return
   */
	def fillDays(int times) {
		

		for (int i=0; i < times; i++) {
		
			// This is a task.
			Random gen = ensureRandomSeed()
			int day = gen.nextInt(Constants.DAYS_OF_WEEK - 1)
			ListIterator iter = days.listIterator();
		
			// Checking if the day is already picked
			while (iter.hasNext()) {
		
				int temp = (int)iter.next()
		
				// Case the day is in the vector
				if (day == temp) {
		
					day = day + 1
					iter = days.listIterator();
		
				} 
			
      }

			days.add(day)
      // Sorting vector values
			java.util.Collections.sort(days);
		
    }
		
		// Sorting vector values
		java.util.Collections.sort(days);
		ListIterator iter = days.listIterator();
		
	}
	
	
  static constraints = {
  }
  
}
