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
import java.util.Vector

import org.powertac.appliances.*
import org.powertac.common.configurations.Constants
import org.powertac.common.enumerations.Status
import org.powertac.persons.*
/**
 * The household is the domain instance represents a single house with the tenants living
 * inside it and fully equipped with appliances statistically distributed. There
 * are different kinds of appliances utilized by the persons inhabiting the premises and
 * each person living has it's own schedule.
 *
 * @author Antonios Chrysopoulos
 * @version 1, 13/02/2011
 */


class Household {

  def householdConsumersService

  /** the household name. It is different for each one to be able to tell them apart.*/
  String name

  /** This is a vector containing each day's load from the appliances installed inside the household. **/
  Vector dailyBaseLoad = new Vector()

  /** This is a vector containing each day's load from the appliances installed inside the household. **/
  Vector dailyControllableLoad = new Vector()

  /** This is a vector containing the load from the appliances installed inside the household for all the week days.**/
  Vector weeklyBaseLoad = new Vector()

  /** This is a vector containing the load from the appliances installed inside the household for all the week days.**/
  Vector weeklyControllableLoad = new Vector()

  /** This is a statistical measure of the household, giving a general idea of the consumption level during a year.*/
  int yearConsumption

  /** This is an agreggated vector containing each day's load in hours. **/
  Vector dailyBaseLoadInHours = new Vector()

  /** This is an agreggated vector containing each day's load in hours. **/
  Vector dailyControllableLoadInHours = new Vector()

  /** This is an agreggated vector containing the weekly base load in hours. **/
  Vector weeklyBaseLoadInHours = new Vector()

  /** This is an agreggated vector containing the weekly controllable load in hours. **/
  Vector weeklyControllableLoadInHours = new Vector()

  /** This variable shows the current load of the house, for the current quarter or hour. **/
  int currentLoad

  /** Helping variable for the correct refreshing of the schedules.*/
  int week = 0

  static hasMany = [members:Person, appliances:Appliance]

  static belongsTo = [village:Village]

  /** This is the initialization function. It uses the variable values for the
   * configuration file to create the household and then fill it with persons and
   * appliances as it seems fit.
   * @param HouseName
   * @param hm
   * @param publicVacationVector
   * @return
   */
  def initialize(String HouseName, ConfigObject conf, Vector publicVacationVector, Random gen) {
    float va = (float)conf.household.vacation.VacationAbsence
    setName(HouseName)
    int persons = memberRandomizer(conf, gen)
    for (int i = 0;i < persons; i++) addPerson(i+1,conf,publicVacationVector, gen)

    this.members.each { member ->
      for (int i =0;i < Constants.DAYS_OF_WEEK;i++) {
        member.fillDailyRoutine(i,va, gen)
        member.weeklyRoutine.add(member.dailyRoutine)
      }
    }

    fillAppliances(conf, gen)

    for (int i =0;i < Constants.DAYS_OF_WEEK;i++) {
      setDailyBaseLoad(fillDailyBaseLoad(i))
      setDailyControllableLoad(fillDailyControllableLoad(i))
      weeklyBaseLoad.add(dailyBaseLoad)
      weeklyControllableLoad.add(dailyControllableLoad)
      setDailyBaseLoadInHours(fillDailyBaseLoadInHours())
      setDailyControllableLoadInHours(fillDailyControllableLoadInHours())
      weeklyBaseLoadInHours.add(dailyBaseLoadInHours)
      weeklyControllableLoadInHours.add(dailyControllableLoadInHours)
    }

    for (week;week < 8;week++){
      refresh(conf,gen)
    }

    this.appliances.each{ appliance ->
      appliance.setVectors()
    }
  }

  /** This function is creating a random number of person (given by the next
   * function) and add them to the current household, filling it up with life.
   * @param counter
   * @param hm
   * @param publicVacationVector
   * @return
   */
  def addPerson(int counter, ConfigObject conf, Vector publicVacationVector, Random gen) {
    // Taking parameters from configuration file
    int pp = (int)conf.household.person.personType.PeriodicPresent
    int mp = (int)conf.household.person.personType.MostlyPresent
    int ra = (int)conf.household.person.personType.RandomlyAbsent

    int x = gen.nextInt(Constants.PERCENTAGE);
    if (x < pp) {
      PeriodicPresentPerson ppp = new PeriodicPresentPerson()
      ppp.initialize("PPP" + counter,conf,publicVacationVector,gen)
      this.addToMembers(ppp)
    } else  {
      if (x >= pp & x < (pp + mp)) {
        MostlyPresentPerson mpp = new MostlyPresentPerson()
        mpp.initialize("MPP" + counter,conf,publicVacationVector,gen)
        this.addToMembers(mpp)
      } else  {
        RandomlyAbsentPerson rap = new RandomlyAbsentPerson()
        rap.initialize("RAP"+ counter,conf,publicVacationVector,gen)
        this.addToMembers(rap)
      }
    }
  }

