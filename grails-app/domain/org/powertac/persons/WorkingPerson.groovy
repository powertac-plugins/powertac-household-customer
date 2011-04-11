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
  def createWorkingDaysVector(int days) {

    // Creating an auxiliary variables
    Vector v = new Vector(days)
    Random gen = ensureRandomSeed()

    // Case the days are less than five
    if (days < Constants.WEEKDAYS) {

      // Loop for the amount of working days
      for (int i = 0; i < days; i++) {

        int x =  (gen.nextInt(1) * (Constants.WEEKDAYS - 1)) + 1
        ListIterator iter = v.listIterator();

        // Check through the previous days the same day exists
        while (iter.hasNext()) {

          int temp = (int)iter.next()

          // Case it exists
          if (x == temp) {

            // Take the next day in vector
            x = x + 1
            iter = v.listIterator();

          } 

        }

        v.add(x)

      }

      // Sort the list of working days
      java.util.Collections.sort(v);
      return v

      // case the days are five or more
    } else  {

      // This is a task.
      v.add(1)
      v.add(2)
      v.add(3)
      v.add(4)
      v.add(5)

      // If the days are five
      if (days == Constants.WEEKDAYS) {

        // If they are more
      } else  {

        // If the days are seven
        if (days == Constants.DAYS_OF_WEEK) {

          // This is a task.
          v.add(6)
          v.add(0)

          // case they are six
        } else  {


          // Choose one of the two left
          if (gen.nextFloat() > 0.5 ) {

            // This is a task.
            v.add(6)

          } else  {

            // This is a task.
            v.add(0)

          }

        }

      }

      // Sort the list of days
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
  def createVacationVector(int duration) {

    // Create auxiliary variables
    Vector v = new Vector(duration)
    int counter = duration
    int counter2 = 0
    Random gen = ensureRandomSeed()

    // Choose the days to begin vacation period
    while (counter > 0) {

      int x = (int) gen.nextInt(Constants.DAYS_OF_YEAR - 1) + 1
      counter2 = 1 + (int)(gen.nextInt(counter))
      ListIterator iter = v.listIterator()

      // Add the days of the vacation period
      while (counter2 > 0) {

        // Add day to the vector
        v.add(x)
        counter = counter - 1
        counter2 = counter2 - 1
        x = x + 1

      }

    }

    // Sort the days vector
    java.util.Collections.sort(v);
    return v

  }

  /** This function chooses randomly the number of the working days of a person
   * The percentages used where taken from a thesis on the subject, based on demographic
   * data.
   * @param hm
   * @return
   */
  def workingDaysRandomizer(HashMap hm) {

    def returnValue

    // Take variables from configuration file
    int oneDay = ((int)hm.get("OneDay"))
    int twoDays = ((int)hm.get("TwoDays"))
    int threeDays = ((int)hm.get("ThreeDays"))
    int fourDays = ((int)hm.get("FourDays"))
    int fiveDays = ((int)hm.get("FiveDays"))
    int sixDays = ((int)hm.get("SixDays"))
    int sevenDays = ((int)hm.get("SevenDays"))

    Random r = new Random()
    int x = (int) r.nextInt(Constants.PERCENTAGE)

    // case percentage for five days
    if (x < fiveDays) {


      returnValue = 5

      // case not for five days
    } else  {

      // case percentage for six days
      if (x >= fiveDays & x < (fiveDays + sixDays)) {


        returnValue = 6

      } else  {

        // case percentage for four days
        if (x >= (fiveDays + sixDays) & x < (fiveDays + sixDays + fourDays)) {


          returnValue = 4

        } else  {

          // case percentage for three days
          if (x >= (fiveDays + sixDays + fourDays) & x < (fiveDays + sixDays + fourDays + threeDays)) {


            returnValue = 3

          } else  {


            // case percentage for two days
            if (x >= (fiveDays + sixDays + fourDays + threeDays) & x < (fiveDays + sixDays + fourDays + threeDays+twoDays)) {


              returnValue = 2

            } else  {

              // case percentage for seven day
              if (x >= (fiveDays + sixDays + fourDays + threeDays+twoDays) & x < (fiveDays + sixDays + fourDays + threeDays+twoDays+sevenDays)) {


                returnValue  = 7

                // case percentage for one day
              } else  {

                // This is a task.
                returnValue = 1

              }

            }
          }
        }
      }
    }

    // Return the results.
    return returnValue

  }

  @ Override
  void showStatus() {


    // Printing the base variable
    System.out.println("Name = " + name)

    // Printing Sickness variables
    System.out.println("Sickness Days = ")
    ListIterator iter = sicknessVector.listIterator();

    while (iter.hasNext()) {

      System.out.println(iter.next());

    }

    // Printing Leisure variables
    System.out.println("Leisure Days of Week = ");
    iter = leisureVector.listIterator();

    while (iter.hasNext()) {

      System.out.println(iter.next());

    }

    System.out.println("Leisure Duration = " + leisureDuration);

    // Printing working variables
    System.out.println("Working Days = ");
    iter = workingDays.listIterator();

    while (iter.hasNext()) {

      System.out.println(iter.next());

    }

    System.out.println("Working Duration = " + workingDuration);
    System.out.println("Working Starting Hour = " + workingStartHour)

    // Printing vacation variables
    System.out.println("Vacation Duration = " + vacationDuration);
    System.out.println("Vacation Days = ");
    iter = vacationVector.listIterator();

    while (iter.hasNext()) {

      System.out.println(iter.next());

    }

    System.out.println("Public Vacation of Year = ");
    iter = publicVacationVector.listIterator();

    while (iter.hasNext()) {

      System.out.println(iter.next());

    }

    // Printing Weekly Schedule
    System.out.println("Weekly Routine : ")
    iter = weeklyRoutine.get(0).listIterator();

    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {

      System.out.println("Day " + (i))
      iter = weeklyRoutine.get(i).listIterator();

      for (int j =0;j < Constants.QUARTERS_OF_DAY;j++) {

        System.out.println("Quarter : " + (j+1) + " Status : " + iter.next())

      }

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
