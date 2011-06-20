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

import groovy.util.ConfigObject

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

  /** This is the service that is utilized to store the Mappings of each village.*/
  def villageConsumersService

  /** Number of customer types in the village.*/
  int types = 4

  /** This is a vector containing aggregated each day's base load from the appliances installed inside the households. **/
  Vector aggDailyBaseLoad = new Vector()

  /** This is a vector containing aggregated each day's controllable load from the appliances installed inside the households. **/
  Vector aggDailyControllableLoad = new Vector()

  /** This is an agreggated vector containing each day's base load of all the households in hours. **/
  Vector aggDailyBaseLoadInHours = new Vector()

  /** This is an agreggated vector containing each day's controllable load of all the households in hours. **/
  Vector aggDailyControllableLoadInHours = new Vector()


  //static hasMany = [houses:Household]

  /** This hashmap variable is utilized to show which portion of the population is under which subscription **/
  // HashMap subscriptionMap = new HashMap()

  /** This is the initialization function. It uses the variable values for the
   * configuration file to create the village with its households and then fill
   * them with persons and appliances.
   * @param conf
   * @param gen
   */
  void initialize(ConfigObject conf, Random gen) {
    // Initializing variables

    int nshouses = conf.household.houses.NotShiftingCustomers
    int rashouses = conf.household.houses.RandomlyShiftingCustomers
    int reshouses = conf.household.houses.RegularlyShiftingCustomers
    int sshouses = conf.household.houses.SmartShiftingCustomers
    int days = conf.household.general.PublicVacationDuration

    customerInfo.population = nshouses + rashouses + reshouses + sshouses
    villageConsumersService.createHouseholdsMap(this, types, nshouses)
    villageConsumersService.createBaseConsumptionsMap(this,types)
    villageConsumersService.createControllableConsumptionsMap(this,types)
    villageConsumersService.createBootstrapConsumptionsMap(this)
    villageConsumersService.createDaysMap(this)
    createCostEstimationDaysList(Constants.RANDOM_DAYS_NUMBER,gen)

    def publicVacationVector = createPublicVacationVector(days, gen)

    for (i in 0..nshouses-1) {
      log.info "Initializing ${this.customerInfo.name} NSHouse ${i} "
      def hh = new Household()
      hh.initialize(this.customerInfo.name+" NSHouse" + i,conf, publicVacationVector, gen)
      villageConsumersService.setHousehold(this, Constants.NOT_SHIFTING_TYPE, i, hh)
      //addToHouses(hh)
    }

    for (i in 0..rashouses-1) {
      log.info "Initializing ${this.customerInfo.name} RaSHouse ${i} "
      def hh = new Household()
      hh.initialize(this.customerInfo.name+" RaSHouse" + i,conf, publicVacationVector, gen)
      villageConsumersService.setHousehold(this, Constants.RANDOM_SHIFTING_TYPE, i, hh)
      //addToHouses(hh)
    }

    for (i in 0..reshouses-1) {
      log.info "Initializing ${this.customerInfo.name} ReSHouse ${i} "
      def hh = new Household()
      hh.initialize(this.customerInfo.name+" ReSHouse" + i,conf, publicVacationVector, gen)
      villageConsumersService.setHousehold(this, Constants.REGULAR_SHIFTING_TYPE, i, hh)
      //addToHouses(hh)
    }

    for (i in 0..sshouses-1) {
      log.info "Initializing ${this.customerInfo.name} SSHouse ${i} "
      def hh = new Household()
      hh.initialize(this.customerInfo.name+" SSHouse" + i,conf, publicVacationVector, gen)
      villageConsumersService.setHousehold(this, Constants.SMART_SHIFTING_TYPE, i, hh)
      //addToHouses(hh)
    }
  }

  void createBootstrapData(Random gen){

    fillAggWeeklyBootstrapLoad("NotShifting")
    fillAggWeeklyBootstrapLoad("RandomlyShifting")
    fillAggWeeklyBootstrapLoad("RegularlyShifting")
    fillAggWeeklyBootstrapLoad("SmartShifting")

    //bootstrapSchedule(gen)
    villageConsumersService.setBootstrapConsumptions(this)
    for (int day = 0;day < Constants.DAYS_OF_BOOTSTRAP;day++) log.debug("Day ${day}: Bootstrap Load : ${villageConsumersService.getBootstrapConsumptions(this)[day].toString()}")
  }

  void createActualData(ConfigObject conf, Random gen){

    villageConsumersService.baseConsumptions.remove(this.customerInfo.name)
    villageConsumersService.createBaseConsumptionsMap(this,types)
    villageConsumersService.controllableConsumptions.remove(this.customerInfo.name)
    villageConsumersService.createControllableConsumptionsMap(this,types)

    def houses = villageConsumersService.getHouseholds(this)

    houses.each { house -> house.createActualData(conf,gen) }

    fillAggWeeklyLoad("NotShifting")
    fillAggWeeklyLoad("RandomlyShifting")
    fillAggWeeklyLoad("RegularlyShifting")
    fillAggWeeklyLoad("SmartShifting")

  }

  /** This function is used in order to fill each week day of the aggregated daily Load 
   * of the village households for each quarter of the hour.
   * @param portion
   * @return
   */
  def fillAggWeeklyLoad(String portion) {
    for (int i = 0; i < Constants.DAYS_OF_WEEK * Constants.WEEKS_OF_COMPETITION;i++) {
      setAggDailyBaseLoad(fillAggDailyBaseLoad(i, portion))
      setAggDailyControllableLoad(fillAggDailyControllableLoad(i, portion))
      setAggDailyBaseLoadInHours(fillAggDailyBaseLoadInHours(i,portion))
      setAggDailyControllableLoadInHours(fillAggDailyControllableLoadInHours(i,portion))
    }
  }

  /** This function is used in order to fill the two week bootstrap data of the aggregated daily Load
   * of the village households for each quarter of the hour.
   * @param portion
   * @return
   */
  def fillAggWeeklyBootstrapLoad(String portion) {
    for (int i = 0; i < Constants.DAYS_OF_WEEK * Constants.WEEKS_OF_BOOTSTRAP;i++) {
      setAggDailyBaseLoad(fillAggDailyBaseLoad(i, portion))
      setAggDailyControllableLoad(fillAggDailyControllableLoad(i, portion))
      setAggDailyBaseLoadInHours(fillAggDailyBaseLoadInHours(i,portion))
      setAggDailyControllableLoadInHours(fillAggDailyControllableLoadInHours(i,portion))
    }
  }


  /** This function is used in order to print the aggregated load of the village households.
   * @param portion
   * @return
   */
  def showAggWeeklyLoad(String portion) {

    def housesBase
    def housesControllable

    if (portion.equals("NotShifting")){
      housesBase = villageConsumersService.getBaseConsumptions(this,Constants.NOT_SHIFTING_TYPE)
      housesControllable = villageConsumersService.getControllableConsumptions(this,Constants.NOT_SHIFTING_TYPE)
    }
    else if (portion.equals("RandomlyShifting")){
      housesBase = villageConsumersService.getBaseConsumptions(this,Constants.RANDOM_SHIFTING_TYPE)
      housesControllable = villageConsumersService.getControllableConsumptions(this,Constants.RANDOM_SHIFTING_TYPE)
    }
    else if (portion.equals("RegularlyShifting")){
      housesBase = villageConsumersService.getBaseConsumptions(this,Constants.REGULAR_SHIFTING_TYPE)
      housesControllable = villageConsumersService.getControllableConsumptions(this,Constants.REGULAR_SHIFTING_TYPE)
    }
    else {
      housesBase = villageConsumersService.getBaseConsumptions(this,Constants.SMART_SHIFTING_TYPE)
      housesControllable = villageConsumersService.getControllableConsumptions(this,Constants.SMART_SHIFTING_TYPE)
    }

    log.info "Portion ${portion} Weekly Aggregated Load "

    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {
      log.info "Day ${i} "
      for (int j = 0;j < Constants.HOURS_OF_DAY; j++) log.info "Hour : ${j+1}  Base Load : ${housesBase[i][j]}   Controllable Load: ${housesControllable[i][j]} "
    }
  }

  @ Override
  double getConsumptionByTimeslot(int serial) {

    int day = (int) (serial / Constants.HOURS_OF_DAY)
    int hour = (int) (serial % Constants.HOURS_OF_DAY)
    double ran = 0,summary = 0

    log.info " Serial : ${serial} Day: ${day} Hour: ${hour} "

    for (int i=0;i < types;i++){
      ran = ran + (villageConsumersService.getBaseConsumptions(this,i)[day][hour] + villageConsumersService.getControllableConsumptions(this,i)[day][hour])
    }
    ran = ran / Constants.PERCENTAGE
    return ran
  }

  /** This function is used in order to fill the aggregated daily Base Load of the village
   * households for each quarter of the hour.
   * @param day
   * @param portion
   * @return
   */
  def fillAggDailyBaseLoad(int day, String portion) {

    def houses

    if (portion.equals("NotShifting")){
      houses = villageConsumersService.getHouseholds(this,Constants.NOT_SHIFTING_TYPE)
    }
    else if (portion.equals("RandomlyShifting")){
      houses = villageConsumersService.getHouseholds(this,Constants.RANDOM_SHIFTING_TYPE)
    }
    else if (portion.equals("RegularlyShifting")){
      houses = villageConsumersService.getHouseholds(this,Constants.REGULAR_SHIFTING_TYPE)
    }
    else {
      houses = villageConsumersService.getHouseholds(this,Constants.SMART_SHIFTING_TYPE)
    }

    Vector v = new Vector(Constants.QUARTERS_OF_DAY)
    int sum = 0
    for (int i = 0;i < Constants.QUARTERS_OF_DAY; i++) {
      sum = 0
      houses.each {
        sum = sum + it.weeklyBaseLoad.get(day).get(i)
      }
      v.add(sum)
    }
    return v
  }

  /** This function is used in order to fill the aggregated daily Controllable Load of the village
   * households for each quarter of the hour.
   * @param day
   * @param portion
   * @return
   */
  def fillAggDailyControllableLoad(int day, String portion) {

    def houses

    if (portion.equals("NotShifting")){
      houses = villageConsumersService.getHouseholds(this,Constants.NOT_SHIFTING_TYPE)
    }
    else if (portion.equals("RandomlyShifting")){
      houses = villageConsumersService.getHouseholds(this,Constants.RANDOM_SHIFTING_TYPE)
    }
    else if (portion.equals("RegularlyShifting")){
      houses = villageConsumersService.getHouseholds(this,Constants.REGULAR_SHIFTING_TYPE)
    }
    else {
      houses = villageConsumersService.getHouseholds(this,Constants.SMART_SHIFTING_TYPE)
    }

    Vector v = new Vector(Constants.QUARTERS_OF_DAY)
    int sum = 0
    for (int i = 0;i < Constants.QUARTERS_OF_DAY; i++) {
      sum = 0
      houses.each {
        sum = sum + it.weeklyControllableLoad.get(day).get(i)
      }
      v.add(sum)
    }
    return v
  }

  /** This function is used in order to fill the daily Base Load of the household
   * for each hour.
   * @param day
   * @param portion
   * @return
   */
  def fillAggDailyBaseLoadInHours(int day, String portion) {
    // Creating auxiliary variables
    def houses

    if (portion.equals("NotShifting")){
      houses = Constants.NOT_SHIFTING_TYPE
    }
    else if (portion.equals("RandomlyShifting")){
      houses = Constants.RANDOM_SHIFTING_TYPE
    }
    else if (portion.equals("RegularlyShifting")){
      houses = Constants.REGULAR_SHIFTING_TYPE
    }
    else {
      houses = Constants.SMART_SHIFTING_TYPE
    }

    int sum = 0
    for (int i = 0;i < Constants.HOURS_OF_DAY; i++) {
      sum = 0
      sum = aggDailyBaseLoad.get(i*Constants.QUARTERS_OF_HOUR) + aggDailyBaseLoad.get(i*Constants.QUARTERS_OF_HOUR +1) + aggDailyBaseLoad.get(i*Constants.QUARTERS_OF_HOUR+2) + aggDailyBaseLoad.get(i*Constants.QUARTERS_OF_HOUR+3)
      villageConsumersService.setBaseConsumption(this,houses,day,i,sum)
    }
  }

  /** This function is used in order to fill the daily Base Load of the household
   * for each hour.
   * @param day
   * @param portion
   * @return
   */
  def fillAggDailyControllableLoadInHours(int day, String portion) {
    // Creating auxiliary variables
    def houses

    if (portion.equals("NotShifting")){
      houses = Constants.NOT_SHIFTING_TYPE
    }
    else if (portion.equals("RandomlyShifting")){
      houses = Constants.RANDOM_SHIFTING_TYPE
    }
    else if (portion.equals("RegularlyShifting")){
      houses = Constants.REGULAR_SHIFTING_TYPE
    }
    else {
      houses = Constants.SMART_SHIFTING_TYPE
    }

    int sum = 0
    for (int i = 0;i < Constants.HOURS_OF_DAY; i++) {
      sum = 0
      sum = aggDailyControllableLoad.get(i*Constants.QUARTERS_OF_HOUR) + aggDailyControllableLoad.get(i*Constants.QUARTERS_OF_HOUR +1) + aggDailyControllableLoad.get(i*Constants.QUARTERS_OF_HOUR+2) + aggDailyControllableLoad.get(i*Constants.QUARTERS_OF_HOUR+3)
      villageConsumersService.setControllableConsumption(this,houses,day,i,sum)
    }
  }


  /** At the end of each week the village households' models refresh their schedule. 
   * This way we have a realistic and dynamic model, changing function hours, consuming power
   * and so on.
   * @param conf
   * @param gen
   * @return
   */
  def refresh(ConfigObject conf, Random gen) {
    for (int i=0; i < villageConsumersService.households.size();i++){
      for (int j=0; j < villageConsumersService.getHouseholds(this,i).size();j++){
        villageConsumersService.getHouseholds(this,i)[j].refresh(conf,gen)
      }
    }

    villageConsumersService.baseConsumptions.remove(this.customerInfo.name)
    villageConsumersService.createBaseConsumptionsMap(this,types)
    villageConsumersService.controllableConsumptions.remove(this.customerInfo.name)
    villageConsumersService.createControllableConsumptionsMap(this,types)

    fillAggWeeklyLoad("NotShifting")
    fillAggWeeklyLoad("RandomlyShifting")
    fillAggWeeklyLoad("RegularlyShifting")
    fillAggWeeklyLoad("SmartShifting")

  }

  @ Override
  double costEstimation(Tariff tariff)
  {
    double costVariable = estimateShiftingVariableTariffPayment(tariff)
    double costFixed = estimateFixedTariffPayments(tariff)
    return (costVariable + costFixed)/Constants.MILLION
  }

  @ Override
  double estimateVariableTariffPayment(Tariff tariff){

    int serial = ((timeService.currentTime.millis - timeService.base) / TimeService.HOUR)
    Instant base = timeService.currentTime - serial*TimeService.HOUR
    int daylimit = (int) (serial / Constants.HOURS_OF_DAY) + 1 // this will be changed to one or more random numbers

    float finalCostSummary = 0

    def daysList = villageConsumersService.getDays(this)

    daysList.each { day ->
      if (day < daylimit) day = (int) (day + (daylimit / Constants.RANDOM_DAYS_NUMBER))
      Instant now = base + day * TimeService.DAY
      float costSummary = 0
      float summary = 0, cumulativeSummary = 0

      for (int hour=0;hour < Constants.HOURS_OF_DAY;hour++){
        for (int j=0;j < types;j++){
          summary = summary + (villageConsumersService.getBaseConsumptions(this,j)[day][hour] + villageConsumersService.getControllableConsumptions(this,j)[day][hour])
        }
        log.info "Cost for hour ${hour}: ${tariff.getUsageCharge(now)}"
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

  /** This is the new function, used in order to find the most cost efficient tariff over the 
   * available ones. It is using Daily shifting in order to put the appliances operation in most
   * suitable hours of the day.
   * @param tariff
   * @return
   */
  double estimateShiftingVariableTariffPayment(Tariff tariff, Random gen){

    int serial = ((timeService.currentTime.millis - timeService.base) / TimeService.HOUR)
    Instant base = timeService.currentTime - serial*TimeService.HOUR
    int daylimit = (int) (serial / Constants.HOURS_OF_DAY) + 1 // this will be changed to one or more random numbers

    float finalCostSummary = 0

    def daysList = villageConsumersService.getDays(this)

    daysList.each { day ->
      if (day < daylimit) day = (int) (day + (daylimit / Constants.RANDOM_DAYS_NUMBER))
      Instant now = base + day * TimeService.DAY
      float costSummary = 0
      float summary = 0, cumulativeSummary = 0

      BigInteger[] newControllableLoad = dailyShifting(gen,tariff,now,day)

      for (int hour=0;hour < Constants.HOURS_OF_DAY;hour++){
        for (int j=0;j < types;j++){
          summary = summary + (villageConsumersService.getBaseConsumptions(this,j)[day][hour] + newControllableLoad[hour])
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

  /** This is the function that takes every household in the village and 
   * readies the shifted Controllable Consumption for the needs of the tariff evaluation.
   * @param tariff
   * @param now
   * @param day
   * @return
   */
  def dailyShifting(Tariff tariff,Instant now, int day, Random gen){

    BigInteger[] newControllableLoad = new BigInteger[Constants.HOURS_OF_DAY]
    for (int j=0;j < Constants.HOURS_OF_DAY;j++) newControllableLoad[j] = 0

    villageConsumersService.getHouseholds(this).each { house ->
      def temp = house.dailyShifting(tariff,now,day)
      for (int j=0;j < Constants.HOURS_OF_DAY;j++) newControllableLoad[j] += temp[j]
    }

    log.debug("New Controllable Load of Village ${this.toString()} for Tariff ${tariff.toString()}")

    for (int i=0;i < Constants.HOURS_OF_DAY;i++) {
      log.debug("Hour: ${i} Cost: ${tariff.getUsageCharge(now)} Load: ${newControllableLoad[i]}")
      now = now + TimeService.HOUR
    }

    return newControllableLoad

  }

  /** This is the function that takes every household in the village and
   * readies the shifted Controllable Consumption for the needs of the tariff evaluation.
   * @param tariff
   * @param now
   * @param day
   * @return
   */
  def dailyShifting(Random gen,Tariff tariff,Instant now, int type, int day){

    BigInteger[] newControllableLoad = new BigInteger[Constants.HOURS_OF_DAY]
    for (int j=0;j < Constants.HOURS_OF_DAY;j++) newControllableLoad[j] = 0

    villageConsumersService.getHouseholds(this,type).each { house ->
      def temp = house.dailyShifting(tariff,now,day)
      for (int j=0;j < Constants.HOURS_OF_DAY;j++) newControllableLoad[j] += temp[j]
    }
    log.debug("New Controllable Load of Village ${this.toString()} for Tariff ${tariff.toString()}")

    for (int i=0;i < Constants.HOURS_OF_DAY;i++) {
      log.debug("Hour: ${i} Cost: ${tariff.getUsageCharge(now)} Load: ${newControllableLoad[i]}")
      now = now + TimeService.HOUR
    }
    return newControllableLoad
  }

  /** This function prints to the screen the daily load of the village's households for the
   * weekday at hand.
   * @param day
   * @param portion
   * @return
   */
  def printDailyLoad(int day, String portion) {

    def houses

    if (portion.equals("NotShifting")){
      houses = Constants.NOT_SHIFTING_TYPE
    }
    else if (portion.equals("RandomlyShifting")){
      houses = Constants.RANDOM_SHIFTING_TYPE
    }
    else if (portion.equals("RegularlyShifting")){
      houses = Constants.REGULAR_SHIFTING_TYPE
    }
    else {
      houses = Constants.SMART_SHIFTING_TYPE
    }

    for (int i=0; i < villageConsumersService.households.size();i++){
      for (int j=0; j < villageConsumersService.getHouseholds(this,houses).size();j++){
        villageConsumersService.getHouseholds(this,houses)[j].printDailyLoad(day)
      }
    }

  }

  /** This function represents the function that shows the status of all the households
   * in the village each moment in time.
   * @param day
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
   * @param days
   * @param gen
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

  /** This function is creating the list of days for each village that will be utilized
   * for the tariff evaluation.
   * @param days
   * @param gen
   * @return
   */
  def createCostEstimationDaysList(int days, Random gen) {

    Vector daysList = new Vector()

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
      daysList.add(x)
    }
    java.util.Collections.sort(daysList)

    for (int i = 0;i < daysList.size();i++){
      villageConsumersService.setDays(this,i,daysList.get(i))
    }
  }

  @ Override
  void step(Random gen){
    super.step();
    if (timeService.getHourOfDay() == 23) rescheduleNextDay(gen)
  }

  void rescheduleNextDay(Random gen){

    int serial = ((timeService.currentTime.millis - timeService.base) / TimeService.HOUR)
    int day = (int) (serial / Constants.HOURS_OF_DAY) + 1
    Instant now = timeService.currentTime + TimeService.HOUR

    subscriptions.each { sub ->
      for (int i=0;i < types;i++){
        log.info "Old Consumption for day ${day} and Type ${i}: ${villageConsumersService.getControllableConsumptions(this,i,day)}"
        BigInteger[] newControllableLoad = dailyShifting(gen,sub.tariff,now,i,day)
        villageConsumersService.setControllableConsumption(this, i, day,newControllableLoad)
        log.info "New Consumption for day ${day} and Type ${i}: ${villageConsumersService.getControllableConsumptions(this,i,day)}"
      }
    }
  }

  void bootstrapSchedule(){

    Instant now = timeService.currentTime

    for (int day = 0;day < Constants.DAYS_OF_BOOTSTRAP;day++){
      println(now)
      subscriptions.each { sub ->
        for (int i=0;i < types;i++){
          BigInteger[] newControllableLoad = dailyShifting(gen,sub.tariff,now,i,day)
          villageConsumersService.setControllableConsumption(this, i, day,newControllableLoad)
        }
      }
      now = now + 24*TimeService.HOUR
    }
  }

  static auditable = true

  public String toString() {
    customerInfo.getName()
  }

  static constraints = {
  }
}
