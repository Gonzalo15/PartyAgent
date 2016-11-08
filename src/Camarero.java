import java.util.Random;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAException;

public class Camarero extends Agent {
	
	 protected void setup() {
		 int pasos=0;
         //registerCamarero(getLocalName());

         Random rnd = new Random();
         int wakeTime = (int) (rnd.nextDouble()); //AJUSTAR BIEN EL RANDOM
         blockingReceive(wakeTime);

         addBehaviour(new TickerBehaviour(myAgent, 3000) {
             @Override
             protected void onTick() {
                 System.out.println("[tickerbehaviour] "+getLocalName() );
                 switch(pasos){

                     case 1:
                         try {

                             EnviarMensaje("Le apetece algo de comer");
                         } catch (FIPAException fe) {
                             fe.printStackTrace();
                         }
                         break;


                     case 2:
                         try {

                             EnviarMensaje("Le apetece algo de beber");
                         } catch (FIPAException fe) {
                             fe.printStackTrace();
                         }
                         break;
                 }
                 pasos++;

             }

         });


        void EnviarMensaje(String s){
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContent(s);
            msg.setSender(myAgent.getAID());
           // partyHost = DFService.search(myAgent, template)[0].getName();
           // msg.addReceiver(partyHost);

            for (DFAgentDescription df : DFService.search(myAgent, template2)) {
                msg.addReceiver(df.getName());
            }

            myAgent.send(msg);

        }
	 } 

    @Override
    protected void takeDown() {
        System.out.println(getLocalName()+" se marcho de la fiesta");
        this.doDelete();
    }

}
