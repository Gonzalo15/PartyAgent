import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Random;

@SuppressWarnings("serial")
public class Camarero extends Agent {

	protected void setup() {
		registerCamarero(getLocalName());

		Random rnd = new Random();
		ACLMessage msg = this.receive();
		System.out.println("Agente "+getLocalName()+": esperando un mensaje REQUEST...");
		//MessageTemplate m = MessageTemplate.MatchConversationId(msg.getContent());
		ACLMessage mensaje = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
		System.out.println("Agente "+getLocalName()+": he recibido mensaje REQUEST.");
		System.out.println("Agente "+getLocalName()+": es hora de trabajar");
		ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
		reply.addReceiver(msg.getSender());
		reply.setContent("a sus ordenes");
		send(reply);
		
		addBehaviour(new TickerBehaviour(this, 3000) {
			@Override
			protected void onTick() {
				System.out.println("[tickerbehaviour] " + getLocalName());

					EnviarMensaje("Le apetece algo de comer");
					EnviarMensaje("Le apetece algo de beber");
			}

			private AID getAID() {
				// TODO Auto-generated method stub
				return null;
			}

		});
	}

	private void EnviarMensaje(String s) {
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
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

	}

	@Override
	protected void takeDown() {
		System.out.println(getLocalName() + " se march� de la fiesta");
		this.doDelete();
	}

}
