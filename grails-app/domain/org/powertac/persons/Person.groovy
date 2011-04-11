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
import org.powertac.consumers.*

/**
 * A person domain instance represents a single person in its real life activities
 * The person is living in a house, it may work, it goes to the cinema, it is sick,
 * it goes on a vacation trip. In order to make the models as realistic as possible 
 * we have them to live their lives as part of a bigger community.
 *
 * @author Antonios Chrysopoulos
 * @version 1, 13/02/2011
 */

class Person {

  // autowire
  def randomSeedService

  /** the person's name in the community. Usually it includes the household he is living in or its type of person */
  String name

  /** the person's name at anytime. He may be sleeping, working, having fun etc. **/
  Status status

  /** vector of the public vacation days of the person's community, such as Christmas, Easter and so on **/
  Vector publicVacationVector = new Vector()

  /** a vector of the days that the person is sick and will stay in the house **/
  Vector sicknessVector = new Vector()

  /** the time each of the person's leisure activity takes **/
  int leisureDuration = 0

  /** a vector of the day's quarter's and the status of the person in each one of them   **/
  Vector dailyRoutine = new Vector()

  /** a vector containing the days of the week that the person has leisure time **/
  Vector leisureVector = new Vector()

  /** the weekly schedule and status of the person **/
  Vector weeklyRoutine = new Vector()

  /** Random Number Seed Creator **/
  Random randomGen 

  // static auditable = true

  static belongsTo = [memberOf:Household]


                      /** This function checks if the person is sleeping
                       * 
                       * @return
                       */
                      def isSleeping() {

    if (status == Status.Sleeping) {
      return true
    }
    else return false
  }

  /** This function checks if the person is at work
   * 
   * @return
   */
  def isAtWork() {

    if (status == Status.Working) {
      return true
    }
    else return false
  }

  /** This function checks if the person is doing a leisure activity
   * 
   * @return
   */
  def isLeisure() {

    if (status == Status.Leisure) {
      return true
    }
    else return false
  }

  /** This function checks if the person is on vacation
   * 
   * @return
   */
  def isVacation() {

    if (status == Status.Vacation) {
      return true
    }
    else return false
  }

  /** This function checks if the person is sick
   * 
   * @return
   */
  def isSick() {

    if (status == Status.Sick) {
      return true
    }
    else return false
  }


  /** This function fills out the leisure days' vector of the person 
   * by choosing randomly days of the week, while the amount of days is
   * different for each person type.
   * 
   * @param counter
   * @return
   */

  def createLeisureVector(int counter) {

    // Create auxiliary variable
    Vector v = new Vector()
    Random gen = ensureRandomSeed()

    //Loop for the amount of days
    for (int i = 0; i < counter; i++) {

      int day = gen.nextInt(Constants.DAYS_OF_WEEK)
      v.add(day)

    }

    // Sort the days we choose
    java.util.Collections.sort(v);

    return v

  }

  /** This function fills out the daily routine of the person, taking into
   * account the different variables and occupations, if he is sick or working etc.
   *  
   * @param day
   * @param vacationAbsence
   * @return
   */

  def fillDailyRoutine(int day, float vacationAbsence) {

    // Create auxiliary variable
    Vector v = new Vector(Constants.QUARTERS_OF_DAY)
    Status st
    Random gen = ensureRandomSeed()

    // Find the weekday
    int weekday = day % Constants.DAYS_OF_WEEK

    // Call for the function to fill the daily routine
    setDailyRoutine(new Vector())


    // case that this day the person is sick
    if (sicknessVector.contains(day)) {

      // We will the daily routine accordingly
      fillSick()

      // case not sick
    } else  {

      // Check if this day is a vacation for this person
      if (publicVacationVector.contains(day) || (this instanceof WorkingPerson && vacationVector.contains(day))) {

        // Checking if he is out of house for vacation or just relaxing at home
        if (gen.nextFloat() < vacationAbsence ) {

          // This is a loop.
          for (int i = 0;i < Constants.QUARTERS_OF_DAY; i++) {

            // Change status to vacation
            st = Status.Vacation
            dailyRoutine.add(st)

          }
          // case he stays home for vacation
        } else  {

          // Stays at home and does his leisure normally
          normalFill()
          addLeisure(weekday)
        }

        // case it is not a vacation day  
      } else  {

        // Fill day as if he doesn't work
        normalFill()

        // case he is working
        if (this instanceof WorkingPerson) {

          int index = workingDays.indexOf(weekday)

          // Case he works this day of the week
          if (index > -1) {

            // Fill the day with the working hours and leisure activity
            fillWork()
            addLeisureWorking(weekday)

            // case he doesn't work that day  
          } else  {

            // Fill just the leisure activity if there is any for that day
            addLeisure(weekday)

          }

          // case he is not working type of person  
        } else  {

          // Fill just the leisure activity if there is any for that day
          addLeisure(weekday)

        }

      }

    }

  }



