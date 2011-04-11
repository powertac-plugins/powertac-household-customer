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


package org.powertac.persons

import java.util.HashMap;
import java.util.Vector;
import org.powertac.common.enumerations.Status;
import org.powertac.common.configurations.Constants;

/**
* This is the instance of the person type that works in shifts that may vary
* form week to week or from month to month. The consequence is that he has
* little time for leisure activities.
*
* @author Antonios Chrysopoulos
* @version 1, 13/02/2011
**/

class RandomlyAbsentPerson extends WorkingPerson {

	
  /** This is the initialization function. It uses the variable values for the
   * configuration file to create the person as it should for this type.
   * @param AgentName
   * @param hm
   * @param publicVacationVector
   * @return
   */

	def initialize(String AgentName, HashMap hm, Vector publicVacationVector) {
		
		// Variables Taken from the configuration file
		float sicknessMean = ((float)hm.get("SicknessMean"))
		float sicknessDev = ((float)hm.get("SicknessDev"))
		float leisureDurationMean = ((int)hm.get("LeisureDurationMean"))
		float leisureDurationDev = ((int)hm.get("LeisureDurationDev"))
		float RALeisure = ((int)hm.get("RALeisure"))
    int workingDurationMean = ((int)hm.get("WorkingDurationMean"))
    int workingDurationDev = ((int)hm.get("WorkingDurationDev"))
    int vacationDurationMean = ((int)hm.get("VacationDurationMean"))
    int vacationDurationDev = ((int)hm.get("VacationDurationDev"))
    
    // Filling the main variables
		name = AgentName
		status = Status.Normal
    Random gen = ensureRandomSeed()
    
	  // Filling the sickness and public Vacation Vectors
		sicknessVector = createSicknessVector(sicknessMean,sicknessDev)
		this.publicVacationVector = publicVacationVector
    
		// Filling the leisure variables
		int x = (int) (gen.nextGaussian() + RALeisure)
		leisureVector = createLeisureVector(x)
		leisureDuration = (int) (leisureDurationDev * gen.nextGaussian() + leisureDurationMean)
		
    // Filling Working variables
		int work = workingDaysRandomizer(hm)
		workingDays = createWorkingDaysVector(work)
		workingStartHour = createWorkingStartHour()
		workingDuration = (int) (workingDurationDev * gen.nextGaussian() + workingDurationMean)

    // Filling Vacation Variables
		vacationDuration = (int) (vacationDurationDev * gen.nextGaussian() + vacationDurationMean)
		vacationVector = createVacationVector(vacationDuration)
	}
	
  /** This function selects the shift of the worker. There three different shifts: 00:00 - 08:00
   * 08:00 - 16:00 and 16:00 - 24:00.
   * @return
   */

	def createWorkingStartHour() {
		
		Random gen = ensureRandomSeed()
		int x = gen.nextInt(Constants.NUMBER_OF_SHIFTS)
		return (x * Constants.HOURS_OF_SHIFT_WORK * Constants.QUARTERS_OF_HOUR)
		
	}
	
  /** This function fills out the leisure activities in the daily schedule
   * of the person in question.
   * @param weekday
   * @return
   */
	def addLeisureWorking(int weekday) {
		

		// Create auxiliary variables
		ListIterator iter = leisureVector.listIterator();
		Status st
    Random gen = ensureRandomSeed()
    
		// Checking the leisure day list
		while (iter.hasNext()) {


      // case current day has leisure activities
			if (iter.next() == weekday) {
		
				// Check the working hours
				int start = workingStartHour + workingDuration
		
        // case working on the first shift
				if (workingStartHour == Constants.SHIFT_START_1) {
		
					// Finding the window of leisure time
					int startq = gen.nextInt((Constants.LEISURE_WINDOW+1) - start) + (start + Constants.SHIFT_START_2)
				
					// Filling the leisure activity
					for (int i = startq;i < startq + leisureDuration;i++) {
		
		  			st = Status.Leisure
						dailyRoutine.set(i,st)
		
					}
		
		
			  } else  {
		
		
				  // case working on the second shift
					if (workingStartHour == Constants.SHIFT_START_2) {
						
            // Finding the window of leisure time
						int startq = start + gen.nextInt(Constants.LEISURE_WINDOW_SHIFT - start)
		
						// Filling the leisure activity
						for (int i = startq;i < startq + leisureDuration;i++) {
		
							st = Status.Leisure
							dailyRoutine.set(i,st)
		
						}
		
            // case working on the third shift
			  	} else  {
		
						// Finding the window of leisure time
						int startq = Constants.SHIFT_START_2 + gen.nextInt(Constants.SHIFT_START_3 - (Constants.LEISURE_WINDOW - 1))
		
						// Filling the leisure activity
						for (int i = startq;i < startq +leisureDuration;i++) {
		
							st = Status.Leisure
							dailyRoutine.set(i,st)
		
						}
		
					}
		
				}
		
			} 
		
    }
		
	}
	
