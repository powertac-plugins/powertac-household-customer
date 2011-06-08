package org.powertac.consumers
import persons.*
import Household.*
import Village.*
import appliances.*

class Consumption {

  int hour
  int day
  int weekday
  int value
  Household household
  Village village

  def initialize(Village v,Household house,int d, int wd,int h, int val){

    village = v
    household = house
    hour = h
    weekday = wd
    day = d
    value = val
  }

  static constraints = {
  }
}
