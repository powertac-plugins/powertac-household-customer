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
                      def isSleeping() 
  {
    if (status == Status.Sleeping) return true
    else return false
  }

  /** This function checks if the person is at work
   * 
   * @return
   */
  def isAtWork() 
  {
    if (status == Status.Working) return true
    else return false
  }

  /** This function checks if the person is doing a leisure activity
   * 
   * @return
   */
  def isLeisure() 
  {
    if (status == Status.Leisure) return true
    else return false
  }

  /** This function checks if the person is on vacation
   * 
   * @return
   */
  def isVacation() 
  {
    if (status == Status.Vacation) return true
    else return false
  }

  /** This function checks if the person is sick
   * 
   * @return
   */
  def isSick() 
  {
    if (status == Status.Sick) return true
    else return false
  }


  /** This function fills out the leisure days' vector of the person 
   * by choosing randomly days of the week, while the amount of days is
   * different for each person type.
   * 
   * @param counter
   * @return
   */

  def createLeisureVector(int counter) 
  {
    // Create auxiliary variable
    Vector v = new Vector()
    Random gen = ensureRandomSeed()
    //Loop for the amount of days
    for (int i = 0; i < counter; i++) {
      int day = gen.nextInt(Constants.DAYS_OF_WEEK)
      v.add(day)
    }
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

  def fillDailyRoutine(int day, float vacationAbsence) 
  {
    // Create auxiliary variable
    Vector v = new Vector(Constants.QUARTERS_OF_DAY)
    Status st
    Random gen = ensureRandomSeed()

    int weekday = day % Constants.DAYS_OF_WEEK
    setDailyRoutine(new Vector())
    if (sicknessVector.contains(day)) {
      fillSick()
    } else  {
      if (publicVacationVector.contains(day) || (this instanceof WorkingPerson && vacationVector.contains(day))) {
        if (gen.nextFloat() < vacationAbsence ) {
          for (int i = 0;i < Constants.QUARTERS_OF_DAY; i++) {
            st = Status.Vacation
            dailyRoutine.add(st)
          }
        } else  {
          normalFill()
          addLeisure(weekday)
        }
      } else  {
        normalFill()
        if (this instanceof WorkingPerson) {
          int index = workingDays.indexOf(weekday)
          if (index > -1) {
            fillWork()
            addLeisureWorking(weekday)
          } else  {
            addLeisure(weekday)
          }
        } else  {
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
  def createSicknessVector(float mean, float dev) 
  {
    // Create auxiliary variables
    Random gen = ensureRandomSeed()
    int days = (int) (dev * gen.nextGaussian() + mean)
    Vector v = new Vector(days)
    Random r = new Random();
    for (int i = 0; i < days; i++) {
      int x = gen.nextInt(Constants.DAYS_OF_YEAR) + 1;
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
  }

  /** This function fills out the daily routine with the leisure activity of the day,
   * if there is one for the person in question.
   * @param weekday
   * @return
   */
  def addLeisure(int weekday) 
  {
    // Create auxiliary variables
    ListIterator iter = leisureVector.listIterator();
    Status st
    Random gen = ensureRandomSeed()
    while (iter.hasNext()) {
      if (iter.next() == weekday) {
        int start = Constants.START_OF_LEISURE  + gen.nextInt(Constants.LEISURE_WINDOW)
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
  def normalFill() 
  {
    Status st
    for (int i = Constants.START_OF_SLEEPING_1;i < Constants.END_OF_SLEEPING_1;i++) {
      st = Status.Sleeping
      dailyRoutine.add(st)
    }
    for (int i = Constants.END_OF_SLEEPING_1;i < Constants.START_OF_SLEEPING_2;i++) {
      st = Status.Normal
      dailyRoutine.add(st)
    }
    for (int i = Constants.START_OF_SLEEPING_2;i < Constants.END_OF_SLEEPING_2;i++) {
      st = Status.Sleeping
      dailyRoutine.add(st)
    }		
  }


  /** This function fills out the daily routine of the person that is sick
   * for the day.
   * @return
   */
  def fillSick() 
  {
    Status st
    for (int i = Constants.START_OF_SLEEPING_1;i < Constants.END_OF_SLEEPING_1;i++) {
      st = Status.Sleeping
      dailyRoutine.add(st)
    }
    for (int i = Constants.END_OF_SLEEPING_1;i < Constants.START_OF_SLEEPING_2;i++) {
      st = Status.Sick
      dailyRoutine.add(st)
    }
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
  def fillWeeklyRoutine(float vacationAbsence) 
  {
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
