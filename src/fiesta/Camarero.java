package fiesta;
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
		System.out.println("Agente " + getLocalName() + ": esperando un mensaje REQUEST...");
		ACLMessage mensaje = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
		System.out.println("Agente " + getLocalName() + ": he recibido mensaje REQUEST.");
		System.out.println("Agente " + getLocalName() + ": es hora de trabajar");
		ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
		reply.setContent("a sus ordenes");
		send(reply);

		addBehaviour(new TickerBehaviour(this, 3000) {
			@Override
			protected void onTick() {
				//System.out.println("[tickerbehaviour] " + getLocalName());
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("Guest");
				template.addServices(sd);
//				DFAgentDescription[] listacomida;
//				DFAgentDescription[] listabebida;
				DFAgentDescription[] lista;
				AID aux;

				try {
					// FOR PARA METER TODOS LOS INVITADOS
//					listacomida = DFService.search(myAgent, template);
//					listabebida = DFService.search(myAgent, template);
					lista = DFService.search(myAgent, template);

					if(lista.length==0/*listacomida.length==0 && listabebida.length==0*/){
						System.out.println("[Tickerbehaviour] " + getLocalName()+"Se acabo la fiesta, hora de dormir");
						//ELIMINAR EL COMPORTAMIENTO
					}
					else{
						Random random = new Random();
//						int invitadocomida = (int) (random.nextDouble()* listacomida.length);
//						int invitadobebida = (int) (random.nextDouble()* listabebida.length);
						int invitado = (int) (random.nextDouble()* lista.length);
						
						String[] content={"comer","beber"};
						int eleccion = (int) (random.nextDouble()*2);
						
						ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
						msg.setSender(myAgent.getAID());
						
						msg.addReceiver(lista[invitado].getName());
						
						//FALTA DEFINIR COMO PONER EL CONTENIDO DEL MENSAJE
						
						
						msg.setContent("Le apetece "+ content[eleccion]+" algo?");
						String conver= content[eleccion] + lista[invitado].getName();
						msg.setConversationId(conver);
						System.out.println("[Tickerbehaviour] " + getLocalName()+" : "+lista[invitado].getName().getLocalName()+" Le apetece "+ content[eleccion]+ " algo?");
						myAgent.send(msg);
						MessageTemplate mt = MessageTemplate.MatchConversationId(conver);

						ACLMessage msg2 = myAgent.blockingReceive(mt);

						if (msg2 != null && msg2.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
							ACLMessage reply = msg2.createReply();
							reply.setPerformative(ACLMessage.CONFIRM);
							System.out.println(getLocalName() + " [ServeItemBehaviour] : Aqui tienes "
									+ msg2.getSender().getLocalName());
							myAgent.send(reply);
						}
						else if(msg2 != null && msg2.getPerformative() == ACLMessage.REJECT_PROPOSAL){
							ACLMessage reply = msg2.createReply();
							reply.setPerformative(ACLMessage.CONFIRM);
							System.out.println(getLocalName() + " [ServeItemBehaviour] : No hay de que "
									+ msg2.getSender().getLocalName());
							myAgent.send(reply);
						}

	}
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
			}

			private AID getAID() {
			
				return null;
			}

		});
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
		System.out.println(getLocalName() + " se marchó de la fiesta");
		this.doDelete();
	}

}
