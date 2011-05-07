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

import java.util.Random

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
  //autowire
  def randomSeedService

  /** This is a vector containing aggregated each day's load from the appliances installed inside the households. **/
  Vector aggDailyLoad = new Vector()

  /** This is a vector containing the aggregated load from the appliances installed inside the households for all the week days.**/
  Vector aggWeeklyLoadNS = new Vector()
  Vector aggWeeklyLoadRaS = new Vector()
  Vector aggWeeklyLoadReS = new Vector()
  Vector aggWeeklyLoadSS = new Vector()

  /** This is an agreggated vector containing all weeks's load of all the households in hours. **/
  Vector aggWeeklyLoadInHoursNS = new Vector()
  Vector aggWeeklyLoadInHoursRaS = new Vector()
  Vector aggWeeklyLoadInHoursReS = new Vector()
  Vector aggWeeklyLoadInHoursSS = new Vector()

  /** This is an agreggated vector containing each day's load of all the households in hours. **/
  Vector aggDailyLoadInHours = new Vector()

  /** Random Number Seed Creator **/
  Random randomGen

  /** This hashmap variable is utilized to show which portion of the population is under which subscription **/
  HashMap subscriptionMap = new HashMap()

  /** There are 4 kinds of Household Customers in each Village: Not Shifting, Randomly Shifting, Regularly Shifting and Smart Shifting **/
  static hasMany = [housesNS:Household, housesRaS:Household, housesReS:Household, housesSS:Household ]

  /** This is the initialization function. It uses the variable values for the
   * configuration file to create the village with its households and then fill
   * them with persons and appliances.
   * @param hm
   * @param publicVacationVector
   */

  void initialize(HashMap hm) {
    // Initializing variables
    int nshouses = (int)hm.get("NotShiftingCustomers")
    int rashouses = (int)hm.get("RandomlyShiftingCustomers")
    int reshouses = (int)hm.get("RegularlyShiftingCustomers")
    int sshouses = (int)hm.get("SmartShiftingCustomers")
    int days = (int)hm.get("PublicVacationDuration")

    customerInfo.population = nshouses + rashouses + reshouses + sshouses

    def publicVacationVector = createPublicVacationVector(days)

    for (i in 0..nshouses-1) {
      log.info "Initializing NSHouse ${i} "
      def hh = new Household()
      this.addToHousesNS(hh)
      hh.initialize("NSHouse" + i,hm, publicVacationVector)
    }

    for (i in 0..rashouses-1) {
      log.info "Initializing RaSHouse ${i} "
      def hh = new Household()
      this.addToHousesRaS(hh)
      hh.initialize("RaSHouse" + i,hm, publicVacationVector)
    }
    for (i in 0..reshouses-1) {
      log.info "Initializing ReSHouse ${i} "
      def hh = new Household()
      this.addToHousesReS(hh)
      hh.initialize("ReSHouse" + i,hm, publicVacationVector)
    }
    for (i in 0..sshouses-1) {
      log.info "Initializing SSHouse ${i} "
      def hh = new Household()
      this.addToHousesSS(hh)
      hh.initialize("SSHouse" + i,hm, publicVacationVector)
    }

    fillAggWeeklyLoad(aggWeeklyLoadNS, aggWeeklyLoadInHoursNS, "NotShifting")
    fillAggWeeklyLoad(aggWeeklyLoadRaS, aggWeeklyLoadInHoursRaS, "RandomlyShifting")
    fillAggWeeklyLoad(aggWeeklyLoadReS, aggWeeklyLoadInHoursReS, "RegularlyShifting")
    fillAggWeeklyLoad(aggWeeklyLoadSS, aggWeeklyLoadInHoursSS, "SmartShifting")
    this.save()
  }

  /** This function is used in order to fill each week day of the aggregated daily Load 
   * of the village households for each quarter of the hour.
   * 
   * @return
   */
  def fillAggWeeklyLoad(Vector aggWeeklyLoad, Vector aggWeeklyLoadInHours, String portion) {
    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {
      setAggDailyLoad(fillAggDailyLoad(i, portion))
      aggWeeklyLoad.add(aggDailyLoad)
      setAggDailyLoadInHours(fillAggDailyLoadInHours())
      aggWeeklyLoadInHours.add(aggDailyLoadInHours)
    }
  }

  /** This function is used in order to print the aggregated load of the village households.
   * 
   * @return
   */
  def showAggWeeklyLoad(String portion) {

    def aggWeeklyLoad, aggWeeklyLoadInHours

    if (portion.equals("NotShifting")){
      aggWeeklyLoad = aggWeeklyLoadNS
      aggWeeklyLoadInHours = aggWeeklyLoadInHoursNS
    }
    else if (portion.equals("RandomlyShifting")){
      aggWeeklyLoad = aggWeeklyLoadRaS
      aggWeeklyLoadInHours = aggWeeklyLoadInHoursRaS
    }
    else if (portion.equals("RegularlyShifting")){
      aggWeeklyLoad = aggWeeklyLoadReS
      aggWeeklyLoadInHours = aggWeeklyLoadInHoursReS
    }
    else {
      aggWeeklyLoad = aggWeeklyLoadSS
      aggWeeklyLoadInHours = aggWeeklyLoadInHoursSS
    }

    log.info "Portion ${portion} Weekly Aggregated Load "

    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {
      log.info "Day ${i} "
      ListIterator iter = aggWeeklyLoad.get(i).listIterator();
      for (int j = 0;j < Constants.QUARTERS_OF_DAY; j++) log.info "Quarter : ${j+1}  Load : ${iter.next()} "
    }

    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {
      log.info "Day ${i} "
      ListIterator iter = aggWeeklyLoadInHours.get(i).listIterator();
      for (int j = 0;j < Constants.HOURS_OF_DAY; j++) log.info "Hour : ${j+1}  Load : ${iter.next()} "
    }
  }

  @ Override
  void consumePower() {
    // Checking the time in the competition.
    int serial = ((timeService.currentTime.millis - timeService.base) / TimeService.HOUR) + 1

    int day = (int) (serial / Constants.HOURS_OF_DAY)
    int hour = (int) (serial % Constants.HOURS_OF_DAY)
    int weekday = (int) (day % Constants.DAYS_OF_WEEK)
    log.info " Serial : ${serial} Hour: ${hour} Weekday: ${weekday} "
    //BigDecimal ran = aggWeeklyLoadInHoursNS.get(weekday).getAt(hour) + aggWeeklyLoadInHoursRaS.get(weekday).getAt(hour) + aggWeeklyLoadInHoursReS.get(weekday).getAt(hour) + aggWeeklyLoadInHoursSS.get(weekday).getAt(hour)
    BigDecimal ran = (aggWeeklyLoadInHoursNS.get(weekday).getAt(hour) + aggWeeklyLoadInHoursRaS.get(weekday).getAt(hour) + aggWeeklyLoadInHoursReS.get(weekday).getAt(hour) + aggWeeklyLoadInHoursSS.get(weekday).getAt(hour)) / Constants.PERCENTAGE

    // For each subscription
    subscriptions.each { sub ->
      log.info " Consumption Load: ${ran} "
      sub.usePower(ran)
    }
  }

  /** This function is used in order to fill the aggregated daily Load of the village
   * households for each quarter of the hour.
   * @param weekday
   * @return
   */
  def fillAggDailyLoad(int weekday, String portion) {

    def houses

    if (portion.equals("NotShifting")){
      houses = this.housesNS
    }
    else if (portion.equals("RandomlyShifting")){
      houses = this.housesRaS
    }
    else if (portion.equals("RegularlyNotShifting")){
      houses = this.housesReS
    }
    else {
      houses = this.housesSS
    }

    Vector v = new Vector(Constants.QUARTERS_OF_DAY)
    int sum = 0
    for (int i = 0;i < Constants.QUARTERS_OF_DAY; i++) {
      sum = 0
      houses.each {
        sum = sum + it.weeklyLoad.get(weekday).get(i)
      }
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
    this.housesNS.each { it.refresh(hm) }
    this.housesRaS.each { it.refresh(hm) }
    this.housesReS.each { it.refresh(hm) }
    this.housesSS.each { it.refresh(hm) }
    this.save()
  }

  /** This function prints to the screen the daily load of the village's households for the
   * weekday at hand.
   * @param weekday
   * @return
   */
  def printDailyLoad(int weekday, String portion) {

    def houses

    if (portion.equals("NotShifting")){
      houses = this.housesNS
    }
    else if (portion.equals("RandomlyShifting")){
      houses = this.housesRaS
    }
    else if (portion.equals("RegularlyNotShifting")){
      houses = this.housesReS
    }
    else {
      houses = this.housesSS
    }

    this.houses.each { it.printDailyLoad(weekday) }
  }

  /** This function represents the function that shows the status of all the households
   * in the village each moment in time.
   * @param weekday
   * @param quarter
   * @return
   */
  def stepStatus(int weekday, int quarter) {
    this.housesNS.each {
      it.stepStatus(weekday,quarter)
    }
    this.housesRaS.each {
      it.stepStatus(weekday,quarter)
    }
    this.housesReS.each {
      it.stepStatus(weekday,quarter)
    }
    this.housesSS.each {
      it.stepStatus(weekday,quarter)
    }
  }

  /** This function is creating a certain number of random days that will be
   * public vacation for the people living in the environment.
   *
   * @param days
   * @return
   */
  def createPublicVacationVector(int days) {
    // Creating auxiliary variables
    Vector v = new Vector(days)
    Random gen = ensureRandomSeed()
    for (int i = 0; i < days; i++) {
      int x = gen.nextInt(Constants.DAYS_OF_YEAR)
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


  private Random ensureRandomSeed () {
    String requestClass
    if (randomGen == null) {
      long randomSeed = randomSeedService.nextSeed('Environment', 'VillageEnvironment', 'model')
      randomGen = new Random(randomSeed)
    }
    return randomGen
  }

  static auditable = true

  public String toString() {
    customerInfo.getName()
  }

  static constraints = {
  }
}