  /** This is the function that utilizes the possibilities of the number of persons
   * in a household and gives back a number randomly.
   * @param hm
   * @return
   */
  def memberRandomizer(ConfigObject conf, Random gen) {
    int one = conf.household.person.personsInHousehold.OnePerson
    int two = conf.household.person.personsInHousehold.TwoPersons
    int three = conf.household.person.personsInHousehold.ThreePersons
    int four = conf.household.person.personsInHousehold.FourPersons
    int five = conf.household.person.personsInHousehold.FivePersons
    int returnValue

    int x = gen.nextInt(Constants.PERCENTAGE);
    if (x < one) {
      setYearConsumption((int) conf.household.person.consumption.OnePersonConsumption)
      returnValue = 1
    } else  {
      if (x >= one &  x < (one + two)) {
        setYearConsumption((int) conf.household.person.consumption.TwoPersonsConsumption)
        returnValue = 2
      } else  {
        if (x >= (one + two) & x < (one + two + three)) {
          setYearConsumption((int) conf.household.person.consumption.ThreePersonsConsumption)
          returnValue = 3
        } else  {
          if (x >= (one + two + three) & x < (one + two + three + four)) {
            setYearConsumption((int) conf.household.person.consumption.FourPersonsConsumption)
            returnValue = 4
          } else  {
            setYearConsumption((int) conf.household.person.consumption.FivePersonsConsumption)
            returnValue = 5
          }
        }
      }
    }
    return returnValue
  }

  /** This function is using the appliance's saturation in order to make a possibility
   * check and install or not the appliance in the current household.
   * @param app
   * @return
   */
  def checkProbability(Appliance app, Random gen) {
    // Creating auxiliary variables

    int x = gen.nextInt(Constants.PERCENTAGE);
    int threshold = app.saturation * Constants.PERCENTAGE
    if (x < threshold) {
      app.fillWeeklyFunction(gen)
      app.createWeeklyPossibilityOperationVector()
    }
    else this.appliances.remove(app);
  }

  /** This function is responsible for the filling of the household with the appliances
   * and their schedule for the first week using a statistic formula and the members 
   * of the household.
   * @param hm
   * @return
   */
  def fillAppliances(ConfigObject conf, Random gen) {

    // Refrigerator
    Refrigerator ref = new Refrigerator();
    this.addToAppliances(ref)
    ref.initialize(this.name, conf,gen);
    ref.fillWeeklyFunction(gen)
    ref.createWeeklyPossibilityOperationVector()
    // Washing Machine
    WashingMachine wm = new WashingMachine();
    this.addToAppliances(wm)
    wm.initialize(this.name,conf,gen);
    wm.fillWeeklyFunction(gen)
    wm.createWeeklyPossibilityOperationVector()
    // Consumer Electronics
    ConsumerElectronics ce = new ConsumerElectronics();
    this.addToAppliances(ce)
    ce.initialize(this.name,conf,gen);
    ce.fillWeeklyFunction(gen)
    ce.createWeeklyPossibilityOperationVector()
    // ICT
    ICT ict = new ICT();
    this.addToAppliances(ict)
    ict.initialize(this.name,conf,gen);
    ict.fillWeeklyFunction(gen)
    ict.createWeeklyPossibilityOperationVector()
    // Lights
    Lights lights = new Lights();
    this.addToAppliances(lights)
    lights.initialize(this.name,conf,gen);
    lights.fillWeeklyFunction(gen)
    lights.createWeeklyPossibilityOperationVector()
    //Others
    Others others = new Others();
    this.addToAppliances(others)
    others.initialize(this.name,conf,gen);
    others.fillWeeklyFunction(gen)
    others.createWeeklyPossibilityOperationVector()
    // Freezer
    Freezer fr = new Freezer()
    fr.initialize(this.name,conf,gen)
    checkProbability(fr,gen)
    // Dishwasher
    Dishwasher dw = new Dishwasher()
    this.addToAppliances(dw)
    dw.initialize(this.name,conf,gen)
    checkProbability(dw,gen)
    //Stove
    Stove st = new Stove()
    this.addToAppliances(st)
    st.initialize(this.name,conf,gen)
    checkProbability(st,gen)
    //Dryer
    Dryer dr = new Dryer()
    this.addToAppliances(dr)
    dr.initialize(this.name,conf,gen)
    checkProbability(dr,gen)
    //Water Heater
    WaterHeater wh = new WaterHeater()
    this.addToAppliances(wh)
    wh.initialize(this.name,conf,gen)
    checkProbability(wh,gen)
    //Circulation Pump
    CirculationPump cp = new CirculationPump()
    this.addToAppliances(cp)
    cp.initialize(this.name,conf,gen)
    checkProbability(cp,gen)
    //Space Heater
    SpaceHeater sh = new SpaceHeater()
    this.addToAppliances(sh)
    sh.initialize(this.name,conf,gen)
    checkProbability(sh,gen)
  }

