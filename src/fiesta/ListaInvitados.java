package fiesta;
import jade.core.AID;
import jade.util.leap.ArrayList;
import jade.util.leap.List;

public class ListaInvitados{

	private static final ArrayList Lista = new ArrayList();
	
	
	public static int numInvitados(){
		return Lista.size();
	}
	
	public static void registrar(AID guest){
		Lista.add(guest);
	}

}
