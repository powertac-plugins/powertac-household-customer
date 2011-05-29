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
import org.powertac.common.configurations.Constants;

/**
 * This is the instance of the person type that works. In addition to the simple
 * persons they are working certain hours a day and they have less time for leisure
 * activities.
 * 
 * @author Antonios Chrysopoulos
 * @version 1, 13/02/2011
 **/

class WorkingPerson extends Person{

  /** A vector the contains the working days of the week **/
  Vector workingDays = null

  /** This variable describes the duration of the work procedure **/
  int workingDuration = 0

  /** This variables shows how many days are working vacation for this person **/
  int vacationDuration = 0

  /** This is a vector of the vacation days of the year for this person**/
  Vector vacationVector = null

  /** The time of the day that the person begins to work **/
  int workingStartHour = 0


  /** This function fills out the working days' vector of the person
   * by choosing randomly days of the week, while the amount of days is
   * different for each person type.
   * @param days
   * @return
   */
  def createWorkingDaysVector(int days, Random gen) 
  {
    // Creating an auxiliary variables
    Vector v = new Vector(days)

    if (days < Constants.WEEKDAYS) {
      for (int i = 0; i < days; i++) {
        int x =  (gen.nextInt(1) * (Constants.WEEKDAYS - 1)) + 1
        ListIterator iter = v.listIterator();
        while (iter.hasNext()) {
          int temp = (int)iter.next()
          if (x == temp) {
            x = x + 1
            iter = v.listIterator();
          } 
        }
        v.add(x)
      }
      java.util.Collections.sort(v);
      return v
    } else  {
      v.add(1)
      v.add(2)
      v.add(3)
      v.add(4)
      v.add(5)
      if (days == Constants.WEEKDAYS) {
      } else  {
        if (days == Constants.DAYS_OF_WEEK) {
          v.add(6)
          v.add(0)
        } else  {
          if (gen.nextFloat() > 0.5 ) {
            v.add(6)
          } else  {
            v.add(0)
          }
        }
      }
      java.util.Collections.sort(v);
      return v
    }
  }

  /** This function fills out the work vacation days' vector of the person
   * by choosing randomly days of the year that the person chooses as vacations.
   * He may choose to go on vacation for short periods, but the summary of the days
   * must be in bounds.
   * @param duration
   * @return
   */
  def createVacationVector(int duration, Random gen) {

    // Create auxiliary variables
    Vector v = new Vector(duration)
    int counter = duration
    int counter2 = 0
    while (counter > 0) {
      int x = (int) gen.nextInt(Constants.DAYS_OF_COMPETITION - 1) + 1
      counter2 = 1 + (int)(gen.nextInt(counter))
      ListIterator iter = v.listIterator()
      while (counter2 > 0) {
        v.add(x)
        counter = counter - 1
        counter2 = counter2 - 1
        x = x + 1
      }
    }
    java.util.Collections.sort(v);
    return v
  }

  /** This function chooses randomly the number of the working days of a person
   * The percentages used where taken from a thesis on the subject, based on demographic
   * data.
   * @param hm
   * @return
   */
  def workingDaysRandomizer(ConfigObject conf, Random gen) 
  {
    def returnValue
    int oneDay = conf.household.work.daysPerWeek.OneDay
    int twoDays = conf.household.work.daysPerWeek.TwoDays
    int threeDays = conf.household.work.daysPerWeek.ThreeDays
    int fourDays = conf.household.work.daysPerWeek.FourDays
    int fiveDays = conf.household.work.daysPerWeek.FiveDays
    int sixDays = conf.household.work.daysPerWeek.SixDays
    int sevenDays = conf.household.work.daysPerWeek.SevenDays

    int x = (int) gen.nextInt(Constants.PERCENTAGE)
    if (x < fiveDays) {
      returnValue = 5
    } else  {
      if (x >= fiveDays & x < (fiveDays + sixDays)) {
        returnValue = 6
      } else  {
        if (x >= (fiveDays + sixDays) & x < (fiveDays + sixDays + fourDays)) {
          returnValue = 4
        } else  {
          if (x >= (fiveDays + sixDays + fourDays) & x < (fiveDays + sixDays + fourDays + threeDays)) {
            returnValue = 3
          } else  {
            if (x >= (fiveDays + sixDays + fourDays + threeDays) & x < (fiveDays + sixDays + fourDays + threeDays+twoDays)) {
              returnValue = 2
            } else  {
              if (x >= (fiveDays + sixDays + fourDays + threeDays+twoDays) & x < (fiveDays + sixDays + fourDays + threeDays+twoDays+sevenDays)) {
                returnValue  = 7
              } else  {
                returnValue = 1
              }
            }
          }
        }
      }
    }
    return returnValue

  }

  @ Override
  void showStatus() 
  {
    // Printing the base variable
    log.info("Name = " + name)

    // Printing Sickness variables
    log.info("Sickness Days = ")
    ListIterator iter = sicknessVector.listIterator();
    while (iter.hasNext()) log.info(iter.next());

    // Printing Leisure variables
    log.info("Leisure Days of Week = ");
    iter = leisureVector.listIterator();	
    while (iter.hasNext()) log.info(iter.next());
    log.info("Leisure Duration = " + leisureDuration);

    // Printing working variables
    log.info("Working Days = ");
    iter = workingDays.listIterator();
    while (iter.hasNext()) log.info(iter.next());
    log.info("Working Duration = " + workingDuration);
    log.info("Working Starting Hour = " + workingStartHour)

    // Printing vacation variables
    log.info("Vacation Duration = " + vacationDuration);
    log.info("Vacation Days = ");
    iter = vacationVector.listIterator();
    while (iter.hasNext()) log.info(iter.next());
    log.info("Public Vacation of Year = ");
    iter = publicVacationVector.listIterator();

    while (iter.hasNext()) log.info(iter.next());

    // Printing Weekly Schedule
    log.info("Weekly Routine : ")
    iter = weeklyRoutine.get(0).listIterator();

    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {
      log.info("Day " + (i))
      iter = weeklyRoutine.get(i).listIterator();
      for (int j =0;j < Constants.QUARTERS_OF_DAY;j++) log.info("Quarter : " + (j+1) + " Status : " + iter.next())
    }
  }

  /** This function fill the daily program of the person with the suitable working
   * activities taking in consideration the working habits, duration and shifts.
   * @return
   */

  def fillWork() {}

  static constraints = {
  }

}
