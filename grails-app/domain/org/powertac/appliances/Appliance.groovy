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

package org.powertac.appliances

import java.util.HashMap
import java.util.Random
import java.util.Vector

import org.powertac.common.configurations.Constants
import org.powertac.consumers.*

/**
 * A appliance domain instance represents a single appliance inside a household. There
 * are different kinds of appliances utilized by the persons inhabiting the premises.
 * Some of them are functioning automatically, some are used only by someone etc
 *
 * @author Antonios Chrysopoulos
 * @version 1, 13/02/2011
 */

class Appliance {

  def householdConsumersService
  
  /** the appliance name. It depends on the type of appliance and the household that contains it.*/
  String name

  /** this variable shows the possibility (%) that this appliance is contained in a house */
  float saturation

  /** the percentage (%) that this appliance utilizes of the total power consumption that is consumed in the household */
  float consumptionShare

  /** the percentage (%) that this appliance utilizes of the base power consumption that is consumed in the household */
  float baseLoadShare

  /** this variable shows the Watt that are consumed by using this appliance */
  int power

  /** this variable equals the duration of the operation cycle of the appliance*/
  int cycleDuration

  /** this is the occupancy dependence boolean variable*/
  boolean od

  /** this variable is true if the appliance is currently in use (automatically or by the tenants */
  boolean inUse

  /** This variable contains the possibility that this appliance will work during a certain season of the year */
  HashMap probabilitySeason

  /** This variable contains the possibility that this appliance will work during a certain weekday */
  HashMap probabilityWeekday

  /** This variable contains the possibility that this appliance will work during a certain hour of the day */
  HashMap probabilityDaytime

  /** This is a vector containing the quarters that the appliance should start functioning (before shifting) */
  Vector operationVector = new Vector()

  /** This is a vector containing the load of consumption of the appliance during the day */
  Vector loadVector = new Vector()

  /** This is a vector containing the daily operation of the appliance (after shifting due to any cause) */
  Vector dailyOperation = new Vector()

  /** This is a vector containing the weekly operation of the appliance (after shifting due to any cause) */
  Vector weeklyOperation = new Vector()

  /** This is a vector containing the weekly load of the appliance (after shifting due to any cause) */
  Vector weeklyLoadVector = new Vector()

  /** This variable contains the amount of times the appliance may work through the week or day */
  int times

  static belongsTo = [applianceOf:Household]

  /** This function is used to create the operation vector of the appliance for the week
   * taking into consideration the times that this appliance has to function.
   * @param times
   * @return
   */

  def createOperationVector(int times, Random gen) {
  }

  /** This function takes into consideration the year season, the weekday and the
   * hour of day and returns the possibility for an appliance to be functioning.
   * @param season
   * @param day
   * @param hour
   * @return
   */
  def getProbability(String season, String day, int hour) {

    float pseason = (float) probabilitySeason.get(season);
    float pday = (float) probabilityWeekday.get(day);
    float phour = (float) probabilityDaytime.get(hour);
    return pseason * pday * phour
  }

  /** This is the initialization function. It uses the variable values for the
   * configuration file to create the appliance as it should for this type.
   * @return
   */
  def initialize(Random gen) {
  }

  /** This is a complex function that changes the appliance's function
   * in order to save energy and money. There is no implementation ready
   * for this yet.
   * @param v
   * @return
   */
  def shiftingOperation(Vector v) {
  }


  /** This is the filling function of the HashMap for the Days of the Week possibilities.
   * 
   * @param sunday
   * @param workingday
   * @param saturday
   * @return
   */
  def fillDay(float sunday, float workingday, float saturday) {

    HashMap hm = new HashMap();
    hm.put("Saturday", new Float(saturday));
    hm.put("Sunday", new Float(sunday));
    hm.put("Monday", new Float(workingday));
    hm.put("Tuesday", new Float(workingday));
    hm.put("Wednesday", new Float(workingday));
    hm.put("Thursday", new Float(workingday));
    hm.put("Friday", new Float(workingday));
    return hm
  }

  /** This is the filling function of the HashMap for the Seasons of the year possibilities.
   * 
   * @param summer
   * @param winter
   * @param transition
   * @return
   */
  def fillSeason(float summer, float winter, float transition) {

    HashMap hm = new HashMap();
    hm.put("Winter", new Float(winter));
    hm.put("Transition", new Float(transition));
    hm.put("Summer", new Float(summer));
    return hm
  }

  /** This is the filling function of the HashMap for the Hours of the Day possibilities.
   * 
   * @return
   */
  def fillHour() {
  }


  /** This is the function utilized to show the information regarding
   * the appliance in question, its variables values etc.
   * @return
   */

  def showStatus() {
    // Printing base variables
    log.info("Name = " + name)
    log.info("Member Of = " + applianceOf.getName())
    log.info("Saturation = " + saturation)
    log.info("Consumption Share = " + consumptionShare)
    log.info("Base Load Share = " + baseLoadShare)
    log.info("Power = " + power)
    log.info("Cycle Duration = "+ cycleDuration)
    log.info("Occupancy Dependence = "+ od)
    log.info("In Use = " + inUse)

    // Printing probability variables variables
    Set set = probabilitySeason.entrySet();
    Iterator it = set.iterator();
    log.info("Probability Season = ")
    while (it.hasNext()) {
      Map.Entry me = (Map.Entry)it.next();
      log.info(me.getKey() + " : " + me.getValue() );
    }

    set = probabilityWeekday.entrySet();
    it = set.iterator();
    log.info("Probability Weekday = ")
    while (it.hasNext()) {
      Map.Entry me = (Map.Entry)it.next();
      log.info(me.getKey() + " : " + me.getValue() );
    }

    // Printing weekly Operation Vector
    log.info("Weekly Operation Vector = ")
    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {
      log.info("Day " + (i))
      ListIterator iter =operationVector.get(i).listIterator();
      for (int j = 0;j < Constants.QUARTERS_OF_DAY; j++) log.info("Quarter : " + (j+1) + "  " + iter.next())
    }

    // Printing Weekly Function Vector and Load
    log.info("Weekly Operation Vector and Load = ")
    for (int i = 0; i < Constants.DAYS_OF_WEEK;i++) {
      log.info("Day " + (i))
      ListIterator iter = weeklyOperation.get(i).listIterator();
      ListIterator iter2 = weeklyLoadVector.get(i).listIterator();
      for (int j = 0;j < Constants.QUARTERS_OF_DAY; j++) log.info("Quarter " + (j+1) + " = " + iter.next() + "   Load = " + iter2.next())
    }
  }

  /** This function fills out the daily function of an appliance for the day. */
  def fillDailyFunction() {
  }

  /** At the end of each week the appliance models refresh their schedule. This way
   * we have a realistic and dynamic model, changing function hours, consuming power
   * and so on.
   * @return
   */
  def refresh(ConfigObject conf, Random gen) {
  }

  /** This is an function to fill the maps utilized by Services in order to keep the vectors of each appliance
   *  during the runtime.
   * @return
   */
  def setVectors(int index) {
    
    for (int i=0;i < weeklyOperation.size();i++){
      
      for (int j=0;j < 96;j++){
        
        householdConsumersService.setApplianceOperation (applianceOf, index, i, j, weeklyOperation.get(i).get(j))
        householdConsumersService.setApplianceLoad (applianceOf, index, i, j, weeklyLoadVector.get(i).get(j))
      }
      
    }
    
  }

  static constraints = {

    name()
    applianceOf()
    power()
    cycleDuration()
    inUse()

  }

  static mapping = { sort "name" }

  String toString(){
    "${name}, ${Household} (${inUse})"
  }
}
