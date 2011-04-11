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

import org.powertac.common.*
import org.powertac.common.configurations.Constants

/**
 * The village domain class in this first version is a set of households that comprise
 * a small village that consumes agreggated energy by the appliances installed in each
 * household.Later on other types of building will be added.
 *
 * @author Antonios Chrysopoulos
 * @version 1, 13/02/2011
 */

class Village extends AbstractCustomer{

  /** the variable that contains the number of houses tha comprise the village.*/
  int numberOfHouses

  /** This is a vector containing aggregated each day's load from the appliances installed inside the households. **/
  Vector aggDailyLoad = new Vector()

  /** This is a vector containing the aggregated load from the appliances installed inside the households for all the week days.**/
  Vector aggWeeklyLoad = new Vector()

  /** This is an agreggated vector containing all weeks's load of all the households in hours. **/
  Vector aggWeeklyLoadInHours = new Vector()

  /** This is an agreggated vector containing each day's load of all the households in hours. **/
  Vector aggDailyLoadInHours = new Vector()

  //Vector aggWeeklyCostInHours = new Vector()

  static hasMany = [houses:Household]

  static belongsTo = [environment:Environment]

  /** This is the initialization function. It uses the variable values for the
   * configuration file to create the village with its households and then fill
   * them with persons and appliances.
   * @param hm
   * @param publicVacationVector
   */

  void initialize(HashMap hm, Vector publicVacationVector)
  {
    // Initializeing variables
    int houses = (int)hm.get("NumberOfHouses")
    setNumberOfHouses(houses)
    for (i in 0..houses-1) {
      System.out.println("Initializing House " + i)
      def hh = new Household()
      this.addToHouses(hh)
      hh.initialize("House" + i,hm, publicVacationVector)
    }
  }

  /** This function is used in order to fill each week day of the aggregated daily Load 
   * of the village households for each quarter of the hour.
   * 
   * @return
   */
  def fillAggWeeklyLoad() {
    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {
      setAggDailyLoad(fillAggDailyLoad(i))
      aggWeeklyLoad.add(aggDailyLoad)
      setAggDailyLoadInHours(fillAggDailyLoadInHours())
      aggWeeklyLoadInHours.add(aggDailyLoadInHours)
    }
  }

  /** This function is used in order to print the aggregated load of the village households.
   * 
   * @return
   */
  def showAggWeeklyLoad() {
    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {
      System.out.println("Day " + (i))
      ListIterator iter = aggWeeklyLoad.get(i).listIterator();
      for (int j = 0;j < Constants.QUARTERS_OF_DAY; j++) System.out.println("Quarter : " + (j+1) + " Load : " + iter.next())
    }
    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {
      System.out.println("Day " + (i))
      ListIterator iter = aggWeeklyLoadInHours.get(i).listIterator();
      for (int j = 0;j < Constants.HOURS_OF_DAY; j++) System.out.println("Hour : " + (j+1) + " Load : " + iter.next())
    }
  }

  @ Override
  void consumePower() {
    // Checking the time in the competition.
    int serial = ((timeService.currentTime.millis - timeService.start)/3600000) + 1

    int day = (int) (serial / Constants.QUARTERS_OF_DAY)+1
    int hour = (int) (serial % Constants.QUARTERS_OF_DAY)
    int weekday = (int) (day % Constants.DAYS_OF_WEEK)
    println(serial + " " + hour + " " + weekday)
    double ran = this.aggWeeklyLoadInHours.get(weekday).getAt(hour)

    // For each subscription
    subscriptions.each {
      println(ran);
      it.usePower(ran)
    }
  }

  /** This function is used in order to fill the aggregated daily Load of the village
   * households for each quarter of the hour.
   * @param weekday
   * @return
   */
  def fillAggDailyLoad(int weekday) {
    // Creating auxiliary variables
    Vector v = new Vector(Constants.QUARTERS_OF_DAY)
    int sum = 0
    for (int i = 0;i < Constants.QUARTERS_OF_DAY; i++) {
      sum = 0
      this.houses.each sum = sum + it.weeklyLoad.get(weekday).get(i)
      v.add(sum)
    }
    return v
  }

  /** This function is used in order to fill the daily Load of the household
   * for each hour.
   * @return
   */
  def fillAggDailyLoadInHours() {
    // Creating auxiliary variables
    Vector v = new Vector()
    int sum = 0
    for (int i = 0;i < Constants.HOURS_OF_DAY; i++) {
      sum = 0
      sum = aggDailyLoad.get(i*Constants.QUARTERS_OF_HOUR) + aggDailyLoad.get(i*Constants.QUARTERS_OF_HOUR +1) + aggDailyLoad.get(i*Constants.QUARTERS_OF_HOUR+2) + aggDailyLoad.get(i*Constants.QUARTERS_OF_HOUR+3)
      v.add(sum)
    }
    return v
  }

  /** At the end of each week the village households' models refresh their schedule. 
   * This way we have a realistic and dynamic model, changing function hours, consuming power
   * and so on.
   * @param hm
   * @return
   */
  def refresh(HashMap hm) {
    this.houses.each it.refresh(hm)
  }

  /** This function prints to the screen the daily load of the village's households for the
   * weekday at hand.
   * @param weekday
   * @return
   */
  def printDailyLoad(int weekday) {
    this.houses.each it.printDailyLoad(weekday)
  }

  /** This function represents the function that shows the status of all the households
   * in the village each moment in time.
   * @param weekday
   * @param quarter
   * @return
   */
  def step(int weekday, int quarter) {
    this.houses.each it.step(weekday,quarter)
  }

  static auditable = true

  public String toString() {
    return name
  }

  static constraints = {
  }
}
