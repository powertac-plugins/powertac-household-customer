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

  Map persons = [:]
  Map appliancesOperations = [:]
  Map appliancesLoads

  void createPersonsMap (Household household, int persons) {
    log.info "create consumption map for Household Customer ${village.id} [${types}]"
    persons[household.name] = new BigDecimal[persons][64][96]
  }

  void createAppliancesOperationsMap (Household household, int appliances) {
    log.info "create consumption map for Household Customer ${village.id} [${types}]"
    appliancesOperations[household.name] = new BigDecimal[appliances][64][96]
  }

  void createAppliancesLoadsMap (Household household, int appliances) {
    log.info "create consumption map for Household Customer ${village.id} [${types}]"
    appliancesLoads[household.name] = new BigDecimal[appliances][64][96]
  }

  void setPerson(Household household, int index, int day, int quarter, Status status) {
    def consumptionMap = consumptions[household.name]
    if (consumptionMap == null) {
      log.error "could not find Persons map for household ${household.toString()}"
      return
    }
    consumptionMap[index][day][quarter] = status
  }

  void setApplianceOperation(Household household, int index, int day, int quarter, Boolean function) {
    def consumptionMap = consumptions[household.name]
    if (consumptionMap == null) {
      log.error "could not find Appliance Operation map for household ${household.toString()}"
      return
    }
    consumptionMap[index][day][quarter] = status
  }

  void setApplianceLoad(Household household, int index, int day, int quarter, BigDecimal value) {
    def consumptionMap = consumptions[household.name]
    if (consumptionMap == null) {
      log.error "could not find Appliance Load map for Household ${household.toString()}"
      return
    }
    consumptionMap[index][day][quarter] = value
  }

  def getPersons(Household household, int index, int day, int quarter) {
    return persons[household.name][index][day][quarter]
  }

  def getApplianceOperations(Household household, int index, int day, int quarter) {
    return persons[household.name][index][day][quarter]
  }

  def getApplianceLoads(Household household, int index, int day, int quarter) {
    return persons[household.name][index][day][quarter]
  }
}