  /** This function fills out the daily routine of the person, taking into
   * account the different variables and occupations, if he is sick or working etc.
   * @param mean
   * @param dev
   * @return
   */
  def createSicknessVector(float mean, float dev) {

    // Create auxiliary variables
    Random gen = ensureRandomSeed()
    int days = (int) (dev * gen.nextGaussian() + mean)
    Vector v = new Vector(days)
    Random r = new Random();

    // Loop through the amount of days that he will be sick
    for (int i = 0; i < days; i++) {

      // This is a task.
      int x = gen.nextInt(Constants.DAYS_OF_YEAR) + 1;
      ListIterator iter = v.listIterator();

      // If there are previous days in the list
      while (iter.hasNext()) {

        // This is a task.
        int temp = (int)iter.next()

        // Case we have already that day
        if (x == temp) {

          // Move sickness to the next day
          x = x + 1
          iter = v.listIterator();

        }

      }

      // Add day to the vector
      v.add(x)

    }

    // Sort the list of days
    java.util.Collections.sort(v);

    // Return the results.
    return v

  }

  /** This function fills out the daily routine with the leisure activity of the day,
   * if there is one for the person in question.
   * @param weekday
   * @return
   */
  def addLeisure(int weekday) {


    // Create auxiliary variables
    ListIterator iter = leisureVector.listIterator();
    Status st
    Random gen = ensureRandomSeed()

    // While there are more days of leisure in the list
    while (iter.hasNext()) {


      // case the day is the current day
      if (iter.next() == weekday) {

        // Find random hour to begin the leisure activity
        int start = Constants.START_OF_LEISURE  + gen.nextInt(Constants.LEISURE_WINDOW)

        // Add the leisure activity for the correct duration of time
        for (int i = start;i < start + leisureDuration;i++) {

          st = Status.Leisure
          dailyRoutine.set(i,st)

        }

      } 		

    }	

  }

  /** This function fills out the daily routine of the person as if 
   * he stays in the house all day long.
   * @return
   */
  def normalFill() {


    // Create auxiliary variables
    Status st

    // Filling the first period of sleeping
    for (int i = Constants.START_OF_SLEEPING_1;i < Constants.END_OF_SLEEPING_1;i++) {

      // This is a task.
      st = Status.Sleeping
      dailyRoutine.add(st)

    }

    // Filling the period of being at house
    for (int i = Constants.END_OF_SLEEPING_1;i < Constants.START_OF_SLEEPING_2;i++) {

      st = Status.Normal
      dailyRoutine.add(st)

    }


    // Filling the second period of sleeping
    for (int i = Constants.START_OF_SLEEPING_2;i < Constants.END_OF_SLEEPING_2;i++) {

      // This is a task.
      st = Status.Sleeping
      dailyRoutine.add(st)

    }


  }


  /** This function fills out the daily routine of the person that is sick
   * for the day.
   * @return
   */
  def fillSick() {


    // Create auxiliary variables
    Status st

    // Filling the first period of sleeping
    for (int i = Constants.START_OF_SLEEPING_1;i < Constants.END_OF_SLEEPING_1;i++) {

      st = Status.Sleeping
      dailyRoutine.add(st)

    }

    // Filling the period of being at house
    for (int i = Constants.END_OF_SLEEPING_1;i < Constants.START_OF_SLEEPING_2;i++) {

      st = Status.Sick
      dailyRoutine.add(st)
    }

    // Filling the second period of sleeping
    for (int i = Constants.START_OF_SLEEPING_2;i < Constants.END_OF_SLEEPING_2;i++) {

      st = Status.Sleeping
      dailyRoutine.add(st)

    }

  }


  /** This function fills out all the days of the person's week one by one.
   * 
   * @param vacationAbsence
   * @return
   */
  def fillWeeklyRoutine(float vacationAbsence) {


    // Create auxiliary variable
    Vector v = new Vector()

    // Fill out each day for the week
    for (int i = 0;i < Constants.DAYS_OF_WEEK;i++) {

      fillDailyRoutine(i,vacationAbsence)
      v.add(dailyRoutine)

    }

    return v

  }

  /** This is the function utilized to show the information regarding
   * the person in question, its variables values etc.
   * @return
   */
  def showInfo() {}

  /** At the end of each week the person models refresh their schedule. This way
   * we have a realistic and dynamic model, changing working hours, leisure activities
   * and so on.
   * @return
   */
  def refresh() {}

  /** Random Number Creator Initializer.
   * 
   * @return
   */
  private Random ensureRandomSeed ()
  {
    String requestClass
    if (randomGen == null) {
      if (this instanceof MostlyPresentPerson) requestClass = 'MostlyPresentPerson'
        if (this instanceof PeriodicPresentPerson) requestClass = 'PeriodicPresentPerson'
          if (this instanceof RandomlyAbsentPerson) requestClass = 'RandomlyAbsentPerson'
            long randomSeed = randomSeedService.nextSeed(requestClass, name, 'model')
            randomGen = new Random(randomSeed)
      //println(requestClass)
    }
    return randomGen
  }


  static constraints = {

    name()
    status()

  }

  static mapping = {
    sort "name"
  }

  String toString(){
    "${name}, ${Household} (${status})"
  }

}
