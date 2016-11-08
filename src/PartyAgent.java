import java.util.Scanner;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;

public class PartyAgent extends Agent {
	
	 protected void setup() {
		 System.out.println("Hola! PartyAgent "+getAID().getName()+" esta listo.");
		 DFAgentDescription host = new DFAgentDescription();
		 
		 
	 } 
	 
	 private class Saludar extends Behaviour{

		@Override
		public void action() {
			
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		 
	 }
}