  /** This function checks if all the inhabitants of the household are out of the household.
   * 
   * @param quarter
   * @return
   */
  def isEmpty(int weekday, int quarter) {
    boolean x = true
    this.members.each {
      if (it.weeklyRoutine.get(week*Constants.DAYS_OF_WEEK+weekday).get(quarter) == Status.Normal || it.weeklyRoutine.get(week*Constants.DAYS_OF_WEEK+weekday).get(quarter) == Status.Sick) {
        x = false
      }
    }
    return x
  }

  /** This is the function utilized to show the information regarding
   * the household in question, its variables values etc.
   * @return
   */
  def showStatus() {
    // Printing basic variables
    log.info("HouseHold Name : " + name)
    log.info("HouseHold Yearly Consumption : " + yearConsumption)
    log.info("Number of Persons : " + members.size())
    // Printing members' status
    Iterator iter = members.iterator();
    while (iter.hasNext()) iter.next().showInfo();
    // Printing appliances' status
    iter = appliances.iterator();
    log.info(" Number Of Appliances = ")
    log.info(appliances.size())
    while (iter.hasNext()) iter.next().showStatus();
    // Printing daily load
    log.info(" Daily Load = ")
    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {
      log.info("Day " + (i))
      ListIterator iter2 = weeklyBaseLoad.get(i).listIterator();
      ListIterator iter3 = weeklyControllableLoad.get(i).listIterator();
      for (int j = 0;j < Constants.QUARTERS_OF_DAY; j++) log.info("Quarter : " + (j+1) + " Base Load : " + iter2.next() + " Controllable Load: " + iter3.next())
    }

    // Printing daily load in hours
    log.info(" Load In Hours = ")
    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {
      log.info("Day " + (i))
      ListIterator iter2 = weeklyBaseLoadInHours.get(i).listIterator();
      ListIterator iter3 = weeklyControllableLoadInHours.get(i).listIterator();
      for (int j = 0;j < Constants.HOURS_OF_DAY; j++) log.info("Hours : " + (j+1) + " Base Load : " + iter2.next() + " Controllable Load: " + iter3.next())
    }
  }

  /** This function is used in order to fill the daily Base Load of the household for each quarter of the hour
   * 
   * @param weekday
   * @return
   */
  def fillDailyBaseLoad(int weekday) {
    // Creating auxiliary variables
    Vector v = new Vector(Constants.QUARTERS_OF_DAY)
    int sum = 0
    for (int i = 0;i < Constants.QUARTERS_OF_DAY; i++) {
      sum = 0
      this.appliances.each {
        if (it instanceof NotShiftingAppliance) sum = sum + it.weeklyLoadVector.get(weekday).get(i)
      }
      v.add(sum)
    }
    return v
  }

  /** This function is used in order to fill the daily Controllable Load of the household for each quarter of the hour
   *
   * @param weekday
   * @return
   */
  def fillDailyControllableLoad(int weekday) {
    // Creating auxiliary variables
    Vector v = new Vector(Constants.QUARTERS_OF_DAY)
    int sum = 0
    for (int i = 0;i < Constants.QUARTERS_OF_DAY; i++) {
      sum = 0
      this.appliances.each {
        if (!(it instanceof NotShiftingAppliance)) sum = sum + it.weeklyLoadVector.get(weekday).get(i)
      }
      v.add(sum)
    }
    return v
  }

  /** This function checks if all the inhabitants of the household are away on vacation on a certain quarter
   * 
   * @param quarter
   * @return
   */
  def isOnVacation(int quarter) {
    boolean x = false
    this.members.each {
      if (it.getDailyRoutine().get(quarter) == Status.Vacation) {
        x = true
      }
    }
    return x
  }

