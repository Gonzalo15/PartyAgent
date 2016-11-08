import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class PartyAgent extends Agent {

    private boolean fiesta = false;
    private boolean lleno = false;
    private String rol;
    private AID partyHost;



    protected void setup() {

        registerAgent();

        Random rnd = new Random();
        int wakeTime = (int) (rnd.nextDouble()); //AJUSTAR BIEN EL RANDOM
        blockingReceive(wakeTime);
        addBehaviour(new WakerBehaviour(this, wakeTime) {
            @Override
            protected void onWake() {

                fiesta = true;
                System.out.println(myAgent.getLocalName() + "[WakerBehaviour] : He llegado a la fiesta");
                myAgent.addBehaviour(new SaludarBehaviour());
                myAgent.addBehaviour(new BienvenidaBehaviour());

            }

        });

        addBehaviour(new LlenaBehaviour());
        

    }





	private void registerAgent() {
		
		if(this.getAID().toString().contains("Host")){
			rol="Host";
		}
		else{
			rol="Guest";
		}
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
	    sd.setType(rol);
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
		Random rnd = new Random();
		if(rnd.nextInt(100)>98){
	        System.out.println(getLocalName()+" se muere de indigestion");
	        this.doDelete();
		}else{
			System.out.println(getLocalName()+" se marcho de la fiesta");
			this.doDelete();
		}
    }

    //HACER UNO QUE HAGA QUE TODOS ESCUCHEN AL ANFITRION CUANDO LA SALA ESTE LLENA
    //UN BEHAVIOUR PARA DEJAR LA FIESTA

    private class SaludarBehaviour extends OneShotBehaviour {
    	
    	private AID[] Invitados;
    	
        @Override
        public void action() {
        	System.out.println(myAgent.getLocalName() + "[WakerBehaviour] : Hola a tod@s");         

            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("Anfitrion");
            template.addServices(sd);

            DFAgentDescription template2 = new DFAgentDescription();
            ServiceDescription sd2 = new ServiceDescription();
            sd2.setType("Invitado");
            template2.addServices(sd2);

            try {

                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setContent("Hola");
                msg.setSender(myAgent.getAID());
                partyHost = DFService.search(myAgent, template)[0].getName();
                msg.addReceiver(partyHost);

                for (DFAgentDescription df : DFService.search(myAgent, template2)) {
                    msg.addReceiver(df.getName());
                }

                myAgent.send(msg);
            } catch (FIPAException fe) {
                fe.printStackTrace();
            }

        }

    }

    private class BienvenidaBehaviour extends SimpleBehaviour {

        @Override
        public void action() {

            MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                    								MessageTemplate.MatchContent("hello"));
            ACLMessage msg = myAgent.receive(mt);
           
            if (msg != null && !msg.getSender().equals(myAgent.getAID())) {
                ACLMessage reply = msg.createReply();
                System.out.println(myAgent.getLocalName() + " [WelcomeBehaviour] : Hola " + msg.getSender().getLocalName());
                reply.setContent("Hola!!");
                myAgent.send(reply);
            }

        }

        @Override
        public boolean done() {
            return false;
        }
     //REGISTRAR LA ENTRADA Y SALIDA DE LAS PERSONAS EN LAS PAGINAS AMARILLAS


    }
}