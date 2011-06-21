/*
 * Copyright (c) 2011 by the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package powertac.household.customer

import org.powertac.common.configurations.Constants
import org.powertac.consumers.Household
import org.powertac.consumers.Village


/**
 * Stores Households in each category of consumers and consumption vectors on behalf of Household Customers, bypassing the database.
 * @author Antonios Chrysopoulos
 */
class VillageConsumersService {

  static transactional = true

  Map households = [:]
  Map baseConsumptions = [:]
  Map controllableConsumptions = [:]
  Map bootstrapConsumptions = [:]
  Map days = [:]

  // manage rate maps
  void createHouseholdsMap (Village village, int types, int population)
  {
    log.debug "create household map for Household Customer ${village.toString()} [${types}]"
    households[village.customerInfo.name] = new Household[types][population]
  }

  // manage tier lists
  def getHouseholds(Village village)
  {
    def householdMap = households[village.customerInfo.name]
    def houses = new ArrayList()

    if (householdMap == null) {
      log.error "could not find household map for Village ${village.toString()}"
      return
    }

    for (int i=0;i < village.types;i++) {
      houses.addAll(householdMap[i])
    }

    return houses
  }

  // manage tier lists
  def getHouseholds(Village village, int type)
  {
    def householdMap = households[village.customerInfo.name]

    if (householdMap == null) {
      log.error "could not find household map for Village ${village.toString()}"
      return
    }
    return householdMap[type]
  }

  void setHousehold(Village village, int type, int index, Household house)
  {
    def householdMap = households[village.customerInfo.name]
    if (householdMap == null) {
      log.error "could not find Household map for village ${village.toString()}"
      return
    }
    householdMap[type][index] = house
  }

  void createBaseConsumptionsMap (Village village, int types)
  {
    log.debug "create Base Consumption map for Household Customer ${village.customerInfo.name} [${types}]"
    baseConsumptions[village.customerInfo.name] = new BigInteger[types][Constants.DAYS_OF_COMPETITION][Constants.HOURS_OF_DAY]
  }

  def getBaseConsumptions(Village village, int type)
  {
    return baseConsumptions[village.customerInfo.name][type]
  }

  void setBaseConsumption(Village village, int type, int day, int hour, BigDecimal value)
  {
    def baseConsumptionMap = baseConsumptions[village.customerInfo.name]
    if (baseConsumptionMap == null) {
      log.error "could not find Base Consumption map for village ${village.toString()}"
      return
    }
    baseConsumptionMap[type][day][hour] = value
  }

  void createControllableConsumptionsMap (Village village, int types)
  {
    log.debug "create Controllable consumption map for Household Customer ${village.customerInfo.name} [${types}]"
    controllableConsumptions[village.customerInfo.name] = new BigInteger[types][Constants.DAYS_OF_COMPETITION][Constants.HOURS_OF_DAY]
  }

  def getControllableConsumptions(Village village, int type, int day)
  {
    def sumControllableLoad = new long[Constants.HOURS_OF_DAY]

    for (int j=0;j < Constants.HOURS_OF_DAY;j++){
      sumControllableLoad[j] = controllableConsumptions[village.customerInfo.name][type][day][j]
    }
    return sumControllableLoad
  }

  def getControllableConsumptions(Village village, int type)
  {
    return controllableConsumptions[village.customerInfo.name][type]
  }

  void setControllableConsumption(Village village, int type, int day, int hour, BigInteger value)
  {
    def controllableConsumptionMap = controllableConsumptions[village.customerInfo.name]
    if (controllableConsumptionMap == null) {
      log.error "could not find Controllable Consumption map for village ${village.toString()}"
      return
    }
    controllableConsumptionMap[type][day][hour] = value
  }

  void setControllableConsumption(Village village, int type, int day, BigInteger[] value)
  {
    def controllableConsumptionMap = controllableConsumptions[village.customerInfo.name]
    if (controllableConsumptionMap == null) {
      log.error "could not find Controllable Consumption map for village ${village.toString()}"
      return
    }
    for (int i=0;i < Constants.HOURS_OF_DAY;i++){
      controllableConsumptionMap[type][day][i] = value[i]
    }
  }


  void createDaysMap (Village village)
  {
    log.debug "create Days List map for Household Customer ${village.toString()}"
    days[village.customerInfo.name] = new int[Constants.RANDOM_DAYS_NUMBER]
  }


  def getDays(Village village)
  {
    def dayMap = days[village.customerInfo.name]

    if (dayMap == null) {
      log.error "could not find household map for Village ${village.toString()}"
      return
    }
    return dayMap
  }

  void setDays(Village village, int index, int value)
  {
    def dayMap = days[village.customerInfo.name]
    if (dayMap == null) {
      log.error "could not find Household map for village ${village.toString()}"
      return
    }
    dayMap[index] = value
  }

  void createBootstrapConsumptionsMap (Village village)
  {
    log.debug "create Base Consumption map for Household Customer ${village.customerInfo.name}"
    bootstrapConsumptions[village.customerInfo.name] = new BigInteger[Constants.DAYS_OF_BOOTSTRAP][Constants.HOURS_OF_DAY]
  }

  def getBootstrapConsumptions(Village village)
  {
    return bootstrapConsumptions[village.customerInfo.name]
  }

  void setBootstrapConsumptions(Village village)
  {
    def bootstrapConsumptionMap = bootstrapConsumptions[village.customerInfo.name]
    if (bootstrapConsumptionMap == null) {
      log.error "could not find Bootstrap Consumption map for village ${village.toString()}"
      return
    }

    for (int j=0;j < Constants.DAYS_OF_BOOTSTRAP;j++) {
      for (int k=0;k < Constants.HOURS_OF_DAY;k++){
        BigInteger temp = 0
        for (int i=0;i < village.types;i++)  temp += getBaseConsumptions(village,i)[j][k] + getControllableConsumptions(village,i)[j][k]
        bootstrapConsumptionMap[j][k] = temp
      }
    }
  }

  def getSumConsumptions(Village village){

    def sumConsumption = new BigInteger[Constants.DAYS_OF_COMPETITION][Constants.HOURS_OF_DAY]

    for (int j=0;j < Constants.DAYS_OF_COMPETITION;j++) {
      for (int k=0;k < Constants.HOURS_OF_DAY;k++){
        BigInteger temp = 0
        for (int i=0;i < village.types;i++) temp += getBaseConsumptions(village,i)[j][k] + getControllableConsumptions(village,i)[j][k]
        sumConsumption[j][k] = temp
      }
    }
    return sumConsumption
  }


}

