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

import java.util.HashMap
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

  /** the household name. It is different for each one to be able to tell them apart.*/
  String name

  /** This is a vector containing each day's load from the appliances installed inside the household. **/
  Vector dailyLoad = new Vector()

  /** This is a vector containing the load from the appliances installed inside the household for all the week days.**/
  Vector weeklyLoad = new Vector()

  /** This is a statistical measure of the household, giving a general idea of the consumption level during a year.*/
  int yearConsumption

  /** This is an agreggated vector containing each day's load in hours. **/
  Vector dailyLoadInHours = new Vector()

  /** This is an agreggated vector containing the weekly load in hours. **/
  Vector weeklyLoadInHours = new Vector()

  /** This variable shows the current load of the house, for the current quarter or hour. **/
  int currentLoad

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
  def initialize(String HouseName, HashMap hm, Vector publicVacationVector, Random gen)
  {
    setName(HouseName)
    int persons = memberRandomizer(hm, gen)
    for (int i = 0;i < persons; i++) addPerson(i+1,hm,publicVacationVector, gen)
    fillAppliances(hm, gen)
    for (int i =0;i < Constants.DAYS_OF_WEEK;i++) {
      setDailyLoad(fillDailyLoad(i))
      weeklyLoad.add(dailyLoad)
      setDailyLoadInHours(fillDailyLoadInHours())
      weeklyLoadInHours.add(dailyLoadInHours)
    }
  }

  /** This function is creating a random number of person (given by the next
   * function) and add them to the current household, filling it up with life.
   * @param counter
   * @param hm
   * @param publicVacationVector
   * @return
   */
  def addPerson(int counter, HashMap hm, Vector publicVacationVector, Random gen) {
    // Taking parameters from configuration file
    int pp = (int)hm.get("PeriodicPresent")
    int mp = (int)hm.get("MostlyPresent")
    int ra = (int)hm.get("RandomlyAbsent")
    float va = (float)hm.get("VacationAbsence")
    
    int x = gen.nextInt(Constants.PERCENTAGE);
    if (x < pp) {
      PeriodicPresentPerson ppp = new PeriodicPresentPerson()
      ppp.initialize("PPP" + counter,hm,publicVacationVector,gen)
      ppp.weeklyRoutine = ppp.fillWeeklyRoutine(va,gen)
      this.addToMembers(ppp)
    } else  {
      if (x >= pp & x < (pp + mp)) {
        MostlyPresentPerson mpp = new MostlyPresentPerson()
        mpp.initialize("MPP" + counter,hm,publicVacationVector,gen)
        mpp.weeklyRoutine = mpp.fillWeeklyRoutine(va,gen)
        this.addToMembers(mpp)
      } else  {
        RandomlyAbsentPerson rap = new RandomlyAbsentPerson()
        rap.initialize("RAP"+ counter,hm,publicVacationVector,gen)
        rap.weeklyRoutine = rap.fillWeeklyRoutine(va,gen)
        this.addToMembers(rap)
      }
    }
  }

  /** This is the function that utilizes the possibilities of the number of persons
   * in a household and gives back a number randomly.
   * @param hm
   * @return
   */
  def memberRandomizer(HashMap hm, Random gen) {
    int one = (int) hm.get("OnePerson")
    int two = (int) hm.get("TwoPersons")
    int three = (int) hm.get("ThreePersons")
    int four = (int) hm.get("FourPersons")
    int five = (int) hm.get("FivePersons")
    int returnValue
    
    int x = gen.nextInt(Constants.PERCENTAGE);
    if (x < one) {
      setYearConsumption((int) hm.get("OnePersonConsumption"))
      returnValue = 1
    } else  {
      if (x >= one &  x < (one + two)) {
        setYearConsumption((int) hm.get("TwoPersonsConsumption"))
        returnValue = 2
      } else  {
        if (x >= (one + two) & x < (one + two + three)) {
          setYearConsumption((int) hm.get("ThreePersonsConsumption"))
          returnValue = 3
        } else  {
          if (x >= (one + two + three) & x < (one + two + three + four)) {
            setYearConsumption((int) hm.get("FourPersonsConsumption"))
            returnValue = 4
          } else  {
            setYearConsumption((int) hm.get("FivePersonsConsumption"))
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
    if (x < threshold) app.fillWeeklyFunction(gen)
    else this.appliances.remove(app);
  }

  /** This function is responsible for the filling of the household with the appliances
   * and their schedule for the first week using a statistic formula and the members 
   * of the household.
   * @param hm
   * @return
   */
  def fillAppliances(HashMap hm, Random gen) {

    // Refrigerator
    Refrigerator ref = new Refrigerator();
    this.addToAppliances(ref)
    ref.initialize(hm,gen);
    ref.fillWeeklyFunction(gen)
    // Washing Machine
    WashingMachine wm = new WashingMachine();
    this.addToAppliances(wm)
    wm.initialize(hm,gen);
    wm.fillWeeklyFunction(gen)
    // Consumer Electronics
    ConsumerElectronics ce = new ConsumerElectronics();
    this.addToAppliances(ce)
    ce.initialize(hm,gen);
    ce.fillWeeklyFunction(gen)
    // ICT
    ICT ict = new ICT();
    this.addToAppliances(ict)
    ict.initialize(hm,gen);
    ict.fillWeeklyFunction(gen)
    // Lights
    Lights lights = new Lights();
    this.addToAppliances(lights)
    lights.initialize(hm,gen);
    lights.fillWeeklyFunction(gen)
    //Others
    Others others = new Others();
    this.addToAppliances(others)
    others.initialize(hm,gen);
    others.fillWeeklyFunction(gen)
    // Freezer
    Freezer fr = new Freezer()
    fr.initialize(hm,gen)
    checkProbability(fr,gen)
    // Dishwasher
    Dishwasher dw = new Dishwasher()
    this.addToAppliances(dw)
    dw.initialize(hm,gen)
    checkProbability(dw,gen)
    //Stove
    Stove st = new Stove()
    this.addToAppliances(st)
    st.initialize(hm,gen)
    checkProbability(st,gen)
    //Dryer
    Dryer dr = new Dryer()
    this.addToAppliances(dr)
    dr.initialize(hm,gen)
    checkProbability(dr,gen)
    //Water Heater
    WaterHeater wh = new WaterHeater()
    this.addToAppliances(wh)
    wh.initialize(hm,gen)
    checkProbability(wh,gen)
    //Circulation Pump
    CirculationPump cp = new CirculationPump()
    this.addToAppliances(cp)
    cp.initialize(hm,gen)
    checkProbability(cp,gen)
    //Space Heater
    SpaceHeater sh = new SpaceHeater()
    this.addToAppliances(sh)
    sh.initialize(hm,gen)
    checkProbability(sh,gen)
  }

  /** This function checks if all the inhabitants of the household are out of the household.
   * 
   * @param quarter
   * @return
   */
  def isEmpty(int quarter) {
    boolean x = true
    this.members.each {
      if (it.getDailyRoutine().get(quarter-1) == Status.Normal || it.getDailyRoutine().get(quarter-1) == Status.Sick) {
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
    System.out.println("HouseHold Name : " + name)
    System.out.println("HouseHold Yearly Consumption : " + yearConsumption)
    System.out.println("Number of Persons : " + members.size())
    // Printing members' status
    Iterator iter = members.iterator();
    while (iter.hasNext()) iter.next().showInfo();
    // Printing appliances' status
    iter = appliances.iterator();
    System.out.println(" Number Of Appliances = ")
    System.out.println(appliances.size())
    while (iter.hasNext()) iter.next().showStatus();
    // Printing weekly load
    System.out.println(" Weekly Load = ")
    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {
      System.out.println("Day " + (i))
      ListIterator iter2 = weeklyLoad.get(i).listIterator();
      for (int j = 0;j < Constants.QUARTERS_OF_DAY; j++) System.out.println("Quarter : " + (j+1) + " Load : " + iter2.next())
    }

    // Printing weekly load in hours
    System.out.println(" Weekly Load In Hours = ")
    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {
      System.out.println("Day " + (i))
      ListIterator iter2 = weeklyLoadInHours.get(i).listIterator();
      for (int j = 0;j < Constants.HOURS_OF_DAY; j++) System.out.println("Hours : " + (j+1) + " Load : " + iter2.next())
    }
  }

  /** This function is used in order to fill the daily Load of the household for each quarter of the hour
   * 
   * @param weekday
   * @return
   */
  def fillDailyLoad(int weekday) {
    // Creating auxiliary variables
    Vector v = new Vector(Constants.QUARTERS_OF_DAY)
    int sum = 0
    for (int i = 0;i < Constants.QUARTERS_OF_DAY; i++) {
      sum = 0
      this.appliances.each {
        sum = sum + it.weeklyLoadVector.get(weekday).get(i)
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
      if (it.getDailyRoutine().get(quarter-1) == Status.Vacation) {
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
  def stepStatus(int weekday, int quarter) {
    // Printing Inhabitants Status
    
    log.info "House: ${name} "
    log.info "Person Quarter Status"

    // For each person in the house
    this.members.each {
      log.info "Name: ${it.getName()} Status: ${it.getWeeklyRoutine().get(weekday).get(quarter)} "
    }

    // Printing Inhabitants Status
    log.info "Appliances Quarter Status"
    this.appliances.each {
      log.info "Name: ${it.getName()} Status: ${it.getWeeklyOperation().get(weekday).get(quarter)} Load: ${it.getWeeklyLoadVector().get(weekday).get(quarter)} "
    }
    // Printing Household Status
    setCurrentLoad(weekday,quarter)
    log.info "Current Load: ${currentLoad} "
  }

  /** This function fills out the daily load in hours vector taking in consideration the load per quarter of an hour
   * 
   * @return
   */
  def fillDailyLoadInHours() {

    // Creating Auxiliary Variables
    Vector v = new Vector(Constants.HOURS_OF_DAY)
    int sum = 0
    for (int i = 0;i < Constants.HOURS_OF_DAY; i++) {
      sum = 0
      sum = dailyLoad.get(i*Constants.QUARTERS_OF_HOUR) + dailyLoad.get(i*Constants.QUARTERS_OF_HOUR +1) + dailyLoad.get(i*Constants.QUARTERS_OF_HOUR+2) + dailyLoad.get(i*Constants.QUARTERS_OF_HOUR+3)
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
  def setCurrentLoad(int weekday, int quarter) {
    setCurrentLoad(weeklyLoad.get(weekday).get(quarter))
  }

  /** At the end of each week the household models refresh their schedule. This way
   * we have a realistic and dynamic model, changing function hours, consuming power
   * and so on.
   * @param hm
   * @return
   */
  def refresh(HashMap hm, Random gen) {

    log.info "Refresh Weekly Routine Of House ${name} "
    log.info "Refresh Weekly Routine Of House Of Household Members"

    // For each member of the household
    this.members.each {member -> 
      member.refresh(hm,gen) 
    }

    // Refreshing appliance's function schedule
    log.info "Refresh Weekly Functions of Appliances"

    this.appliances.each { appliance ->
      appliance.refresh(gen)
    }

    // Erase information from vectors
    weeklyLoad.removeAllElements()
    weeklyLoadInHours.removeAllElements()
    for (int i =0;i < Constants.DAYS_OF_WEEK;i++) {
      setDailyLoad(fillDailyLoad(i))
      weeklyLoad.add(dailyLoad)
      setDailyLoadInHours(fillDailyLoadInHours())
      weeklyLoadInHours.add(dailyLoadInHours)
    }
    this.save()
  }

  /** This function prints to the screen the daily load of the household for the 
   * weekday at hand 
   * @param weekday
   * @return
   */
  def printDailyLoad(int weekday) {
    ListIterator iter = weeklyLoadInHours.get(weekday).listIterator()
    log.info "Summary of Daily Load of House ${name} "
    for (int j = 0;j < Constants.HOURS_OF_DAY; j++) log.info "Hour : ${j+1} Load : ${iter.next()} "
  }

  public String toString() {
    this.getName()
  }
  
  static constraints = {
  }
}
