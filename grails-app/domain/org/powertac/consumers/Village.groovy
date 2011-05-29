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

import org.joda.time.Instant
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

  def villageConsumersService

  /** This is a vector containing aggregated each day's load from the appliances installed inside the households. **/
  Vector aggDailyLoad = new Vector()

  /** This is an agreggated vector containing each day's load of all the households in hours. **/
  Vector aggDailyLoadInHours = new Vector()

  List daysList = new List[Constants.RANDOM_DAYS_NUMBER]

  /** This hashmap variable is utilized to show which portion of the population is under which subscription **/
  // HashMap subscriptionMap = new HashMap()

  /** This is the initialization function. It uses the variable values for the
   * configuration file to create the village with its households and then fill
   * them with persons and appliances.
   * @param hm
   * @param publicVacationVector
   */

  void initialize(ConfigObject conf, Random gen) {
    // Initializing variables
    
    int nshouses = conf.household.houses.NotShiftingCustomers
    int rashouses = conf.household.houses.RandomlyShiftingCustomers
    int reshouses = conf.household.houses.RegularlyShiftingCustomers
    int sshouses = conf.household.houses.SmartShiftingCustomers
    int days = conf.household.general.PublicVacationDuration

    customerInfo.population = nshouses + rashouses + reshouses + sshouses
    villageConsumersService.createHouseholdsMap(this, 4, nshouses)
    villageConsumersService.createConsumptionsMap(this,4)
    createCostEstimationDaysList(Constants.RANDOM_DAYS_NUMBER,gen)

    def publicVacationVector = createPublicVacationVector(days, gen)

    for (i in 0..nshouses-1) {
      log.info "Initializing ${this.customerInfo.name} NSHouse ${i} "
      def hh = new Household()
      hh.initialize(this.customerInfo.name+" NSHouse" + i,conf, publicVacationVector, gen)
      villageConsumersService.setHousehold(this, 0, i, hh)
    }

    for (i in 0..rashouses-1) {
      log.info "Initializing ${this.customerInfo.name} RaSHouse ${i} "
      def hh = new Household()
      hh.initialize(this.customerInfo.name+" RaSHouse" + i,conf, publicVacationVector, gen)
      villageConsumersService.setHousehold(this, 1, i, hh)
    }
    for (i in 0..reshouses-1) {
      log.info "Initializing ${this.customerInfo.name} ReSHouse ${i} "
      def hh = new Household()
      hh.initialize(this.customerInfo.name+" ReSHouse" + i,conf, publicVacationVector, gen)
      villageConsumersService.setHousehold(this, 2, i, hh)
    }
    for (i in 0..sshouses-1) {
      log.info "Initializing ${this.customerInfo.name} SSHouse ${i} "
      def hh = new Household()
      hh.initialize(this.customerInfo.name+" SSHouse" + i,conf, publicVacationVector, gen)
      villageConsumersService.setHousehold(this, 3, i, hh)
    }

    fillAggWeeklyLoad("NotShifting")
    fillAggWeeklyLoad("RandomlyShifting")
    fillAggWeeklyLoad("RegularlyShifting")
    fillAggWeeklyLoad("SmartShifting")

  }

  /** This function is used in order to fill each week day of the aggregated daily Load 
   * of the village households for each quarter of the hour.
   * 
   * @return
   */
  def fillAggWeeklyLoad(String portion) {
    for (int i = 0; i < Constants.DAYS_OF_WEEK * 9;i++) {
      setAggDailyLoad(fillAggDailyLoad(i, portion))
      setAggDailyLoadInHours(fillAggDailyLoadInHours(i,portion))
    }
  }

  /** This function is used in order to print the aggregated load of the village households.
   * 
   * @return
   */
  def showAggWeeklyLoad(String portion) {

    def houses

    if (portion.equals("NotShifting")){
      houses = villageConsumersService.getConsumptions(this,0)
    }
    else if (portion.equals("RandomlyShifting")){
      houses = villageConsumersService.getConsumptions(this,1)
    }
    else if (portion.equals("RegularlyShifting")){
      houses = villageConsumersService.getConsumptions(this,2)
    }
    else {
      houses = villageConsumersService.getConsumptions(this,3)
    }

    log.info "Portion ${portion} Weekly Aggregated Load "

    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {
      log.info "Day ${i} "
      for (int j = 0;j < Constants.HOURS_OF_DAY; j++) log.info "Hour : ${j+1}  Load : ${houses[i][j]} "
    }
  }

  @ Override
  double getConsumptionByTimeslot(int serial) {
    
    int day = (int) (serial / Constants.HOURS_OF_DAY)
    int hour = (int) (serial % Constants.HOURS_OF_DAY)
    double ran = 0,summary = 0
    
    log.info " Serial : ${serial} Day: ${day} Hour: ${hour} "

    for (int i=0;i < 4;i++){
      ran = ran + villageConsumersService.getConsumptions(this,i)[day][hour]
    }
    ran = ran / Constants.PERCENTAGE
    return ran
   } 

  /** This function is used in order to fill the aggregated daily Load of the village
   * households for each quarter of the hour.
   * @param weekday
   * @return
   */
  def fillAggDailyLoad(int day, String portion) {

    def houses

    if (portion.equals("NotShifting")){
      houses = villageConsumersService.getHouseholds(this,0)
    }
    else if (portion.equals("RandomlyShifting")){
      houses = villageConsumersService.getHouseholds(this,1)
    }
    else if (portion.equals("RegularlyShifting")){
      houses = villageConsumersService.getHouseholds(this,2)
    }
    else {
      houses = villageConsumersService.getHouseholds(this,3)
    }

    Vector v = new Vector(Constants.QUARTERS_OF_DAY)
    int sum = 0
    for (int i = 0;i < Constants.QUARTERS_OF_DAY; i++) {
      sum = 0
      houses.each {
        sum = sum + it.weeklyLoad.get(day).get(i)
      }
      v.add(sum)
    }
    return v
  }

  /** This function is used in order to fill the daily Load of the household
   * for each hour.
   * @return
   */
  def fillAggDailyLoadInHours(int day, String portion) {
    // Creating auxiliary variables
    def houses

    if (portion.equals("NotShifting")){
      houses = 0
    }
    else if (portion.equals("RandomlyShifting")){
      houses = 1
    }
    else if (portion.equals("RegularlyShifting")){
      houses = 2
    }
    else {
      houses = 3
    }

    int sum = 0
    for (int i = 0;i < Constants.HOURS_OF_DAY; i++) {
      sum = 0
      sum = aggDailyLoad.get(i*Constants.QUARTERS_OF_HOUR) + aggDailyLoad.get(i*Constants.QUARTERS_OF_HOUR +1) + aggDailyLoad.get(i*Constants.QUARTERS_OF_HOUR+2) + aggDailyLoad.get(i*Constants.QUARTERS_OF_HOUR+3)
      villageConsumersService.setConsumption(this,houses,day,i,sum)
    }
  }

  /** At the end of each week the village households' models refresh their schedule. 
   * This way we have a realistic and dynamic model, changing function hours, consuming power
   * and so on.
   * @param hm
   * @return
   */
  def refresh(ConfigObject conf, Random gen) {
    for (int i=0; i < villageConsumersService.households.size();i++){
      for (int j=0; j < villageConsumersService.getHouseholds(this,i).size();j++){
        villageConsumersService.getHouseholds(this,i)[j].refresh(conf,gen)
      }
    }

    villageConsumersService.consumptions.remove(this.customerInfo.name)
    villageConsumersService.createConsumptionsMap(this,4)

    fillAggWeeklyLoad("NotShifting")
    fillAggWeeklyLoad("RandomlyShifting")
    fillAggWeeklyLoad("RegularlyShifting")
    fillAggWeeklyLoad("SmartShifting")

  }

  double estimateVariableTariffPayment(Tariff tariff){


    int serial = ((timeService.currentTime.millis - timeService.base) / TimeService.HOUR)
    Instant base = timeService.currentTime - serial*TimeService.HOUR
    int daylimit = (int) (serial / 24) + 1 // this will be changed to one or more random numbers

    float finalCostSummary = 0

    daysList.each { day ->

      if (day < daylimit) day = (int) (day + (daylimit / Constants.RANDOM_DAYS_NUMBER))
      Instant now = base + day * TimeService.DAY
      float costSummary = 0
      float summary = 0, cumulativeSummary = 0

      for (int hour=0;hour < Constants.HOURS_OF_DAY;hour++){
        for (int j=0;j < 4;j++){
          summary = summary + villageConsumersService.getConsumptions(this,j)[day][hour]
        }
        summary = summary / Constants.PERCENTAGE
        cumulativeSummary += summary
        costSummary += tariff.getUsageCharge(now,summary,cumulativeSummary)
        now = now + TimeService.HOUR
      }
      log.info "Variable Cost Summary: ${finalCostSummary}"
      finalCostSummary += costSummary
    }
    return finalCostSummary / Constants.RANDOM_DAYS_NUMBER
  }


  /** This function prints to the screen the daily load of the village's households for the
   * weekday at hand.
   * @param weekday
   * @return
   */
  def printDailyLoad(int day, String portion) {

    def houses

    if (portion.equals("NotShifting")){
      houses = 0
    }
    else if (portion.equals("RandomlyShifting")){
      houses = 1
    }
    else if (portion.equals("RegularlyShifting")){
      houses = 2
    }
    else {
      houses = 3
    }

    for (int i=0; i < villageConsumersService.households.size();i++){
      for (int j=0; j < villageConsumersService.getHouseholds(this,houses).size();j++){
        villageConsumersService.getHouseholds(this,houses)[j].printDailyLoad(day)
      }
    }

  }

  /** This function represents the function that shows the status of all the households
   * in the village each moment in time.
   * @param weekday
   * @param quarter
   * @return
   */
  def stepStatus(int day, int quarter) {
    for (int i=0; i < villageConsumersService.households.size();i++){
      for (int j=0; j < villageConsumersService.getHouseholds(this,i).size();j++){
        villageConsumersService.getHouseholds(this,i)[j].stepStatus(day,quarter)
      }
    }
  }

  /** This function is creating a certain number of random days that will be
   * public vacation for the people living in the environment.
   *
   * @param days
   * @return
   */
  def createPublicVacationVector(int days, Random gen) {
    // Creating auxiliary variables
    Vector v = new Vector(days)

    for (int i = 0; i < days; i++) {
      int x = gen.nextInt(Constants.DAYS_OF_COMPETITION)
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

  def createCostEstimationDaysList(int days, Random gen) {

    for (int i = 0; i < days; i++) {
      int x = gen.nextInt(Constants.DAYS_OF_COMPETITION)
      ListIterator iter = daysList.listIterator();
      while (iter.hasNext()) {
        int temp = (int)iter.next()
        if (x == temp) {
          x = x + 1
          iter = daysList.listIterator();
        }
      }
      daysList[i] = x
    }
    java.util.Collections.sort(daysList)

  }


  static auditable = true

  public String toString() {
    customerInfo.getName()
  }

  static constraints = {
  }
}