  @ Override
	def fillWork() {

		
		// Create auxiliary variables
		Status st
		
		// case working on the first shift
		if (workingStartHour == Constants.SHIFT_START_1) {
		
		
			// Filling the working hours
			for (int i = Constants.SHIFT_START_1;i < workingDuration;i++) {
		
				st = Status.Working
				dailyRoutine.set(i,st)
		
			}
				
			// Filling the sleeping hours
			for (int i = workingDuration;i < workingDuration + Constants.SHIFT_START_2 ;i++) {
		
				st = Status.Sleeping
				dailyRoutine.set(i,st)
		
			}
		
			// Filling the normal hours
			for (int i = workingDuration + Constants.SHIFT_START_2;i < Constants.QUARTERS_OF_DAY;i++) {
		
				st = Status.Normal
				dailyRoutine.set(i,st)
		
			}
		
		
		} else  {
		
		
			// case working on the second shift
			if (workingStartHour == Constants.SHIFT_START_2) {
	
		
				// Filling the sleeping hours
				for (int i = Constants.START_OF_SLEEPING_1;i < Constants.END_OF_SLEEPING_1;i++) {
	
 
					st = Status.Sleeping
					dailyRoutine.set(i,st)
		
				}
		
		
				// Filling the normal hours
				for (int i = Constants.END_OF_SLEEPING_1;i < Constants.SHIFT_START_2;i++) {
		

					st = Status.Normal
					dailyRoutine.set(i,st)
		
        }
		
		
				// Filling the working hours
				for (int i = Constants.SHIFT_START_2;i < workingDuration + Constants.SHIFT_START_2 ;i++) {
		
					st = Status.Working
					dailyRoutine.set(i,st)
		
				}
		
		
				// Filling the normal hours
				for (int i = workingDuration + Constants.SHIFT_START_2;i < Constants.START_OF_SLEEPING_1;i++) {
		
					st = Status.Normal
					dailyRoutine.set(i,st)
		
				}
		
		
				// Filling the sleeping hours
				for (int i = Constants.START_OF_SLEEPING_1;i < Constants.QUARTERS_OF_DAY;i++) {
		
					st = Status.Sleeping
					dailyRoutine.set(i,st)
		
				}
		
		
      // case working on the third shift
      } else  {
		
		
				// Filling the sleeping hours
				for (int i = Constants.START_OF_SLEEPING_1;i < Constants.END_OF_SLEEPING_1;i++) {
		
					st = Status.Sleeping
					dailyRoutine.set(i,st)
		
				}
		
		
				// Filling the normal hours
				for (int i = Constants.END_OF_SLEEPING_1;i < Constants.SHIFT_START_3 ;i++) {
		
					st = Status.Normal
					dailyRoutine.set(i,st)
		
				}
		
		
				// This is an agent decision.
				if (workingDuration > Constants.HOURS_OF_SHIFT_WORK * Constants.QUARTERS_OF_HOUR) {
		
          // Filling the working hours
					for (int i = Constants.SHIFT_START_3;i < Constants.QUARTERS_OF_DAY;i++) {
		

						st = Status.Working
						dailyRoutine.set(i,st)
		
					}
		
				} else  {
		
		
					// Filling the working hours
					for (int i = Constants.SHIFT_START_3;i < Constants.SHIFT_START_3 + workingDuration;i++) {
		
            if (i >= Constants.QUARTERS_OF_DAY) break
						st = Status.Working
						dailyRoutine.set(i,st)
		
					}
		
		
					// Filling the sleeping hours
					for (int i =  Constants.SHIFT_START_3 + workingDuration;i < Constants.QUARTERS_OF_DAY;i++) {
		
						st = Status.Sleeping
						dailyRoutine.set(i,st)
            
					}
		
				}
		
			}
		
		}

	}
	
  @ Override
	void refresh(HashMap hm) {
		
    
		// Renew Variables
		float leisureDurationMean = ((int)hm.get("LeisureDurationMean"))
		float leisureDurationDev = ((int)hm.get("LeisureDurationDev"))
		float RALeisure = ((int)hm.get("RALeisure"))
		float vacationAbsence = ((float)hm.get("VacationAbsence"))
	
    // This is a task.
		int work = workingDaysRandomizer(hm)
		workingDays = createWorkingDaysVector(work)
		workingStartHour = createWorkingStartHour()
		
    // This is a task.
		Random gen = ensureRandomSeed()
		int x = (int) (gen.nextGaussian() + RALeisure)
		leisureDuration = (int) (leisureDurationDev * gen.nextGaussian() + leisureDurationMean)
		leisureVector = createLeisureVector(x)
		weeklyRoutine = fillWeeklyRoutine(vacationAbsence)

	}
	
  static constraints = {
  }
}