  /** This function represents the function that shows the conditions in an household each moment in time
   * 
   * @param weekday
   * @param quarter
   * @return
   */
  def stepStatus(int day, int quarter) {
    // Printing Inhabitants Status

    log.info "House: ${name} "
    log.info "Person Quarter Status"

    // For each person in the house
    this.members.each { log.info "Name: ${it.getName()} Status: ${it.getWeeklyRoutine().get(day).get(quarter)} " }

    // Printing Inhabitants Status
    log.info "Appliances Quarter Status"
    this.appliances.each { log.info "Name: ${it.getName()} Status: ${it.getWeeklyOperation().get(day).get(quarter)} Load: ${it.getWeeklyLoadVector().get(day).get(quarter)} " }
    // Printing Household Status
    setCurrentLoad(day,quarter)
    log.info "Current Load: ${currentLoad} "
  }

  /** This function fills out the daily Base Load in hours vector taking in consideration the load per quarter of an hour
   * 
   * @return
   */
  def fillDailyBaseLoadInHours() {

    // Creating Auxiliary Variables
    Vector v = new Vector(Constants.HOURS_OF_DAY)
    int sum = 0
    for (int i = 0;i < Constants.HOURS_OF_DAY; i++) {
      sum = 0
      sum = dailyBaseLoad.get(i*Constants.QUARTERS_OF_HOUR) + dailyBaseLoad.get(i*Constants.QUARTERS_OF_HOUR +1) + dailyBaseLoad.get(i*Constants.QUARTERS_OF_HOUR+2) + dailyBaseLoad.get(i*Constants.QUARTERS_OF_HOUR+3)
      v.add(sum)
    }
    return v
  }

  /** This function fills out the daily Controllable Load in hours vector taking in consideration the load per quarter of an hour
   *
   * @return
   */
  def fillDailyControllableLoadInHours() {

    // Creating Auxiliary Variables
    Vector v = new Vector(Constants.HOURS_OF_DAY)
    int sum = 0
    for (int i = 0;i < Constants.HOURS_OF_DAY; i++) {
      sum = 0
      sum = dailyControllableLoad.get(i*Constants.QUARTERS_OF_HOUR) + dailyControllableLoad.get(i*Constants.QUARTERS_OF_HOUR +1) + dailyControllableLoad.get(i*Constants.QUARTERS_OF_HOUR+2) + dailyControllableLoad.get(i*Constants.QUARTERS_OF_HOUR+3)
      v.add(sum)
    }
    return v
  }


  /** This function set the current load in accordance with the time of the competition
   * 
   * @param weekday
   * @param quarter
   * @return
   */
  def setCurrentLoad(int day, int quarter) {
    setCurrentLoad(weeklyBaseLoad.get(day).get(quarter) + weeklyControllableLoad.get(day).get(quarter))
  }

  /** At the end of each week the household models refresh their schedule. This way
   * we have a realistic and dynamic model, changing function hours, consuming power
   * and so on.
   * @param hm
   * @return
   */
  def refresh(ConfigObject conf, Random gen) {

    // For each member of the household
    this.members.each {member ->
      member.refresh(conf,gen)
    }

    // For each appliance of the household
    this.appliances.each { appliance ->
      appliance.refresh(gen)
    }

    for (int i =0;i < Constants.DAYS_OF_WEEK;i++) {
      setDailyBaseLoad(fillDailyBaseLoad(i))
      setDailyControllableLoad(fillDailyControllableLoad(i))
      weeklyBaseLoad.add(dailyBaseLoad)
      weeklyControllableLoad.add(dailyControllableLoad)
      setDailyBaseLoadInHours(fillDailyBaseLoadInHours())
      setDailyControllableLoadInHours(fillDailyControllableLoadInHours())
      weeklyBaseLoadInHours.add(dailyBaseLoadInHours)
      weeklyControllableLoadInHours.add(dailyControllableLoadInHours)
    }

    this.save()
  }

  /** This function prints to the screen the daily load of the household for the 
   * weekday at hand 
   * @param weekday
   * @return
   */
  def printDailyLoad(int day) {
    ListIterator iter = weeklyBaseLoadInHours.get(day).listIterator()
    ListIterator iter2 = weeklyControllableLoadInHours.get(day).listIterator()
    log.info "Summary of Daily Load of House ${name} "
    for (int j = 0;j < Constants.HOURS_OF_DAY; j++) log.info "Hour : ${j+1} Base Load : ${iter.next()} Controllable Load : ${iter2.next()} "
  }

  public String toString() {
    this.getName()
  }

  static constraints = {
  }
}
