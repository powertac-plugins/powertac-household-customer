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

import org.powertac.common.enumerations.Status
import org.powertac.consumers.Household

/**
 * Stores Households in each category of consumers and consumption vectors on behalf of Household Customers, bypassing the database.
 * @author Antonios Chrysopoulos
 */
class HouseholdConsumersService {

  static transactional = true

  /** This variable contains the persons' schedule during the competition period.*/
  Map persons = [:]

  /** This variable contains the operating vectors of each appliance during the competition period. */
  Map appliancesOperations = [:]

  /** This variable contains the load of the appliances usage throughout the competition */
  Map appliancesLoads = [:]

  /** This variable contains the possible operation times of each appliance for the competition's duration. */
  Map appliancesPossibilityOperations = [:]

  void createPersonsMap (Household household, int persons) {
    log.info "create persons map for Household ${household.toString()} [${persons}]"
    this.persons[household.name] = new Status[persons][63][96]
  }

  void createAppliancesOperationsMap (Household household, int appliances) {
    log.info "create appliance Operations map for Household ${household.toString()} [${appliances}]"
    appliancesOperations[household.name] = new boolean[appliances][63][96]
  }

  void createAppliancesLoadsMap (Household household, int appliances) {
    log.info "create appliance load map for Household ${household.toString()} [${appliances}]"
    appliancesLoads[household.name] = new int[appliances][63][96]
  }

  void createAppliancesPossibilityOperationsMap (Household household, int appliances) {
    log.info "create appliance Possibility Operations map for Household ${household.toString()} [${appliances}]"
    appliancesPossibilityOperations[household.name] = new boolean[appliances][63][96]
  }

  void setPerson(Household household, int index, int day, int quarter, Status status) {
    def personsMap = persons[household.name]
    if (personsMap == null) {
      log.error "could not find Persons map for household ${household.toString()}"
      return
    }
    personsMap[index][day][quarter] = status
  }

  void setApplianceOperation(Household household, int index, int day, int quarter, Boolean function) {
    def applianceOperationMap = appliancesOperations[household.name]
    if (applianceOperationMap == null) {
      log.error "could not find Appliance Operation map for household ${household.toString()}"
      return
    }
    applianceOperationMap[index][day][quarter] = function
  }

  void setApplianceLoad(Household household, int index, int day, int quarter, BigDecimal value) {
    def applianceLoadMap = appliancesLoads[household.name]
    if (applianceLoadMap == null) {
      log.error "could not find Appliance Load map for Household ${household.toString()}"
      return
    }
    applianceLoadMap[index][day][quarter] = value
  }

  void setAppliancePossibilityOperation(Household household, int index, int day, int quarter, Boolean function) {
    def appliancePossibilityOperationMap = appliancesPossibilityOperations[household.name]
    if (appliancePossibilityOperationMap == null) {
      log.error "could not find Appliance Operation map for household ${household.toString()}"
      return
    }
    appliancePossibilityOperationMap[index][day][quarter] = function
  }

  def getPersons(Household household, int index) {
    return persons[household.name][index]
  }

  def getPersons(Household household, int index, int day) {
    return persons[household.name][index][day]
  }

  def getPersons(Household household, int index, int day, int quarter) {
    return persons[household.name][index][day][quarter]
  }

  def getApplianceOperations(Household household, int index) {
    return appliancesOperations[household.name][index]
  }

  def getApplianceOperations(Household household, int index, int day) {
    return appliancesOperations[household.name][index][day]
  }

  def getApplianceOperations(Household household, int index, int day, int quarter) {
    return appliancesOperations[household.name][index][day][quarter]
  }

  def getApplianceLoads(Household household, int index) {
    return appliancesLoads[household.name][index]
  }

  def getApplianceLoads(Household household, int index, int day) {
    return appliancesLoads[household.name][index][day]
  }

  def getApplianceLoads(Household household, int index, int day, int quarter) {
    return appliancesLoads[household.name][index][day][quarter]
  }

  def getAppliancePossibilityOperations(Household household, int index) {
    return appliancesPossibilityOperations[household.name][index]
  }

  def getAppliancePossibilityOperations(Household household, int index, int day) {
    return appliancesPossibilityOperations[household.name][index][day]
  }

  def getAppliancePossibilityOperations(Household household, int index, int day, int quarter) {
    return appliancesPossibilityOperations[household.name][index][day][quarter]
  }
}

