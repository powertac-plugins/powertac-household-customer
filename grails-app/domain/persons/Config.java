package persons;

import java.io.*;
import java.util.*;

public class Config {

	public Scanner x;
	
	public HashMap hm = new HashMap();

	
	public void readConf() throws FileNotFoundException{
		
		x = new Scanner(new File("../powertac-household-customer/config.txt"));
		
		
		this.findVariable("PublicVacationDuration");
		this.findVariable("NumberOfHouses");
		this.findVariable("NumberOfVillages");
		
		this.findVariable("OnePersonConsumption");
		this.findVariable("TwoPersonsConsumption");
		this.findVariable("ThreePersonsConsumption");
		this.findVariable("FourPersonsConsumption");
		this.findVariable("FivePersonsConsumption");
		
		this.findVariable("OnePerson");
		this.findVariable("TwoPersons");
		this.findVariable("ThreePersons");
		this.findVariable("FourPersons");
		this.findVariable("FivePersons");
		
		
		this.findVariable("PeriodicPresent");
		this.findVariable("MostlyPresent");
		this.findVariable("RandomlyAbsent");
		
		
		this.findVariable("SicknessMean");
		this.findVariable("SicknessDev");

		
		this.findVariable("PPLeisure");
		this.findVariable("MPLeisure");
		this.findVariable("RALeisure");
		this.findVariable("LeisureDev");
		
		this.findVariable("LeisureDurationMean");
		this.findVariable("LeisureDurationDev");
		
		
		this.findVariable("OneDay");
		this.findVariable("TwoDays");
		this.findVariable("ThreeDays");
		this.findVariable("FourDays");
		this.findVariable("FiveDays");
		this.findVariable("SixDays");
		this.findVariable("SevenDays");
		
		this.findVariable("WorkingDurationMean");
		this.findVariable("WorkingDurationDev");
		
		this.findVariable("VacationDurationMean");
		this.findVariable("VacationDurationDev");
		this.findVariable("VacationAbsence");
		
		this.findVariable("RefrigeratorSaturation");
		
		this.findVariable("FreezerSaturation");
		
		this.findVariable("DishwasherSaturation");
		this.findVariable("DishwasherWeeklyTimes");
		
		this.findVariable("StoveSaturation");
		this.findVariable("StoveDailyTimes");

		this.findVariable("WashingMachineSaturation");
		this.findVariable("WashingMachineWeeklyTimes");

		this.findVariable("DryerSaturation");
		this.findVariable("DryerWeeklyTimes");

		this.findVariable("ConsumerElectronicsSaturation");
		this.findVariable("ConsumerElectronicsDailyTimes");

		this.findVariable("ICTSaturation");
		this.findVariable("ICTDailyTimes");

		this.findVariable("WaterHeaterSaturation");
		this.findVariable("InstantHeater");
		this.findVariable("StorageHeater");
		this.findVariable("InstantHeaterDailyTimes");

		this.findVariable("CirculationPumpSaturation");
		this.findVariable("CirculationPumpPercentage");

		this.findVariable("SpaceHeaterSaturation");
		this.findVariable("SpaceHeaterPercentage");
		
		this.findVariable("LightsDailyTimes");
		
		this.findVariable("OthersDailyTimes");
		
		
		x.close();
	}
	
	public void findVariable(String str) throws FileNotFoundException{
		
		String test1 = "";
		String test2 = "";
		String test3 = "";
		
		while (x.hasNext()){
			
			test1 = x.next();
			
			if (test1.equals(str)) {
				
				test2 = x.next();
				test3 = x.next();
				hm.put(str, new Float(test3));
						
			
				break;
			}
		}
		
		if (test3.equals("")) {
			
			System.out.println("Variable " + str + " not found in file");
		}
		
	}
	
	public void showContents(){
		
		Set set = hm.entrySet();
		Iterator it = set.iterator();
		
		while (it.hasNext()) {

            // This is a task.
            Map.Entry me = (Map.Entry)it.next();
            System.out.println(me.getKey() + " : " + me.getValue() );

        }
		
	}
	
}