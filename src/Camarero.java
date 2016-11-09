import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class Camarero extends Agent {
	
	 protected void setup() {
         registerCamarero(getLocalName());

         Random rnd = new Random();
         int wakeTime = (int) (rnd.nextDouble()); //AJUSTAR BIEN EL RANDOM
         blockingReceive(wakeTime);

         addBehaviour(new TickerBehaviour(this, 3000) {
             @Override
             protected void onTick() {
            	 int pasos=0;
                 System.out.println("[tickerbehaviour] "+getLocalName() );
                 
                 switch(pasos){

                     case 1:					
                    	 EnviarMensaje("Le apetece algo de comer");
                    	 break;
                    	 
                     case 2:					
                    	 EnviarMensaje("Le apetece algo de beber");
                         break;
                 }
                 pasos++;
             }
            
			private AID getAID() {
				// TODO Auto-generated method stub
				return null;
			}

         });
	 } 

	 private void EnviarMensaje(String s){
         ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
         msg.setContent(s);
         msg.setSender(this.getAID());
        // partyHost = DFService.search(myAgent, template)[0].getName();
        // msg.addReceiver(partyHost);
         DFAgentDescription template = new DFAgentDescription();
         ServiceDescription sd = new ServiceDescription();
         sd.setType("Guest");
         template.addServices(sd);
         
         try {
			for (DFAgentDescription df : DFService.search(this, template)) {
			     msg.addReceiver(df.getName());
			 }
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

         this.send(msg);
         

     }
	 
    private void registerCamarero(String localName) {
    	DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
	    sd.setType("Camarero");
	    sd.setName(this.getLocalName());
	    dfd.addServices(sd);
		
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
	}

	@Override
    protected void takeDown() {
        System.out.println(getLocalName()+" se marcho de la fiesta");
        this.doDelete();
    }

}
