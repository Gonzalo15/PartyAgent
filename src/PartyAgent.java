import java.security.acl.Acl;
import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.DFAgentDscDlg;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class PartyAgent extends Agent {

    private static String rol;
    private boolean comienzo= false;
    private int hambre;
    private int sed;
    private AID partyHost;



    protected void setup() {

    	registerList();
    	Random rnd = new Random();
    	hambre = (int) (rnd.nextDouble()* 10);
    	sed = (int) (rnd.nextDouble()*10);
    	
    	if(this.getAID().toString().contains("Host")){
    		registerAgent("Host");
    		partyHost=this.getAID();
    		addBehaviour(new LlenaBehaviour());
    	}
    	else{
    		int wakeTime = (int) (rnd.nextDouble()* 99 + 1); //Random de 1 a 100 ( el 99 es el numero de elementeos y el 1 el primer numero
    		blockingReceive(wakeTime);
    		addBehaviour(new WakerBehaviour(this, wakeTime) {
    			@Override
    			protected void onWake() {
    				System.out.println(myAgent.getLocalName() + "[WakerBehaviour] : He llegado a la fiesta");
    				myAgent.addBehaviour(new SaludarBehaviour());
    				myAgent.addBehaviour(new BienvenidaBehaviour());
    				
    			}
    		});
    		System.out.println("Agente "+getLocalName()+": esperando a que de coomienzo la fiesta...");
    		
    		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchContent("Disfrutad de la fiesta!"), 
    							 MessageTemplate.MatchSender(partyHost));
    		
    		ACLMessage mensaje = blockingReceive(mt);
    		this.addBehaviour(new Comer());
    		this.addBehaviour(new Beber());
    	}
        	
    }


	public static String getRol() {
		return rol;
	}

	private void registerList() {
		ListaInvitados.registrar(getAID());
		
	}

	private void registerAgent(String rol) {
		
		this.rol=rol;
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

	private class LlenaBehaviour extends SimpleBehaviour{

		@Override
		public void action() {
			 
			DFAgentDescription template = new DFAgentDescription();
			DFAgentDescription templateC = new DFAgentDescription();
			
	        ServiceDescription sd = new ServiceDescription();
	        ServiceDescription sdC = new ServiceDescription();
	        
	        sd.setType("Guest");
	        sdC.setType("Camarero");
	        
	        template.addServices(sd);
	        templateC.addServices(sdC);
	        
	        DFAgentDescription[] listaG;
	        DFAgentDescription[] listaC;
	        AID aux;
	        
	        try {
	          	listaG = DFService.search(myAgent, template);
	          	listaC = DFService.search(myAgent, templateC);
	          	
	          	if (ListaInvitados.numInvitados()== listaG.length && rol.equals("Host")){
	          		
	          		ACLMessage msgC= new ACLMessage(ACLMessage.REQUEST);
	          		ACLMessage msgG= new ACLMessage(ACLMessage.INFORM);
	          		
	          		for (int i=0; i<listaC.length; i++){
	          			aux=listaC[i].getName();
	          			msgC.addReceiver(aux);
	          		}  
	          		for (int i=0; i<listaG.length; i++){
	          			aux=listaG[i].getName();
	          			msgG.addReceiver(aux);
	          		} 
	          		msgC.setContent("Camareros!!!");
	          		msgG.setContent("Disfrutad de la fiesta!");
	          		comienzo=true;
	          	}
	        }
	         catch (FIPAException fe) {
	             fe.printStackTrace();
	        }
	       
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return comienzo;
		}
		
	}
	
    private class SaludarBehaviour extends OneShotBehaviour {
    	
    	private AID[] Invitados;
    	
        @Override
        public void action() {
        	registerAgent("Guest");
        	System.out.println(myAgent.getLocalName() + "[OneShootBehaviour] : Hola a tod@s");         

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
    }
    
    private class Comer extends Behaviour{
    	MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
							 MessageTemplate.MatchContent("Le apetece comer algo?"));
    						
    	private boolean lleno=false;
		@Override
		public void action() {
			
			while (hambre>0){
				ACLMessage msg= myAgent.receive(mt);
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
				reply.setContent("Si por favor");
				myAgent.send(reply);
				
				hambre--;
			}
			lleno=true;
			
		}

		@Override
		public boolean done() {
			return lleno;
		}
		//	
    }
    
    private class Beber extends Behaviour{
    	MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				 			 MessageTemplate.MatchContent("Le apetece beber algo?"));
				
    	private boolean saciado=false;
		@Override
		public void action() {
			while (sed>0){
				ACLMessage msg= myAgent.receive(mt);
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
				reply.setContent("Si por favor");
				myAgent.send(reply);
				
				sed--;
			}
			saciado=true;
			
		}

		@Override
		public boolean done() {
			return saciado;
		}
    	
    }
    
}